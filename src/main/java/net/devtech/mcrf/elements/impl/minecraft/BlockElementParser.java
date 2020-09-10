package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;
import net.devtech.mcrf.util.minecraft.BlockData;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * an element parser that parses blocks, block properties, and block entity data all in one go
 */
public class BlockElementParser implements ElementParser<BlockData> {
	@Override
	public BlockData parse(Reader reader) throws IllegalArgumentException, IOException {
		Identifier identifier = IDENTIFIER.parse(reader);
		MCRFUtil.skipWhitespace(reader);
		String blockstate = identifier + MCRFUtil.readBetween(reader, '[', ']');
		BlockArgumentParser parser = new BlockArgumentParser(new StringReader(blockstate), false);
		try {
			parser.parse(false);
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		BlockState state = parser.getBlockState();
		CompoundTag tag = NBT.parse(reader);
		return new BlockData(state, tag);
	}
}
