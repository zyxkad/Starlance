package net.jcm.vsch.util.assemble;

import net.jcm.vsch.accessor.ControlledContraptionEntityAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity;

public class MoveableLinearActuatorBlockEntity implements IMoveable<AbstractContraptionEntity> {

	@Override
	public AbstractContraptionEntity beforeMove(ServerLevel level, BlockPos origin, BlockPos target) {
		final LinearActuatorBlockEntity be = (LinearActuatorBlockEntity) (level.getBlockEntity(origin));
		final AbstractContraptionEntity entity = be.movedContraption;
		be.movedContraption = null;
		return entity;
	}

	@Override
	public void afterMove(ServerLevel level, BlockPos origin, BlockPos target, AbstractContraptionEntity entity) {
		if (entity == null) {
			return;
		}
		MoveableIControlContraption.moveContraptionEntity(level, entity, target.subtract(origin));
	}
}
