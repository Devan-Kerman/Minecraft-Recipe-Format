package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;

/**
 * an optional element, if '*' is found, it's considered not present
 */
public class WildcardOrOtherElementParser<T> implements ElementParser<Optional<T>> {
	private final ElementParser<T> other;

	public WildcardOrOtherElementParser(ElementParser<T> other) {this.other = other;}


	@Override
	public Optional<T> parse(Reader reader) throws IllegalArgumentException, IOException {
		if(MCRFUtil.peek(reader) == '*') {
			reader.read();
			return Optional.empty();
		}
		return Optional.of(this.other.parse(reader));
	}
}
