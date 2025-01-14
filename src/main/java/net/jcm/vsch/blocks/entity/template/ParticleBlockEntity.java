package net.jcm.vsch.blocks.entity.template;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ParticleBlockEntity {

	default void clientTick(Level level, BlockPos pos, BlockState state, ParticleBlockEntity be) {
		tickParticles(level, pos, state);
	}

	default void serverTick(Level level, BlockPos pos, BlockState state, ParticleBlockEntity be) {
		if (level instanceof ServerLevel) {
			tickForce(level, pos, state);
		}
	}

	void tickForce(Level level, BlockPos pos, BlockState state);

	void tickParticles(Level level, BlockPos pos, BlockState state);
}
