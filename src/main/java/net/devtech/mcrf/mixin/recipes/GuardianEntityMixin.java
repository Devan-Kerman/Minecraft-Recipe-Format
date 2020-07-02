package net.devtech.mcrf.mixin.recipes;

import java.util.Optional;

import net.devtech.mcrf.defaults.GuardianRecipes;
import net.devtech.mcrf.mixin.ArmorStandEntityAccess;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.util.MCRFUtil;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin (GuardianEntity.class)
public abstract class GuardianEntityMixin extends HostileEntity {

	@Shadow
	protected abstract void setBeamTarget(int progress);

	@Unique private static final BlockState FILLED_CAULDRON = Blocks.CAULDRON.getDefaultState()
	                                                                         .with(CauldronBlock.LEVEL, 3);

	protected GuardianEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	@Intrinsic
	public void tick() {
		super.tick();
	}

	private BlockPos lastPos;
	private Integer lastId;
	private int time;

	@SuppressWarnings ("UnresolvedMixinReference")
	@Inject (method = "tick", at = @At ("HEAD"))
	private void scan(CallbackInfo ci) {
		if (this.age % 10 == 0) {
			for (BlockPos pos : BlockPos.iterateOutwards(this.getBlockPos(), 5, 5, 5)) {
				if (this.world.getBlockState(pos) == FILLED_CAULDRON) {
					this.time++;
					// if target cauldron switches, remove the old client entity and make a new one
					if (!pos.equals(this.lastPos)) {
						this.time = 0;
						// packet entities because I CBA deal with all the exploits that could come out of this otherwise
						// client entity
						ArmorStandEntity entity = new ArmorStandEntity(this.world, pos.getX() + .5, pos.getY(), pos.getZ() + .5);
						int id = entity.getEntityId();
						// cleanup old entities
						this.killVirtual();
						this.lastId = id;
						this.lastPos = pos.toImmutable();


						// send to nearby players
						Optional.ofNullable(this.world.getServer())
						        .ifPresent(m -> {
							        m.getPlayerManager()
							         .sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64, this.world.getRegistryKey(), new MobSpawnS2CPacket(entity));
							        entity.setInvisible(true);
							        ArmorStandEntityAccess access = (ArmorStandEntityAccess) entity;
							        access.callSetMarker(true);
							        access.callSetSmall(true);
							        m.getPlayerManager()
							         .sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64, this.world.getRegistryKey(), new EntityTrackerUpdateS2CPacket(id, entity.getDataTracker(), true));
						        });

						this.setBeamTarget(id);
					}

					for (Recipe recipe : GuardianRecipes.RECIPES) {
						if (!recipe.getMachine()
						           .equals(GuardianRecipes.ELDER_GUARDIAN) || (Object) this instanceof ElderGuardianEntity) {
							if (this.time >= recipe.<Integer>getInput(1)) {
								if (MCRFUtil.executeInWorldRecipe(this.world, pos, recipe.getInput(0), recipe.getOutput(0))) {
									break;
								}
							}
						}
					}

					return;
				}
			}
			// if no cauldron was found, clean up old entities, and reset progress
			this.time = 0;
			this.killVirtual();
		}
	}

	// kill client entities to prevent client lag
	@Unique
	private void killVirtual() {
		if (this.lastId == null) {
			return;
		}
		BlockPos pos = this.getBlockPos();
		Optional.ofNullable(this.world.getServer())
		        .ifPresent(m -> m.getPlayerManager()
		                         .sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64, this.world.getRegistryKey(), new EntitiesDestroyS2CPacket(this.lastId)));
		this.setBeamTarget(0);
		this.lastId = null;
		this.lastPos = null;
	}
}
