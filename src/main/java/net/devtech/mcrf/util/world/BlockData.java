package net.devtech.mcrf.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * a blockstate and it's block entity
 */
public final class BlockData {
	private final BlockState state;
	private final CompoundTag tag;

	public BlockData(BlockState state, CompoundTag tag) {
		this.state = state;
		this.tag = tag;
	}

	public boolean set(World world, BlockPos pos) {
		if(world.setBlockState(pos, this.state)) {
			BlockEntity entity = BlockEntity.createFromTag(this.state, this.tag);
			world.setBlockEntity(pos, entity);
			return true;
		}
		return false;
	}

	public BlockState getState() {
		return this.state;
	}

	public CompoundTag getEntity() {
		return this.tag;
	}
}
