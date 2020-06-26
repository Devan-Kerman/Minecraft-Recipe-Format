package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.IOUtil;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

public class ItemStackElementParser implements ElementParser<ItemStack> {
	@Override
	public ItemStack parse(Reader reader) throws IllegalArgumentException, IOException {
		Item item = Registry.ITEM.get(IDENTIFIER.parse(reader));
		IOUtil.skipWhitespace(reader);
		CompoundTag tag = NBT.parse(reader);
		IOUtil.skipWhitespace(reader);
		int count = 1;
		reader.mark(1);
		int chr = reader.read();
		if(chr == 'x') {
			count = INTEGER.parse(reader);
		} else {
			reader.reset();
		}
		ItemStack stack = new ItemStack(item, count);
		stack.setTag(tag);
		return stack;
	}
}
