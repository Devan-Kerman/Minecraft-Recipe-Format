package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;

import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.tag.TagRegistry;

public class IngredientElementParser implements ElementParser<Ingredient> {
	@Override
	public Ingredient parse(Reader reader) throws IllegalArgumentException, IOException {
		if(MCRFUtil.peek(reader) == '#') {
			reader.read();
			Identifier identifier = IDENTIFIER.parse(reader);
			return Ingredient.fromTag(TagRegistry.item(identifier));
		}
		return Ingredient.ofItems(Registry.ITEM.get(IDENTIFIER.parse(reader)));
	}
}
