///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.6.3
//DEPS ch.qos.reload4j:reload4j:1.2.24

import java.util.concurrent.Callable;
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
    public Integer call() throws Exception { // your business logic goes here...
        logger.info("Hello " + greeting);
        return 0;
    }
}
