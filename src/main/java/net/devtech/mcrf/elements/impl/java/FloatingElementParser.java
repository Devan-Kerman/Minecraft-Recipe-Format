package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;

public interface FloatingElementParser<T> extends ElementParser<T> {
	@Override
	default T parse(Reader reader) throws IllegalArgumentException, IOException {
		StringBuilder builder = new StringBuilder();
		while (true) {
			reader.mark(1);
			int chr = reader.read();
			if (valid(chr)) {
				builder.append((char) chr);
			} else {
				reader.reset();
				break;
			}
		}

		return this.from(builder.toString());
	}

	static boolean valid(int chr) {
		return Character.isDigit(chr) | chr == '-' | chr == '.';
	}


	T from(String string);
}
