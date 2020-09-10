package net.devtech.mcrf.util.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * an entity and it's data
 * eg. namespace:path{nbt:data}
 */
public final class EntityData<T extends Entity> {
	private final EntityType<T> type;
	private final CompoundTag tag;

	public EntityData(EntityType<T> type, CompoundTag tag) {
		this.type = type;
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("EntityTag", tag);
		this.tag = compoundTag;
	}

	public Entity create(ServerWorld world, BlockPos pos) {
		return this.create(world, null, null, pos, SpawnReason.TRIGGERED, false, false);
	}

	public Entity create(ServerWorld world, /*nullable*/ Text name, /*nullable*/ PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY) {
		return this.type.create(world, this.tag.copy(), name, player, pos, spawnReason, alignPosition, invertY);
	}

	public EntityType<T> getType() {
		return this.type;
	}

	public CompoundTag getTag() {
		return this.tag;
	}
}
