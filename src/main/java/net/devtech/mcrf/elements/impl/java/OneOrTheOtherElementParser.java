package net.devtech.mcrf.elements.impl.java;

import java.io.IOException;
import java.io.Reader;

import com.mojang.datafixers.util.Either;
import net.devtech.mcrf.elements.ElementParser;

/**
 * a parser that can be one or the other objects, based on a condition
 */
public class OneOrTheOtherElementParser<A, B> implements ElementParser<Either<A, B>> {
	private final ATester aTest;
	private final ElementParser<A> aParser;
	private final ElementParser<B> bParser;
	public interface ATester {
		boolean test(Reader reader) throws IOException;
	}

	public OneOrTheOtherElementParser(ATester aTest, ElementParser<A> parser, ElementParser<B> bParser) {
		this.aTest = aTest;
		this.aParser = parser;
		this.bParser = bParser;
	}

	@Override
	public Either<A, B> parse(Reader reader) throws IllegalArgumentException, IOException {
		if(this.aTest.test(reader)) {
			return Either.left(this.aParser.parse(reader));
		}
		return Either.right(this.bParser.parse(reader));
	}
}
