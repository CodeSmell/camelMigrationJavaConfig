package codesmell.util;

import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * fluent logging utility to help
 * with standardized logging
 */
public class LogFormatUtil {
    private final Logger logger;
    private String message;
    private Throwable throwable;
    private Map<String, Object> structuredArgs;

    /**
     * logger builder
     */
    public static LogFormatUtil logger(Logger logger) {
        return new LogFormatUtil(logger);
    }

    /**
     * log the message with a throwable
     *
     * @param throwable
     */
    public LogFormatUtil withThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    /**
     * log the message
     *
     * @param message
     */
    public LogFormatUtil withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * log the message with params
     *
     * @param message
     * @param messageParams
     * @return
     */
    public LogFormatUtil withMessage(String message, Object... messageParams) {
        this.message = String.format(message, messageParams);
        return this;
    }

    /**
     * log the message with structured arguments.
     * Multiple invocations of this method will overwrite existing structured args unless appendToOtherArgs is set to true
     *
     * @param structuredArgs
     * @param appendToOtherArgs
     * @return
     */
    public LogFormatUtil withStructuredArguments(Map<String, Object> structuredArgs, boolean appendToOtherArgs) {
        if (appendToOtherArgs && this.structuredArgs != null) {
            if (structuredArgs != null) {
                this.structuredArgs.putAll(structuredArgs);
            }
        } else {
            this.structuredArgs = structuredArgs;
        }
        return this;
    }

    /**
     * log the message with structured arguments.
     * This method will overwrite existing structured args
     *
     * @param structuredArgs
     * @return
     */
    public LogFormatUtil withStructuredArguments(Map<String, Object> structuredArgs) {
        this.withStructuredArguments(structuredArgs, false);
        return this;
    }

    /**
     * log the message with structured arguments
     *
     * @param key
     * @param value
     * @return
     */
    public LogFormatUtil withStructuredArgument(String key, Object value) {
        if (this.structuredArgs == null) {
            this.structuredArgs = new HashMap<>();
        }
        structuredArgs.put(key, value);
        return this;
    }

    /**
     * log with level
     *
     * @param logLevel
     */
    public void andLog(Level logLevel) {
        switch (logLevel) {
            case TRACE:
                if (logger.isTraceEnabled()) {
                    logger.trace(message, StructuredArguments.entries(structuredArgs), throwable);
                }
                break;
            case DEBUG:
                if (logger.isDebugEnabled()) {
                    logger.debug(message, StructuredArguments.entries(structuredArgs), throwable);
                }
                break;
            case INFO:
                if (logger.isInfoEnabled()) {
                    logger.info(message, StructuredArguments.entries(structuredArgs), throwable);
                }
                break;
            case WARN:
                if (logger.isWarnEnabled()) {
                    logger.warn(message, StructuredArguments.entries(structuredArgs), throwable);
                }
                break;
            case ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(message, StructuredArguments.entries(structuredArgs), throwable);
                }
                break;
            default:
                break;
        }
    }

    private LogFormatUtil(Logger logger) {
        // you can't make me
        this.logger = logger;
    }
}
