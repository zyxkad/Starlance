package net.jcm.vsch.mixin;

import net.jcm.vsch.accessor.ContraptionHolder;

import net.minecraft.server.level.ServerLevel;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Pseudo
@Mixin(MechanicalBearingBlockEntity.class)
public abstract class MixinMechanicalBearingBlockEntity extends GeneratingKineticBlockEntity implements IBearingBlockEntity, ContraptionHolder {
	@Shadow(remap = false)
	protected ControlledContraptionEntity movedContraption;

	protected MixinMechanicalBearingBlockEntity() {
		super(null, null, null);
	}

	@Override
	public List<AbstractContraptionEntity> clearContraptions() {
		final List<AbstractContraptionEntity> res = List.of(this.movedContraption);
		this.movedContraption = null;
		this.sendData();
		return res;
	}

	@Override
	public void restoreContraptions(List<AbstractContraptionEntity> contraptions) {
		for (final AbstractContraptionEntity entity : contraptions) {
			if (entity instanceof final ControlledContraptionEntity cce) {
				final ServerLevel level = (ServerLevel) (cce.level());
				final ControlledContraptionEntity newEntity = ControlledContraptionEntity.create(level, this, cce.getContraption());
				cce.discard();
				level.addFreshEntity(newEntity);
				this.attach(cce);
				return;
			}
		}
	}
}
