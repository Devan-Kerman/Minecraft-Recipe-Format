package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;

/**
 * an element that may be a list, or just another object
 */
public class ListOrOtherElementParser<T, L> implements ElementParser<Object> {
	private final ElementParser<?> list;
	private final ElementParser<T> type;

	public ListOrOtherElementParser(ElementParser<T> type) {
		if(type instanceof ListElementParser | type instanceof ListOrOtherElementParser) {
			throw new IllegalArgumentException("type cannot be a list!");
		}
		this.type = type;
		this.list = ElementParser.list(type);
	}

	public ListOrOtherElementParser(ElementParser<List<L>> list, ElementParser<T> type) {
		this.list = list;
		this.type = type;
	}

	@Override
	public Object parse(Reader reader) throws IllegalArgumentException, IOException {
		if(MCRFUtil.peek(reader) == '[') {
			return this.list.parse(reader);
		}
		return this.type.parse(reader);
	}
}
