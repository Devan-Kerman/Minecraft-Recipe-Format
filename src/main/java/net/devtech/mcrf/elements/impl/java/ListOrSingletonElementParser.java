package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;

/**
 * an element that is either an array, or a singleton, but the type in both cases is still a list
 */
public class ListOrSingletonElementParser<T> implements ElementParser<List<T>> {
	private final ElementParser<T> parser;

	public ListOrSingletonElementParser(ElementParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public List<T> parse(Reader reader) throws IllegalArgumentException, IOException {
		if(MCRFUtil.peek(reader) == '[') {
			return ElementParser.list(this.parser).parse(reader);
		} else return Collections.singletonList(this.parser.parse(reader));
	}
}
