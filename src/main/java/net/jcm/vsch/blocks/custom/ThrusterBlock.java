package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.custom.template.AbstractThrusterBlock;

import net.jcm.vsch.blocks.entity.AirThrusterEngine;
import net.jcm.vsch.blocks.entity.NormalThrusterEngine;
import net.jcm.vsch.blocks.entity.VSCHBlockEntities;
import net.jcm.vsch.blocks.thruster.AirThrusterBlockEntity;
import net.jcm.vsch.blocks.thruster.GenericThrusterBlockEntity;
import net.jcm.vsch.config.VSCHConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.jcm.vsch.blocks.entity.ThrusterBlockEntity;


public class ThrusterBlock extends AbstractThrusterBlock<AirThrusterBlockEntity> {

	public ThrusterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public AirThrusterBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		AirThrusterBlockEntity be = new AirThrusterBlockEntity(
				"air_thruster",
				VSCHBlockEntities.AIR_THRUSTER_BLOCK_ENTITY.get(),
				pos,
				state,
				new NormalThrusterEngine(
						VSCHConfig.THRUSTER_ENERGY_CONSUME_RATE.get().intValue(),
						VSCHConfig.THRUSTER_STRENGTH.get().floatValue(),
						VSCHConfig.getThrusterFuelConsumeRates()
				)
		);
		be.evaporateDistance = 8;
		return be;
	}
}
