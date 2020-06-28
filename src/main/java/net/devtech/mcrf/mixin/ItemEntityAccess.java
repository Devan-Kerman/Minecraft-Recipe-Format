package net.devtech.mcrf.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.ItemEntity;

@Mixin(ItemEntity.class)
public interface ItemEntityAccess {
	@Invoker void callTryMerge();
}
