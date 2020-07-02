package net.devtech.mcrf.defaults;

import static net.devtech.mcrf.elements.ElementParser.FLOAT;
import static net.devtech.mcrf.elements.ElementParser.IDENTIFIER;
import static net.devtech.mcrf.elements.ElementParser.INGREDIENT;
import static net.devtech.mcrf.elements.ElementParser.INTEGER;
import static net.devtech.mcrf.elements.ElementParser.ITEM_STACK;
import static net.devtech.mcrf.elements.ElementParser.RETROACTIVE;
import static net.devtech.mcrf.elements.ElementParser.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import net.devtech.mcrf.callbacks.LoadRecipeCallback;
import net.devtech.mcrf.elements.impl.java.ListOrOtherElementParser;
import net.devtech.mcrf.mixin.DefaultedListAccess;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.recipes.RecipeSchema;
import net.devtech.mcrf.util.Id;
import net.devtech.mcrf.util.io.CommentStrippingReader;
import net.devtech.mcrf.util.io.LineTrackingReader;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class MinecraftRecipes {
	private static final Logger LOGGER = Logger.getLogger("MCRF|Minecraft");
	private static final Id CRAFTING_TABLE = new Id("minecraft", "crafting_table");
	private static final Id STONE_CUTTING = new Id("minecraft", "stone_cutter");
	private static final Id SMITHING = new Id("minecraft", "smithing_table");
	private static final Id CAMPFIRE = new Id("minecraft", "campfire");
	private static final Id SMOKING = new Id("minecraft", "smoker");
	private static final Id BLASTING = new Id("minecraft", "blast_furnace");
	private static final Id SMELTING = new Id("minecraft", "furnace");
	public static final RecipeSchema MINECRAFT_RECIPE_SCHEMA = new RecipeSchema.DynamicBuilder()
			                                                           // recipe id
			                                                           .addDefault(IDENTIFIER, RETROACTIVE)
			                                                           // inputs
			                                                           .addInput(CRAFTING_TABLE, list(new ListOrOtherElementParser<>(INGREDIENT)))
			                                                           .addInputs(new Id[] {
					                                                           STONE_CUTTING,
					                                                           CAMPFIRE,
					                                                           SMOKING,
					                                                           BLASTING,
					                                                           SMELTING,
					                                                           SMITHING
			                                                           }, INGREDIENT)
			                                                           .addInput(SMITHING, INGREDIENT)
			                                                           // output
			                                                           .addOutputs(new Id[] {
					                                                           CRAFTING_TABLE,
					                                                           STONE_CUTTING,
					                                                           SMITHING,
					                                                           SMOKING,
					                                                           BLASTING,
					                                                           SMELTING,
					                                                           CAMPFIRE
			                                                           }, ITEM_STACK)
			                                                           .addOutputs(new Id[] {
					                                                           SMOKING,
					                                                           BLASTING,
					                                                           SMELTING,
					                                                           CAMPFIRE
			                                                           }, FLOAT, INTEGER);

	static {
		LoadRecipeCallback.EVENT.register(((map, manager) -> {
			LOGGER.info("loading...");
			for (Identifier resource : manager.findResources(new Identifier("mcrf", "minecraft"), s -> s.endsWith(".mcrf"))) {
				try {
					loadFromStream(resource, map, manager.getResource(resource)
					                                     .getInputStream());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}));
	}

	public static void loadFromStream(Identifier resource, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, net.minecraft.recipe.Recipe<?>>> map, InputStream manager) {
		try {
			List<Recipe> recipes = Recipe.parse(manager, MINECRAFT_RECIPE_SCHEMA);
			LOGGER.info(recipes.size() + " found in " + resource);
			for (Recipe recipe : recipes) {
				Identifier recipeId = recipe.getInput(0);
				Id machine = recipe.getMachine();
				LOGGER.info("adding: " + recipe);
				if (CRAFTING_TABLE.equals(machine)) {
					if (((List<?>) recipe.getInput(1)).get(0) instanceof List) {
						// nested list = shaped
						ImmutableMap.Builder<Identifier, net.minecraft.recipe.Recipe<?>> instance = map.computeIfAbsent(RecipeType.CRAFTING, c -> ImmutableMap.builder());
						ImmutableMap<Identifier, net.minecraft.recipe.Recipe<?>> recipeMap = instance.build();
						instance.put(recipeId, new ShapedRecipe(recipeId, "", 3, 3, flatten(recipe.getInput(1)), recipe.getOutput(0)));
					} else {
						List<Ingredient> shapeless = recipe.getInput(1);
						map.computeIfAbsent(RecipeType.CRAFTING, c -> ImmutableMap.builder())
						   .put(recipeId,
						        new ShapelessRecipe(recipeId,
						                            "",
						                            recipe.getOutput(0),
						                            DefaultedListAccess.createDefaultedList(shapeless, Ingredient.EMPTY)
						        )
						   );
					}
				} else if (STONE_CUTTING.equals(machine)) {
					// stone cutting
					Ingredient input = recipe.getInput(1);
					map.computeIfAbsent(RecipeType.STONECUTTING, c -> ImmutableMap.builder())
					   .put(recipeId, new StonecuttingRecipe(recipeId, "", input, recipe.getOutput(0)));
				} else if (SMITHING.equals(machine)) {
					Ingredient base = recipe.getInput(1);
					Ingredient add = recipe.getInput(2);
					map.computeIfAbsent(RecipeType.SMITHING, c -> ImmutableMap.builder())
					   .put(recipeId, new SmithingRecipe(recipeId, base, add, recipe.getOutput(0)));
				} else {
					// SMELTING
					Ingredient base = recipe.getInput(1);
					ItemStack output = recipe.getOutput(0);
					float exp = recipe.getOutput(1);
					int time = recipe.getOutput(2);
					if(CAMPFIRE.equals(machine)) {
						map.computeIfAbsent(RecipeType.CAMPFIRE_COOKING, c -> ImmutableMap.builder())
						   .put(recipeId, new CampfireCookingRecipe(recipeId, "", base, output, exp, time));
					} else if(SMOKING.equals(machine)) {
						map.computeIfAbsent(RecipeType.SMOKING, c -> ImmutableMap.builder())
						   .put(recipeId, new SmokingRecipe(recipeId, "", base, output, exp, time));
					} else if(BLASTING.equals(machine)) {
						map.computeIfAbsent(RecipeType.BLASTING, c -> ImmutableMap.builder())
						   .put(recipeId, new BlastingRecipe(recipeId, "", base, output, exp, time));
					} else {
						map.computeIfAbsent(RecipeType.SMELTING, c -> ImmutableMap.builder())
						   .put(recipeId, new SmeltingRecipe(recipeId, "", base, output, exp, time));
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private static DefaultedList<Ingredient> flatten(Object object) {
		List<List<Ingredient>> itemss = (List<List<Ingredient>>) object;
		Ingredient[] arr = new Ingredient[9];
		Arrays.fill(arr, Ingredient.EMPTY);
		for (int x = 0; x < itemss.size(); x++) {
			List<Ingredient> items = itemss.get(x);
			for (int z = 0; z < items.size(); z++) {
				arr[x * 3 + z] = items.get(z);
			}
		}
		return DefaultedList.copyOf(Ingredient.EMPTY, arr);
	}

	public static void init() {}
}
