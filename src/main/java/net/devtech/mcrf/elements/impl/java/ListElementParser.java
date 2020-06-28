package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;

public class ListElementParser<T> implements ElementParser<List<T>> {
	private final ElementParser<T> element;

	public ListElementParser(ElementParser<T> element) {
		this.element = element;
	}

	@Override
	public List<T> parse(Reader reader) throws IllegalArgumentException, IOException {
		if(reader.read() != '[')
			throw new IllegalArgumentException("Arrays must start with '['!");
		List<T> list = new ArrayList<>();
		while (true) {
			MCRFUtil.skipWhitespace(reader);
			list.add(this.element.parse(reader));
			MCRFUtil.skipWhitespace(reader);
			int next = reader.read();
			if(next == ']')
				break;
			else if(next == -1)
				throw new IllegalStateException("reached end of file!");
			else if(next != ',') {
				throw new IllegalArgumentException("Arrays must be separated by ','! found: " + (char)next);
			}
		}

		return list;
	}
}
