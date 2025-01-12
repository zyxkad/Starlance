package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.custom.template.AbstractThrusterBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.jcm.vsch.blocks.entity.PowerfulThrusterBlockEntity;


public class PowerfulThrusterBlock extends AbstractThrusterBlock<PowerfulThrusterBlockEntity> {

	public PowerfulThrusterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public PowerfulThrusterBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PowerfulThrusterBlockEntity(pos, state);
	}
}
