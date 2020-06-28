package net.devtech.mcrf.util.io;

import java.io.IOException;
import java.io.Reader;

/**
 * eliminates commented lines and return carriages
 */
public class CommentStrippingReader extends Reader {
	public static final char COMMENT = '%';
	private int next = -1;
	private final Reader source;

	public CommentStrippingReader(Reader source) {this.source = source;}

	@Override
	public int read() throws IOException {
		int next = this.next;
		if(next != -1) {
			this.next = -1;
		} else {
			next = this.source.read();
		}

		if(next == '\\') {
			int val = this.source.read();
			if(val == COMMENT) { // escaped comment
				return '%';
			}
			return val;
		} else if(next == COMMENT) {
			int c;
			while ((c = this.source.read()) != -1) {
				if(c == '\r') {
					// skip newline
					this.source.read();
					return '\n';
				} else if(c == '\n') {
					return '\n';
				}
			}

			return -1;
		}

		return next;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int i, c;
		for (i = 0; i < len && (c = this.read()) != -1; i++) {
			cbuf[i + off] = (char) c;
		}
		int n = this.source.read();
		if(n == -1 && i == 0)
			return -1;
		this.next = n;
		return i;
	}

	@Override
	public void close() throws IOException {
		this.source.close();
	}
}
