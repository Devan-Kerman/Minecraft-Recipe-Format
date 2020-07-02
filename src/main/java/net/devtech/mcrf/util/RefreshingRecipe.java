package net.devtech.mcrf.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.recipes.RecipeSchema;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;

/**
 * a utility class for easy reloading support, refreshing recipes automatically update their internal recipe list on reload, if u need to sort out the
 * recipes in advance for performance's sake, u can override {@link #postReload()}
 */
public class RefreshingRecipe implements Iterable<Recipe> {
	private static final AtomicInteger CURRENT_ID = new AtomicInteger();

	private final List<Recipe> instance = new Vector<>();
	private final Identifier id;

	public RefreshingRecipe(Identifier identifier, RecipeSchema schema) {
		this.id = identifier;
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleResourceReloadListener<List<Recipe>>() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(identifier.getNamespace(), identifier.getPath() + "_autorefreshing_recipe_"+CURRENT_ID);
			}

			@Override
			public CompletableFuture<List<Recipe>> load(ResourceManager manager, Profiler profiler, Executor executor) {
				return CompletableFuture.supplyAsync(() -> {
					List<Recipe> recipes = new ArrayList<>();
					for (Identifier resource : manager.findResources(identifier, s -> s.endsWith(".mcrf"))) {
						try {
							recipes.addAll(Recipe.parse(manager.getResource(resource)
							                                   .getInputStream(), schema));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
					return recipes;
				});
			}

			@Override
			public CompletableFuture<Void> apply(List<Recipe> data, ResourceManager manager, Profiler profiler, Executor executor) {
				return CompletableFuture.runAsync(() -> {
					RefreshingRecipe.this.instance.clear();
					RefreshingRecipe.this.instance.addAll(data);
					RefreshingRecipe.this.postReload();
				});
			}
		});
	}

	/**
	 * called after the recipe is done reloading,
	 * this is called asynchronously!
	 */
	protected void postReload() {
		System.out.println(this.id + " is done reloading!");
	}

	@Override
	public Iterator<Recipe> iterator() {
		return this.instance.iterator();
	}
}
