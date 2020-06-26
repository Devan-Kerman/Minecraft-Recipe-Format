package net.devtech.mcrf.util;

import java.io.IOException;
import java.io.Reader;

public final class IOUtil {
	public static void skipWhitespace(Reader reader) throws IOException {
		int curr;
		while (true) {
			reader.mark(1);
			curr = reader.read();
			if (curr == -1) {
				break;
			}
			if (!Character.isWhitespace(curr)) {
				reader.reset();
				break;
			}
		}
	}

	public static String readBetween(Reader reader, char start, char end) throws IOException {
		reader.mark(1);
		if(reader.read() != start) {
			reader.reset();
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(start);
		int starts = 1;
		int chr;
		while ((chr = reader.read()) != -1) {
			builder.append((char)chr);
			if(chr == start) {
				starts++;
				break;
			}

			if(chr == end) {
				starts--;
				if(starts == 0)
					break;
			}
		}
		return builder.toString();
	}
}
