package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.entity.template.AbstractThrusterBlockEntity;
import org.joml.Vector3d;

import net.jcm.vsch.config.VSCHConfig;

import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;

public class AirThrusterBlockEntity extends AbstractThrusterBlockEntity {

	public AirThrusterBlockEntity(BlockPos pos, BlockState state) {
		super("air_thruster", VSCHBlockEntities.AIR_THRUSTER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public float getMaxThrottle() {
		return VSCHConfig.AIR_THRUSTER_STRENGTH.get().intValue();
	}

	@Override
	protected ParticleOptions getThrusterParticleType() {
		return CosmosModParticleTypes.AIR_THRUST.get();
	}

	@Override
	protected ParticleOptions getThrusterSmokeParticleType() {
		return CosmosModParticleTypes.AIR_THRUST.get();
	}

	@Override
	protected void spawnParticles(Vector3d pos, Vector3d direction) {
		Vector3d speed = new Vector3d(direction).mul(-getPower());
		speed.mul(0.118);

		int max = 100;

		for (int i = 0; i < max; i++) {
			level.addParticle(
					getThrusterParticleType(),
					pos.x, pos.y, pos.z,
					speed.x, speed.y, speed.z
					);
		}
	}
}
