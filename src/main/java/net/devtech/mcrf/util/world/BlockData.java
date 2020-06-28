package net.devtech.mcrf.util.world;

import net.devtech.mcrf.util.MCRFUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
			if(this.tag != null) {
				BlockEntity entity = BlockEntity.createFromTag(this.state, this.tag);
				world.setBlockEntity(pos, entity);
			}
			return true;
		}
		return false;
	}

	public boolean isAir() {
		return this.state.getBlock() == Blocks.AIR;
	}

	public boolean test(World world, BlockPos pos) {
		if(world.getBlockState(pos) == this.state) {
			BlockEntity be = world.getBlockEntity(pos);
			if(be != null) {
				CompoundTag tag = be.toTag(new CompoundTag());
				return MCRFUtil.match(this.tag, tag);
			}
			return this.tag.isEmpty();
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
