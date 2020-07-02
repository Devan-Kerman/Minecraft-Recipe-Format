package net.devtech.mcrf.mixin;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.devtech.mcrf.callbacks.LoadRecipeCallback;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Shadow @Final private static Logger LOGGER;

	@Inject(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2) {
		LOGGER.info("Loading MCRF default recipes!");
		LoadRecipeCallback.EVENT.invoker().load(map2, resourceManager);
	}

	@Inject(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void throwOnError(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2, Iterator var5, Map.Entry<Identifier, JsonElement> entry, Identifier identifier, Recipe<?> recipe) {
		// dirty hack to allow replacement of vanilla recipes :sunglasses:
		ImmutableMap.Builder<Identifier, Recipe<?>> recipes = map2.get(recipe.getType());
		if(recipes != null) {
			if(recipes.build().containsKey(identifier)) {
				LOGGER.info("Ignore the next message:");
				throw new IllegalArgumentException("ignore this");
			}
		}
	}
}
