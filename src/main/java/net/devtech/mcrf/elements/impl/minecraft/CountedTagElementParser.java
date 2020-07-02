package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;
import net.devtech.mcrf.util.minecraft.CountedTags;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * a tag that can also have an amount attached to it via the 'xAMOUNT' syntax like in {@link ItemStackElementParser}
 * eg. tag:namespace xAMOUNT
 */
public abstract class CountedTagElementParser<T> implements ElementParser<CountedTags<T>> {
	@Override
	public CountedTags<T> parse(Reader reader) throws IllegalArgumentException, IOException {
		List<Identifier> id = IDENTIFIERS.parse(reader);
		MCRFUtil.skipWhitespace(reader);
		reader.mark(1);
		int amount;
		if(reader.read() == 'x') {
			amount = INTEGER.parse(reader);
		} else {
			amount = 1;
			reader.reset();
		}
		return new CountedTags<T>(id, amount) {
			@Override
			protected Tag<T> get(Identifier identifier) {
				return CountedTagElementParser.this.get(identifier);
			}
		};
	}

	protected abstract Tag<T> get(Identifier identifier);
}
