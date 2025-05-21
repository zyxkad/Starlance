package net.jcm.vsch.util.assemble;

import net.jcm.vsch.accessor.ContraptionHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;

import java.util.List;

public class MoveableIControlContraption implements IMoveable<List<AbstractContraptionEntity>> {
	private static final CompoundTag EMPTY_TAG = new CompoundTag();

	@Override
	public List<AbstractContraptionEntity> beforeMove(ServerLevel level, BlockPos origin, BlockPos target) {
		final BlockEntity be = level.getBlockEntity(origin);
		if (be instanceof ContraptionHolder holder) {
			return holder.clearContraptions();
		}
		return null;
	}

	@Override
	public void afterMove(ServerLevel level, BlockPos origin, BlockPos target, List<AbstractContraptionEntity> data) {
		final BlockEntity be = level.getBlockEntity(target);
		if (be instanceof ContraptionHolder holder) {
			holder.restoreContraptions(data);
		}
	}
}
