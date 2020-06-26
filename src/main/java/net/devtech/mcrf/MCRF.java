package net.devtech.mcrf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import net.devtech.mcrf.callbacks.LoadRecipeCallback;
import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.recipes.RecipeSchema;
import net.devtech.mcrf.util.Id;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import net.fabricmc.api.ModInitializer;

// todo add comment support
public class MCRF implements ModInitializer {
	private static final Id CRAFTING_TABLE = new Id("mcrf", "crafting_table");
	public static final RecipeSchema SHAPED_CRAFTING_RECIPE_SCHEMA = new RecipeSchema.Builder()
																			 // recipe id
			                                                                 .addInput(ElementParser.IDENTIFIER)
																			 // inputs
			                                                                 .addInput(
			                                                                 		ElementParser.array(
			                                                                 				ElementParser.array(
			                                                                 						ElementParser.ITEM)))
																			 // outputs
			                                                                 .addOutput(ElementParser.ITEM_STACK)
			                                                                 .build();

	@Override
	public void onInitialize() {
		LoadRecipeCallback.EVENT.register(((map, manager) -> {
			System.out.println("Loading!");
			for (Identifier resource : manager.findResources(new Identifier("mcrf", "shaped"), s -> s.endsWith(".mcrf"))) {
				System.out.println("Reading " + resource);
				try {
					Reader stream = new BufferedReader(new InputStreamReader(manager.getResource(resource).getInputStream()));
					// todo test/support all vanilla recipe types + custom ones
					List<Recipe> recipes = Recipe.parse(stream, SHAPED_CRAFTING_RECIPE_SCHEMA);
					for (Recipe recipe : recipes) {
						System.out.println("Adding shaped recipe: " + recipe);
						if(recipe.getMachine().equals(CRAFTING_TABLE)) {
							Identifier id = recipe.getInput(0);
							map.computeIfAbsent(RecipeType.CRAFTING, c -> ImmutableMap.builder()).put(id, new ShapedRecipe(
									id,
									"",
									3,
									3,
									flatten(recipe.getInput(1)),
									recipe.getOutput(0)
							));
						} else {
							throw new IllegalArgumentException(recipe + " is in a shaped recipe file, but it doesn't declare crafting table as it's machine");
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}));
	}

	private static DefaultedList<Ingredient> flatten(Object object) {
		List<List<Item>> itemss = (List<List<Item>>) object;
		Ingredient[] arr = new Ingredient[9];
		for (int x = 0; x < itemss.size(); x++) {
			List<Item> items = itemss.get(x);
			for (int z = 0; z < items.size(); z++) {
				Item item = items.get(z);
				arr[x*3+z] = Ingredient.ofItems(item);
			}
		}
		return DefaultedList.copyOf(Ingredient.EMPTY, arr);
	}
}
