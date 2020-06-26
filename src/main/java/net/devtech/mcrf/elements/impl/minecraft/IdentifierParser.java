package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;

import net.minecraft.util.Identifier;

public class IdentifierParser implements ElementParser<Identifier> {
	@Override
	public Identifier parse(Reader reader) throws IllegalArgumentException, IOException {
		StringBuilder builder = new StringBuilder();
		while(true) {
			reader.mark(1);
			int chr = reader.read();
			if(chr == -1)
				break;

			if(Identifier.isCharValid((char) chr)) {
				builder.append((char) chr);
			} else {
				reader.reset();
				break;
			}
		}
		return new Identifier(builder.toString());
	}
}
