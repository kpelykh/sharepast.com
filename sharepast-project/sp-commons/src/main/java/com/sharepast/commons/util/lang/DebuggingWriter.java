package com.sharepast.commons.util.lang;

import org.slf4j.Logger;

import java.io.StringWriter;

/**
 * A StringWriter that also sends its output to a log stream
 * @author Konstantin Pelykh
 */
public class DebuggingWriter extends StringWriter
{

    final public static String lineSeparator = System.getProperty("line.separator");

    private final Logger logger;

    private String prefix;

    protected final StringBuffer buffer = new StringBuffer();

    public DebuggingWriter(String prefix, Logger log) {
        this.prefix = prefix;
        this.logger = log;
    }

    @Override
    public void write(String s) {
        super.write(s);
        buffer.append(s);
        if (s.equals(lineSeparator)) {
            printBuffer();
        }
    }

    /**
     * Write the characters in the print buffer out to the stream
     */
    private void printBuffer()
    {
        if (buffer.length() > 0)
        {
            logger.info(prefix + buffer.toString());
            buffer.setLength(0);
        }
    }

}
