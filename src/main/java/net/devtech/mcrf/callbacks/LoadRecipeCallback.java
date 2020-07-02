package net.devtech.mcrf.callbacks;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * called then the RecipeManager's recipes are called to load, this is used to implement vanilla support for MCRF
 */
public interface LoadRecipeCallback {
	Event<LoadRecipeCallback> EVENT = EventFactory.createArrayBacked(LoadRecipeCallback.class, (m, i) -> {}, ls -> (map, manager) -> {
		for (LoadRecipeCallback l : ls) {
			l.load(map, manager);
		}
	});

	/**
	 * @param map2 the recipe map
	 * @param manager a resource manager
	 */
	void load(Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2, ResourceManager manager);
}
