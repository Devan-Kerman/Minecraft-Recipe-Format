package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import com.mojang.datafixers.util.Either;
import net.devtech.mcrf.elements.ElementParser;

import net.minecraft.util.Identifier;

public class TagOrOtherElementParser<T> implements ElementParser<Either<Identifier, T>> {
	private final ElementParser<T> other;

	public TagOrOtherElementParser(ElementParser<T> other) {this.other = other;}

	@Override
	public Either<Identifier, T> parse(Reader reader) throws IllegalArgumentException, IOException {
		reader.mark(1);
		int read = reader.read();
		if(read == '#') {
			return Either.left(IDENTIFIER.parse(reader));
		} else {
			reader.reset();
			return Either.right(this.other.parse(reader));
		}
	}
}
