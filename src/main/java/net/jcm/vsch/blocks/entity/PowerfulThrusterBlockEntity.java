package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.config.VSCHConfig;

import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;

public class PowerfulThrusterBlockEntity extends AbstractThrusterBlockEntity {

	public PowerfulThrusterBlockEntity(BlockPos pos, BlockState state) {
		super("powerful_thruster", VSCHBlockEntities.POWERFUL_THRUSTER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public float getMaxThrottle() {
		return VSCHConfig.POWERFUL_THRUSTER_STRENGTH.get().intValue();
	}

	@Override
	protected ParticleOptions getThrusterParticleType() {
		return CosmosModParticleTypes.BLUETHRUSTED.get();
	}
}
