package net.devtech.mcrf.mixin.recipes;

import com.mojang.datafixers.util.Either;
import net.devtech.mcrf.defaults.AnvilRecipes;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.util.MCRFUtil;
import net.devtech.mcrf.util.minecraft.CountedTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilContainerMixin extends ForgingScreenHandler {
	@Shadow @Final private Property levelCost;

	@Shadow private int repairItemUsage;

	@Unique private int normalItemUsage = -1;

	public AnvilContainerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "onTakeOutput", at = @At("TAIL"))
	private void update(PlayerEntity player, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		if(!this.player.world.isClient) {
			this.updateResult();
			this.sendContentUpdates();
		}
	}

	@Redirect (method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 0))
	private void takeFirst(Inventory inventory, int slot, ItemStack stack) {
		if(this.normalItemUsage != -1) {
			inventory.getStack(slot).decrement(this.normalItemUsage);
			this.normalItemUsage = -1;
			return;
		}
		// EMPTY
		inventory.setStack(slot, stack);
	}

	@Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
	private void recipe(CallbackInfo ci) {
		this.normalItemUsage = -1;
		for (Recipe recipe : AnvilRecipes.MINECRAFT) {
			Either<CountedTags<Item>, ItemStack> first = recipe.getInput(0);
			Either<CountedTags<Item>, ItemStack> adder = recipe.getInput(1);
			if(MCRFUtil.suffices(this.input.getStack(0), first) && MCRFUtil.suffices(this.input.getStack(1), adder)) {
				this.levelCost.set(recipe.getInput(2));
				this.repairItemUsage = MCRFUtil.getAmount(adder);
				this.normalItemUsage = MCRFUtil.getAmount(first);
				this.output.setStack(0, ((ItemStack)recipe.getOutput(0)).copy());
				ci.cancel();
				break;
			}
		}
	}
}
