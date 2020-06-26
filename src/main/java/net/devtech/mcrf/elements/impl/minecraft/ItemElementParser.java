package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ItemElementParser implements ElementParser<Item> {
	@Override
	public Item parse(Reader reader) throws IllegalArgumentException, IOException {
		return Registry.ITEM.get(IDENTIFIER.parse(reader));
	}
}
