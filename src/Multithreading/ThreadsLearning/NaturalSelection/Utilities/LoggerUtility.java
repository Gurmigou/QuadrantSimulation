package Multithreading.ThreadsLearning.NaturalSelection.Utilities;

import java.util.logging.Logger;

/**
 *  A logging class.
 */
public class LoggerUtility {

    /**
     *  Logger field;
     */
    private final Logger logger;

    /**
     *  There are two different types of loggers:
     *      1) Logger that writes logs in console;
     *      2) Logger that writes logs in file;
     */
    public enum LoggerType {
        FILE_LOGGER
    }

    /* Constructors */
    public LoggerUtility(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    public LoggerUtility(String loggerName, LoggerType loggerType) {
        if (loggerType == LoggerType.FILE_LOGGER)  {
            System.setProperty("java.util.logging.config.file",
                                "D:/IT/Java/src/Multithreading/ThreadsLearning/" +
                                "NaturalSelection/Utilities/logging.properties");
        }
        this.logger = Logger.getLogger(loggerName);
    }

    /* Getter */
    public Logger logger() {
        return this.logger;
    }
}
