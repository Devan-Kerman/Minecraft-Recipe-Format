package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;

public class StringElementParser implements ElementParser<String> {
	public static final StringElementParser INSTANCE = new StringElementParser();
	private StringElementParser() {}

	@Override
	public String parse(Reader reader) throws IllegalArgumentException, IOException {
		StringBuilder builder = new StringBuilder();
		boolean escape = false;
		while (true) {
			int chr = reader.read();
			if(chr == '\\') {
				escape = true;
				continue;
			}
			if(escape | !(chr == '\n' | chr == ',' | chr == '+' | chr == -1 | chr == '-')) {
				builder.append(chr);
			} else {
				break;
			}
			escape = false;
		}

		return builder.toString();
	}
}
