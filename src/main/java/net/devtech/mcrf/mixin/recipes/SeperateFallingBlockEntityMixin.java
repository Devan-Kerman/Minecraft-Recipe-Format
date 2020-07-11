package net.devtech.mcrf.mixin.recipes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.FallingBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(FallingBlockEntity.class)
public class SeperateFallingBlockEntityMixin {
	@Environment (EnvType.CLIENT)
	@Inject (method = "doesRenderOnFire", at = @At ("TAIL"), cancellable = true)
	private void setTrue(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
