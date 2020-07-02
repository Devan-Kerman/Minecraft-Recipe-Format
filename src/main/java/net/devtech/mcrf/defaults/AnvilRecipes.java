package net.devtech.mcrf.defaults;

import static net.devtech.mcrf.elements.ElementParser.BLOCK;
import static net.devtech.mcrf.elements.ElementParser.BOOLEAN;
import static net.devtech.mcrf.elements.ElementParser.FLOAT;
import static net.devtech.mcrf.elements.ElementParser.INTEGER;
import static net.devtech.mcrf.elements.ElementParser.ITEM_STACK;
import static net.devtech.mcrf.elements.ElementParser.MCRF_INGREDIENT;
import static net.devtech.mcrf.elements.ElementParser.RETROACTIVE;
import static net.devtech.mcrf.elements.ElementParser.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.devtech.mcrf.elements.impl.java.WildcardOrOtherElementParser;
import net.devtech.mcrf.elements.impl.minecraft.TagOrOtherElementParser;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.recipes.RecipeSchema;
import net.devtech.mcrf.util.Id;
import net.devtech.mcrf.util.RefreshingRecipe;

/**
 * anvil recipes, of which there are 2 types,
 * falling anvil, and vanilla anvil crafting.
 * Falling anvil means u drop an anvil on some items to craft an output, and you can also require the anvil to be on fire.
 * (surface) + ()
 * Vanilla anvil crafting brings data driving to anvil recipes!
 * (ingredient) + (ingredient) + (levels) --[minecraft:anvil]-> (output)
 */
public class AnvilRecipes {
	private static final Id FALLING_ANVIL = new Id("mcrf", "falling_anvil");
	private static final Id ANVIL = new Id("minecraft", "anvil");
	private static final RecipeSchema SCHEMA = new RecipeSchema.DynamicBuilder()
													   .addDefault(RETROACTIVE)
													   // minecraft anvil
													   // input item
													   .addInput(ANVIL, MCRF_INGREDIENT)
													   // adding item
													   .addInput(ANVIL, MCRF_INGREDIENT)
													   // exp needed
													   .addInput(ANVIL, INTEGER)
													   // result
													   .addOutput(ANVIL, ITEM_STACK)

													   // mcrf falling anvil
													   // block to land on
													   .addInput(FALLING_ANVIL, new TagOrOtherElementParser<>(BLOCK))
													   // item input
													   .addInput(FALLING_ANVIL, list(MCRF_INGREDIENT))
													   // flaming
			                                           .addInput(FALLING_ANVIL, BOOLEAN)
													   // smashed block
													   .addOutput(FALLING_ANVIL, new WildcardOrOtherElementParser<>(BLOCK))
													   // item output
													   .addOutput(FALLING_ANVIL, list(ITEM_STACK));

	public static final List<Recipe> MINECRAFT = new Vector<>();
	public static final List<Recipe> MCRF = new Vector<>();

	public static final RefreshingRecipe RECIPES = new RefreshingRecipe(FALLING_ANVIL.asIdentifier(), SCHEMA) {
		@Override
		protected void postReload() {
			for (Recipe recipe : this) {
				if(ANVIL.equals(recipe.getMachine()))
					MINECRAFT.add(recipe);
				else if(FALLING_ANVIL.equals(recipe.getMachine()))
					MCRF.add(recipe);
				else
					throw new IllegalArgumentException("unsupported machine: " + recipe.getMachine());
			}
		}
	};

	public static void init() {}
}
