package net.jcm.vsch.mixin;

import net.jcm.vsch.accessor.ContraptionHolder;

import net.minecraft.server.level.ServerLevel;

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
	public List<AbstractContraptionEntity> clearContraptions() {
		final List<AbstractContraptionEntity> res =
			this.hourHand == null
				? this.minuteHand == null ? List.of() : List.of(this.minuteHand)
				: this.minuteHand == null ? List.of(this.hourHand) : List.of(this.hourHand, this.minuteHand);
		this.hourHand = null;
		this.minuteHand = null;
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
			}
		}
	}
}
