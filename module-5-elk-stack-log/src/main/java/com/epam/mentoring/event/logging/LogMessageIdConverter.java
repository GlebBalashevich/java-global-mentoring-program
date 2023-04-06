package com.epam.mentoring.event.logging;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

@Plugin(name = "MessageIdPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys(value = "msgId")
public class LogMessageIdConverter extends LogEventPatternConverter {

    private static final String MESSAGE_ID_REGEX = "(\\d{2})(\\d{2})(\\d+)";

    private static final String MESSAGE_ID_FORMAT = "$1-$2-$3";

    private final AtomicLong sqn;

    protected LogMessageIdConverter(String name, String style) {
        super(name, style);
        sqn = new AtomicLong();
    }

    public static LogMessageIdConverter newInstance(String[] options) {
        return new LogMessageIdConverter("Message Id converter", "msgId");
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        final var messageId = String.format("%09d", sqn.incrementAndGet());
        toAppendTo.append(messageId.replaceFirst(MESSAGE_ID_REGEX, MESSAGE_ID_FORMAT));
    }

}
