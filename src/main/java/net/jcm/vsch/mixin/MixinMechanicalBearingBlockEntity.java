package net.jcm.vsch.mixin;

import net.jcm.vsch.accessor.ContraptionHolder;

import net.minecraft.server.level.ServerLevel;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

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
		final List<AbstractContraptionEntity> res = this.movedContraption == null ? List.of() : List.of(this.movedContraption);
		this.movedContraption = null;
		return res;
	}

	@Override
	public void restoreContraptions(List<AbstractContraptionEntity> contraptions) {
	}
}
