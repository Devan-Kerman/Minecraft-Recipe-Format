package net.devtech.mcrf.mixin.recipes;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Either;
import net.devtech.mcrf.MCRF;
import net.devtech.mcrf.defaults.ExplosionRecipes;
import net.devtech.mcrf.mixin.DefaultedListAccess;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.util.MCRFUtil;
import net.devtech.mcrf.util.minecraft.CountedTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin (ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	@Shadow public abstract ItemStack getStack();

	@Shadow public abstract void setStack(ItemStack stack);

	public ItemEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}


	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void projectile(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// I'm too lazy to blacklist only items used in fusion / explosion crafting, so I'm just disabling all explosion damage to ItemEntities
		if(source.isExplosive()) {
			cir.setReturnValue(false);
		}
	}

	@Inject (method = "tick", at = @At ("HEAD"))
	private void tntAlloying(CallbackInfo ci) {
		Vec3d velocity = this.getVelocity();
		double val = velocity.lengthSquared();
		if (val > 10) {
			Vec3d current = this.getPos();
			EntityHitResult result = ProjectileUtil.getEntityCollision(this.world, this, current, current.add(velocity), this.getBoundingBox().stretch(velocity), e -> e instanceof ItemEntity);
			if(result != null) {
				ItemEntity entity = (ItemEntity) result.getEntity();
				for (Recipe recipe : ExplosionRecipes.RECIPES) {
					Either<CountedTags<Item>, ItemStack> projectile = recipe.getInput(0);
					Either<CountedTags<Item>, ItemStack> target = recipe.getInput(1);
					if(MCRFUtil.suffices(this.getStack(), projectile) && MCRFUtil.suffices(entity.getStack(), target)) {
						this.remove();
						this.setStack(ItemStack.EMPTY);
						entity.remove();
						entity.setStack(ItemStack.EMPTY);

						List<ItemStack> output = new ArrayList<>(recipe.getOutput(0));
						for (int i = 0; i < output.size(); i++) {
							output.set(i, output.get(i).copy());
						}

						ItemScatterer.spawn(this.world, this.getBlockPos(), DefaultedListAccess.createDefaultedList(output, ItemStack.EMPTY));
					}
				}
			}
		}
	}
}
