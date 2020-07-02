package net.devtech.mcrf.defaults;

import static net.devtech.mcrf.elements.ElementParser.ITEM_STACK;
import static net.devtech.mcrf.elements.ElementParser.MCRF_INGREDIENT;
import static net.devtech.mcrf.elements.ElementParser.listOrSingleton;

import net.devtech.mcrf.recipes.RecipeSchema;
import net.devtech.mcrf.util.Id;
import net.devtech.mcrf.util.RefreshingRecipe;

/**
 * fusion alloying, accelerate a projectile into a target to fuse them together
 */
public class ExplosionRecipes {
	private static final Id FUSION = new Id("mcrf", "fusion");

	private static final RecipeSchema EXPLOSIVE = new RecipeSchema.Builder(FUSION)
			// projectile
			.addInput(MCRF_INGREDIENT)
			// target
			.addInput(MCRF_INGREDIENT)
			// output
			.addOutput(listOrSingleton(ITEM_STACK));

	public static final RefreshingRecipe RECIPES = new RefreshingRecipe(FUSION.asIdentifier(), EXPLOSIVE);

	public static void init() {}
}
