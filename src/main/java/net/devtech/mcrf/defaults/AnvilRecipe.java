package net.devtech.mcrf.defaults;

import static net.devtech.mcrf.elements.ElementParser.BLOCK;
import static net.devtech.mcrf.elements.ElementParser.BOOLEAN;
import static net.devtech.mcrf.elements.ElementParser.ITEM_STACK;
import static net.devtech.mcrf.elements.ElementParser.list;

import net.devtech.mcrf.elements.impl.java.WildcardOrOtherElementParser;
import net.devtech.mcrf.elements.impl.minecraft.TagOrOtherElementParser;
import net.devtech.mcrf.recipes.RecipeSchema;
import net.devtech.mcrf.util.Id;
import net.devtech.mcrf.util.RefreshingRecipe;

public class AnvilRecipe {
	private static final Id ANVIL = new Id("mcrf", "falling_anvil");
	private static final RecipeSchema SCHEMA = new RecipeSchema.Builder(ANVIL)
													   // block to land on
													   .addInput(new TagOrOtherElementParser<>(BLOCK))
													   // item input
													   .addInput(list(ITEM_STACK))
													   // flaming
			                                           .addInput(BOOLEAN)
													   // smashed block
													   .addOutput(new WildcardOrOtherElementParser<>(BLOCK))
													   // item output
													   .addOutput(list(ITEM_STACK));

	public static final RefreshingRecipe RECIPES = new RefreshingRecipe(ANVIL.asIdentifier(), SCHEMA);

	public static void init() {}
}
