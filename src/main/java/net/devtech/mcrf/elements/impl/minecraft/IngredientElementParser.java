package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.mixin.IngredientAccessor;
import net.devtech.mcrf.util.MCRFUtil;

import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.tag.TagRegistry;

public class IngredientElementParser implements ElementParser<Ingredient> {
	@Override
	public Ingredient parse(Reader reader) throws IllegalArgumentException, IOException {
		if(MCRFUtil.peek(reader) == '#') {
			reader.read();
			return IngredientAccessor.callOfEntries(IDENTIFIERS.parse(reader).stream().map(TagRegistry::item).map(Ingredient.TagEntry::new));
		}
		return Ingredient.ofItems(Registry.ITEM.get(IDENTIFIER.parse(reader)));
	}
}
