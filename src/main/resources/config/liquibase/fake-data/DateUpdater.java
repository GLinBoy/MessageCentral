///usr/bin/env jbang "$0" "$@" ; exit $?
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
import picocli.CommandLine.Parameters;

@Command(name = "DateUpdater", mixinStandardHelpOptions = true, version = "DateUpdater 0.1", description = "DateUpdater made with jbang")
class DateUpdater implements Callable<Integer> {

    static final Logger logger = Logger.getLogger(DateUpdater.class);

    @Parameters(index = "0", description = "The greeting to print", defaultValue = "World!")
    private String greeting;

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
                                    LocalDateTime adjustedDate = date.withYear(today.getYear()).withMonth(today.getMonthValue());
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
