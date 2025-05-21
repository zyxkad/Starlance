package net.jcm.vsch.mixin;

import net.jcm.vsch.accessor.ClearableContraptionHolder;

import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(MechanicalBearingBlockEntity.class)
public class MixinMechanicalBearingBlockEntity implements ClearableContraptionHolder {
	@Shadow(remap = false)
	protected ControlledContraptionEntity movedContraption;

	@Override
	public void clearContraptions() {
		this.movedContraption = null;
	}
}
