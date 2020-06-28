package net.devtech.mcrf;

import net.devtech.mcrf.defaults.AnvilRecipe;
import net.devtech.mcrf.defaults.MinecraftRecipes;

import net.fabricmc.api.ModInitializer;

public class MCRF implements ModInitializer {

	@Override
	public void onInitialize() {
		MinecraftRecipes.init();
		AnvilRecipe.init();
	}
}
