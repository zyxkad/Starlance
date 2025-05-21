package net.jcm.vsch.util.assemble;

import net.jcm.vsch.accessor.ContraptionHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity;

public class MoveableLinearActuatorBlockEntity implements IMoveable<AbstractContraptionEntity> {

	@Override
	public AbstractContraptionEntity beforeMove(ServerLevel level, BlockPos origin, BlockPos target) {
		final LinearActuatorBlockEntity be = (LinearActuatorBlockEntity) (level.getBlockEntity(origin));
		final AbstractContraptionEntity data = be.movedContraption;
		be.movedContraption = null;
		return data;
	}

	@Override
	public void afterMove(ServerLevel level, BlockPos origin, BlockPos target, AbstractContraptionEntity data) {
		if (data == null) {
			return;
		}
		final LinearActuatorBlockEntity be = (LinearActuatorBlockEntity) (level.getBlockEntity(target));
		final Contraption contraption = data.getContraption();
		final ControlledContraptionEntity newEntity = ControlledContraptionEntity.create(level, be, contraption);
		data.discard();
		level.addFreshEntity(newEntity);
		contraption.anchor = contraption.anchor.offset(target.getX() - origin.getX(), target.getY() - origin.getY(), target.getZ() - origin.getZ());
		newEntity.setPos(newEntity.position().add(target.getX() - origin.getX(), target.getY() - origin.getY(), target.getZ() - origin.getZ()));
		be.attach(newEntity);
	}
}
