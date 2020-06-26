package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.IOUtil;

public class ArrayElementParser<T> implements ElementParser<List<T>> {
	private final ElementParser<T> element;

	public ArrayElementParser(ElementParser<T> element) {
		this.element = element;
	}

	@Override
	public List<T> parse(Reader reader) throws IllegalArgumentException, IOException {
		if(reader.read() != '[')
			throw new IllegalArgumentException("Arrays must start with '['!");
		List<T> list = new ArrayList<>();
		while (true) {
			IOUtil.skipWhitespace(reader);
			list.add(this.element.parse(reader));
			IOUtil.skipWhitespace(reader);
			int next = reader.read();
			if(next == ']')
				break;
			else if(next != ',') {
				throw new IllegalArgumentException("Arrays must be separated by ','! found: " + (char)next);
			}
		}

		return list;
	}
}
