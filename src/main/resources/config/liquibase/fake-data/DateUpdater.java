///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS info.picocli:picocli:4.6.3
//DEPS ch.qos.reload4j:reload4j:1.2.24
//DEPS com.opencsv:opencsv:5.7.1

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(name = "DateUpdater", mixinStandardHelpOptions = true, version = "DateUpdater 0.1", description = "DateUpdater made with jbang")
class DateUpdater implements Callable<Integer> {

    static final Logger logger = Logger.getLogger(DateUpdater.class);

    @Spec
    CommandSpec spec;

    private int month = LocalDateTime.now().getMonthValue();

    @Option(names = { "--month", "-m" }, description = "The month of records (between 1-12)")
    public void setMonth(int value) {
        if (value <= 0 || value > 12) {
            throw new ParameterException(
                spec.commandLine(),
                //The value '13' for option '-m or --month' is invalid: the value should be between 1 and 12.
                String.format("The value '%s' for option '-m or --month' is invalid: " + "the value should be between 1 and 12.", value)
            );
        }
        month = value;
    }

    public static void main(String... args) {
        BasicConfigurator.configure();
        int exitCode = new CommandLine(new DateUpdater()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try (Stream<Path> stream = Files.walk(Paths.get("."), 1)) {
            stream
                .filter(file -> !Files.isDirectory(file) && file.getFileName().toString().endsWith("csv"))
                .forEach(path -> {
                    try (Reader reader = Files.newBufferedReader(path)) {
                        CSVParser parser = new CSVParserBuilder().withSeparator(';').withIgnoreQuotations(true).build();
                        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(0).withCSVParser(parser).build();
                        var lines = csvReader.readAll();
                        if (!lines.isEmpty() && Arrays.toString(lines.get(0)).contains("created_at")) {
                            lines
                                .subList(1, lines.size())
                                .forEach(line -> {
                                    LocalDateTime today = LocalDateTime.now();
                                    LocalDateTime date = LocalDateTime.parse(line[line.length - 1]);
                                    LocalDateTime adjustedDate = date.withYear(today.getYear()).withMonth(month);
                                    line[line.length - 1] = adjustedDate.toString();
                                });
                        }
                        try (
                            CSVWriter writer = new CSVWriter(
                                new FileWriter(path.toString()),
                                ';',
                                CSVWriter.NO_QUOTE_CHARACTER,
                                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                CSVWriter.DEFAULT_LINE_END
                            )
                        ) {
                            lines.forEach(writer::writeNext);
                        }
                    } catch (IOException | CsvException ex) {
                        logger.error(ex);
                    }
                    logger.info(path.getFileName() + " has been updated.");
                });
        } catch (IOException ex) {
            logger.error(ex);
        }
        return 0;
    }
}
