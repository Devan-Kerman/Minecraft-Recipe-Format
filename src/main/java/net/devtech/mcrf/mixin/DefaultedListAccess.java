package net.devtech.mcrf.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.collection.DefaultedList;

@Mixin (DefaultedList.class)
public interface DefaultedListAccess {
	@Invoker
	static <E> DefaultedList<E> createDefaultedList(List<E> delegate, E initialElement) { throw new UnsupportedOperationException(); }
}
