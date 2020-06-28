package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

public class NbtElementParser implements ElementParser<CompoundTag> {
	@Override
	public CompoundTag parse(Reader reader) throws IllegalArgumentException, IOException {
		String data = MCRFUtil.readBetween(reader, '{', '}');
		if (!data.isEmpty()) {
			try {
				return StringNbtReader.parse(data);
			} catch (CommandSyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			return null;
		}
	}
}
