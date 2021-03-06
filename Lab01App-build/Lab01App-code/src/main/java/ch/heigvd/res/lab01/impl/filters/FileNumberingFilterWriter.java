/*
 -----------------------------------------------------------------------------------
 Course       : RES
 Laboratory   : Labo-01
 File         : FileNumberingFilterWriter.java
 Author       : Olivier Liechti, Guillaume Serneels
 Date         : 13.03.2016
 But          : File numbering filter to decorate the writer of the quote fetching 
                and treatment application
 -----------------------------------------------------------------------------------
 */
package ch.heigvd.res.lab01.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

    private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());

    //Our lines counter is an int representing a char, we will increment it and print it
    private int linesCpt;

    //Are we currently starting a new line with a '\r'(necessary for writing int by int)
    private static boolean newLineWithBackslashR = true;

    public FileNumberingFilterWriter(Writer out) {
        super(out);
        //linesCpt = 1;

    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        //Save the offset
        int i = off;

        //Write the first line when necessary
        if (linesCpt == 0) {
            linesCpt++;
            super.write(Integer.toString(linesCpt));
            super.write('\t');
            
        }
        //Write each char with appropriate processing of new lines
        for (int lengthRemaining = len; lengthRemaining > 0; lengthRemaining--) {
            super.write(str.charAt(i));
            if (str.charAt(i) == '\n' || str.charAt(i) == '\r') {

                if (str.charAt(i) == '\r' && (i + 1) <= str.length() && str.charAt(i + 1) == '\n') {
                    i++;
                    lengthRemaining--;
                    super.write(str.charAt(i));
                }
                linesCpt++;
                super.write(Integer.toString(linesCpt));
                super.write('\t');
                //Since we wrote a new line, we have to increment the lines counter
                
            }
            i++;
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        String upperCased = String.valueOf(cbuf).toUpperCase();
        write(upperCased, off, len);

    }

    @Override
    public void write(int c) throws IOException {
        //Write the first line when necessary
        if (linesCpt == 0) {
            linesCpt++;
            super.write(Integer.toString(linesCpt));
            super.write('\t');
            
        }

        //Handling of a potential windows new line feed (\r\n)
        if (newLineWithBackslashR) {
            linesCpt++;
            if (c == '\n') {
                super.write(c);
                super.write(Integer.toString(linesCpt));
                super.write('\t');
            } else {
                //After all, it was only a MacOSX new line feed (\r)
                super.write(Integer.toString(linesCpt));
                super.write('\t');
                super.write(c);
            }
            newLineWithBackslashR = false;
            

        } else if (c == '\r') {
            super.write(c);
            newLineWithBackslashR = true;
        } else if (c == '\n') {
            //Handling a Unix new line feed (\n)
            super.write(c);
            linesCpt++;
            super.write(Integer.toString(linesCpt));
            super.write('\t');
            
        } else {
            super.write(c);
        }
    }
}
