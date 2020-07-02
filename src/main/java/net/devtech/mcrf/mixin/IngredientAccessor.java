package net.devtech.mcrf.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.recipe.Ingredient;

@Mixin (Ingredient.class)
public interface IngredientAccessor {
	@Invoker
	static Ingredient callOfEntries(Stream<? /*extends Ingredient.Entry*/> entries) { throw new UnsupportedOperationException(); }
}
