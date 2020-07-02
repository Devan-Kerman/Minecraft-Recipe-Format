package net.devtech.mcrf.defaults;

import static net.devtech.mcrf.elements.ElementParser.*;

import net.devtech.mcrf.recipes.RecipeSchema;
import net.devtech.mcrf.util.Id;
import net.devtech.mcrf.util.RefreshingRecipe;

/**
 * laser powered cauldron crafting
 * original idea by calloatti
 */
public class GuardianRecipes {
	public static final Id GUARDIAN = new Id("mcrf", "guardian");
	public static final Id ELDER_GUARDIAN = new Id("mcrf", "elder_guardian");
	private static final RecipeSchema GUARDIAN_RECIPE = new RecipeSchema.Builder(GUARDIAN, ELDER_GUARDIAN)
			.addInput(listOrSingleton(MCRF_INGREDIENT))
			.addInput(INTEGER)
			.addOutput(listOrSingleton(ITEM_STACK));
	public static final RefreshingRecipe RECIPES = new RefreshingRecipe(GUARDIAN.asIdentifier(), GUARDIAN_RECIPE);

	public static void init() {}
}
