package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.custom.template.AbstractThrusterBlock;

import net.jcm.vsch.blocks.entity.NormalThrusterEngine;
import net.jcm.vsch.blocks.entity.PowerfulThrusterEngine;
import net.jcm.vsch.blocks.entity.VSCHBlockEntities;
import net.jcm.vsch.blocks.thruster.GenericThrusterBlockEntity;
import net.jcm.vsch.config.VSCHConfig;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class PowerfulThrusterBlock extends AbstractThrusterBlock<GenericThrusterBlockEntity> {

	public PowerfulThrusterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public GenericThrusterBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		GenericThrusterBlockEntity be = new GenericThrusterBlockEntity(
				"powerful_thruster",
				VSCHBlockEntities.THRUSTER_BLOCK_ENTITY.get(),
				pos,
				state,
				new PowerfulThrusterEngine(
						VSCHConfig.POWERFUL_THRUSTER_ENERGY_CONSUME_RATE.get().intValue(),
						VSCHConfig.POWERFUL_THRUSTER_STRENGTH.get().floatValue(),
						VSCHConfig.POWERFUL_THRUSTER_FUEL_CONSUME_RATE.get().intValue()
				)
		);
		be.evaporateDistance = 8;
		be.fireParticleType = CosmosModParticleTypes.BLUETHRUSTED.get();

		return be;
	}
}
