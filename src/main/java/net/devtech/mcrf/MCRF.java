package net.devtech.mcrf;

import net.devtech.mcrf.defaults.AnvilRecipes;
import net.devtech.mcrf.defaults.ExplosionRecipes;
import net.devtech.mcrf.defaults.GuardianRecipes;
import net.devtech.mcrf.defaults.MinecraftRecipes;

import net.minecraft.network.packet.s2c.play.EntityS2CPacket;

import net.fabricmc.api.ModInitializer;

public class MCRF implements ModInitializer {
	@Override
	public void onInitialize() {
		MinecraftRecipes.init();
		AnvilRecipes.init();
		ExplosionRecipes.init();
		GuardianRecipes.init();
	}
}
