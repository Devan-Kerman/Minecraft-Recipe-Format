package net.devtech.mcrf.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.decoration.ArmorStandEntity;

@Mixin (ArmorStandEntity.class)
public interface ArmorStandEntityAccess {
	@Invoker void callSetSmall(boolean small);
	@Invoker void callSetMarker(boolean marker);
}
