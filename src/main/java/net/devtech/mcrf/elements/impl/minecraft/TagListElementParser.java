package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;

import net.minecraft.util.Identifier;

public class TagListElementParser implements ElementParser<List<Identifier>> {
	@Override
	public List<Identifier> parse(Reader reader) throws IllegalArgumentException, IOException {
		if(reader.read() != '#') throw new IllegalArgumentException("tag list must start with #!");
		if(MCRFUtil.peek(reader) == '[')
			return ElementParser.list(IDENTIFIER).parse(reader);
		else
			return Collections.singletonList(IDENTIFIER.parse(reader));
	}
}
