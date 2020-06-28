package net.devtech.mcrf.mixin.recipes;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.devtech.mcrf.defaults.AnvilRecipe;
import net.devtech.mcrf.mixin.DefaultedListAccess;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.util.MCRFUtil;
import net.devtech.mcrf.util.world.BlockData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.tag.TagRegistry;

@Mixin (FallingBlockEntity.class)
public abstract class FallingBlockMixin extends Entity {
	@Shadow private BlockState block;
	@Unique private static final Tag<Block> ANVILS = TagRegistry.block(new Identifier("anvil"));

	public FallingBlockMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject (method = "handleFallDamage", at = @At ("HEAD"))
	private void land(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> cir) {
		if (!this.world.isClient && ANVILS.contains(this.block.getBlock())) {
			for (Recipe recipe : AnvilRecipe.RECIPES) {
				boolean flaming = recipe.getInput(2);
				if (flaming) {
					if (!this.isOnFire()) {
						continue;
					}
				}

				BlockPos floorPos = new BlockPos(this.getX(), this.getY() - .01, this.getZ());
				Either<Tag<Block>, BlockData> surface = ((Either<Identifier, BlockData>) recipe.getInput(0)).mapLeft(TagRegistry::block);
				boolean[] valid = {true};
				surface.ifRight(b -> {
					if (!(b.isAir() || b.test(this.world, floorPos))) {
						valid[0] = false;
					}
				});
				surface.ifLeft(t -> {
					if (!t.contains(this.world.getBlockState(floorPos)
					                          .getBlock())) {
						valid[0] = false;
					}
				});

				if (!valid[0]) {
					continue;
				}

				List<ItemStack> ingredients = recipe.getInput(1);
				List<ItemEntity> stacks = this.world.getEntities(EntityType.ITEM, new Box(this.getBlockPos()), e -> true);
				Object2IntMap<ItemEntity> removed = new Object2IntOpenHashMap<>();
				if (!ingredients.isEmpty()) {
					boolean allSatisfied = true;
					for (ItemStack ingredient : ingredients) {
						if (!stacks.removeIf(i -> MCRFUtil.hasAtleast(i.getStack(), ingredient) && removed.put(i, ingredient.getCount()) != Integer.MIN_VALUE)) {
							allSatisfied = false;
						}
					}

					if (!allSatisfied) {
						continue;
					}
				}

				// enact transformation
				removed.forEach((i, a) -> i.getStack()
				                           .decrement(a));
				Optional<BlockData> block = recipe.getOutput(0);
				List<ItemStack> output = recipe.getOutput(1);
				for (int i = 0; i < output.size(); i++) {
					output.set(i,
					           output.get(i)
					                 .copy()
					);
				}

				ItemScatterer.spawn(this.world,
				                    this.getBlockPos()
				                        .up(),
				                    DefaultedListAccess.createDefaultedList(output, ItemStack.EMPTY)
				);
				block.ifPresent(b -> b.set(this.world, floorPos));
			}
		}
	}

	// hackfix to prevent people from loosing items when an anvil falls on their ingredients because merging is not properly implemented
	@Redirect(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
	private List<Entity> blacklistItems(World world, Entity except, Box box) {
		return world.getEntities(except, box, e -> !(e instanceof ItemEntity));
	}

	@Inject (method = "doesRenderOnFire", at = @At ("TAIL"), cancellable = true)
	private void setTrue(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
