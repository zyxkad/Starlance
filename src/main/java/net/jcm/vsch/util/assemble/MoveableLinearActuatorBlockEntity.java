package net.jcm.vsch.util.assemble;

import net.jcm.vsch.accessor.ControlledContraptionEntityAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
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
		final BlockPos offset = target.subtract(origin);
		final Contraption contraption = entity.getContraption();
		contraption.anchor = contraption.anchor.offset(offset);
		entity.setPos(entity.position().add(offset.getX(), offset.getY(), offset.getZ()));
		if (entity instanceof ControlledContraptionEntityAccessor ccea) {
			ccea.setControllerPos(ccea.getControllerPos().offset(offset));
		}
		AbstractContraptionEntity newEntity = (AbstractContraptionEntity) (entity.getType().create(level));
		newEntity.restoreFrom(entity);
		entity.getPassengers().forEach((passenger) -> {
			final Integer seat = contraption.getSeatMapping().get(passenger.getUUID());
			if (seat != null) {
				newEntity.addSittingPassenger(passenger, seat);
			}
		});
		entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
		level.addFreshEntity(newEntity);
	}
}
