package net.jcm.vsch.accessor;

import net.minecraft.core.BlockPos;

public interface ControlledContraptionEntityAccessor {
	BlockPos getControllerPos();

	void setControllerPos(BlockPos pos);
}
