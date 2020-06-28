package net.devtech.mcrf.util.io;

import java.io.IOException;
import java.io.Reader;

public class LineTrackingReader extends Reader {
	private final Reader reader;

	public LineTrackingReader(Reader reader) {
		this.reader = reader;
	}

	private int ln, markLn;
	private StringBuilder line = new StringBuilder();
	private String markLine;

	@Override
	public boolean markSupported() {
		return this.reader.markSupported();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		this.markLine = this.line.substring(0);
		this.markLn = this.ln;
		this.reader.mark(readAheadLimit);
	}

	@Override
	public void reset() throws IOException {
		this.ln = this.markLn;
		this.line = new StringBuilder(this.markLine);
		this.reader.reset();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int read = this.reader.read(cbuf, off, len);
		for (int i = 0; i < read; i++) {
			this.processLn(cbuf[off + i]);
		}
		return read;
	}

	private void processLn(char chr) {
		if(chr == '\n') {
			this.ln++;
			this.line.setLength(0);
		} else {
			this.line.append(chr);
		}
	}

	@Override
	public void close() throws IOException {
		this.reader.close();
	}

	public int getLineNumber() {
		return this.ln;
	}

	public String getLine() {
		return this.line.toString();
	}
}
