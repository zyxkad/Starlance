package net.jcm.vsch.util.assemble;

import net.jcm.vsch.accessor.ClearableContraptionHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.simibubi.create.content.contraptions.ControlledContraptionEntity;

public class MoveableIControlContraption implements IMoveable<Object> {
	private static final CompoundTag EMPTY_TAG = new CompoundTag();

	@Override
	public Object beforeMove(ServerLevel level, BlockPos origin, BlockPos target) {
		final BlockEntity be = level.getBlockEntity(origin);
		if (be instanceof ClearableContraptionHolder clearable) {
			clearable.clearContraptions();
		}
		return null;
	}

	@Override
	public void afterMove(ServerLevel level, BlockPos origin, BlockPos target, Object data) {
		//
	}
}
