package org.javadynamicanalyzer.gui;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream{
	private final JTextArea textArea;
    private StringBuilder buffer;


	public TextAreaOutputStream(JTextArea ta) {
		// TODO Auto-generated constructor stub
		this.textArea = ta;
	}

	public void close() {
            buffer.setLength(0);
    }

    /**
     * Flush the data that is currently in the buffer.
     * 
     * @throws IOException
     */

    public void flush() throws IOException {
            textArea.append(getBuffer().toString());
            if (System.getProperty("java.version").startsWith("1.1")) {
                    textArea.append("\n");
            }
            textArea.setCaretPosition(textArea.getDocument().getLength());
            buffer = null;
    }

    protected StringBuilder getBuffer() {
            if (buffer == null) {
                    buffer = new StringBuilder();
            }
            return buffer;
    }

    @Override
    public void write(int b) throws IOException {
            getBuffer().append((char)b);
    }

}
