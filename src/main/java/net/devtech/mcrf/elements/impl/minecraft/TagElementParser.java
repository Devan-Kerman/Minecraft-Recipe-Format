package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;

import net.minecraft.util.Identifier;

public class TagElementParser implements ElementParser<Identifier> {
	@Override
	public Identifier parse(Reader reader) throws IllegalArgumentException, IOException {
		if(reader.read() != '#') throw new IllegalArgumentException("Tag must start with '#'!");
		return IDENTIFIER.parse(reader);
	}
}
