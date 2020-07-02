package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import net.devtech.mcrf.elements.ElementParser;

/**
 * parses normal integers, with hex and binary support with the prefixes 'Ox' and '0b' respectively
 */
public interface IntegerElementParser<T> extends ElementParser<T> {
	char[] HEX_HEADER = "0x".toCharArray();
	char[] BINARY_HEADER = "0b".toCharArray();

	@Override
	default T parse(Reader reader) throws IllegalArgumentException, IOException {
		int base;
		char[] header = new char[2];
		reader.mark(2);
		int read = reader.read(header);
		if(read == HEX_HEADER.length && Arrays.equals(header, HEX_HEADER)) {
			base = 16;
		} else if(read == BINARY_HEADER.length && Arrays.equals(header, BINARY_HEADER)) {
			base = 2;
		} else {
			base = 10;
			reader.reset();
		}

		StringBuilder builder = new StringBuilder();
		while (true) {
			reader.mark(1);
			int chr = reader.read();
			if(valid(chr, base)) {
				builder.append((char)chr);
			} else {
				reader.reset();
				break;
			}
		}

		return this.from(builder.toString(), base);
	}

	static boolean valid(int chr, int base) {
		if(base == 2)
			return chr == '1' | chr == '0';
		else if(base == 10)
			return Character.isDigit(chr);
		else if(base == 16) {
			return Character.isDigit(chr) | (chr >= 'A' & chr <= 'F') | (chr >= 'a' & chr <= 'f');
		}
		throw new UnsupportedOperationException("base" + base + " not supported!");
	}



	T from(String string, int base);
}
