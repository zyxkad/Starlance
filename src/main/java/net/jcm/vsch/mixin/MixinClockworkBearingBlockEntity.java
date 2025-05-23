package net.jcm.vsch.mixin;

import net.jcm.vsch.accessor.ContraptionHolder;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Pseudo
@Mixin(ClockworkBearingBlockEntity.class)
public abstract class MixinClockworkBearingBlockEntity extends KineticBlockEntity implements IBearingBlockEntity, ContraptionHolder {
	@Shadow(remap = false)
	protected ControlledContraptionEntity hourHand;
	@Shadow(remap = false)
	protected ControlledContraptionEntity minuteHand;

	protected MixinClockworkBearingBlockEntity() {
		super(null, null, null);
	}

	@Override
	public List<AbstractContraptionEntity> starlance$clearContraptions() {
		final List<AbstractContraptionEntity> res =
			this.hourHand == null
				? this.minuteHand == null ? List.of() : List.of(this.minuteHand)
				: this.minuteHand == null ? List.of(this.hourHand) : List.of(this.hourHand, this.minuteHand);
		this.hourHand = null;
		this.minuteHand = null;
		return res;
	}

	@Override
	public void starlance$restoreContraptions(List<AbstractContraptionEntity> contraptions) {
	}
}
