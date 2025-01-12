package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.custom.template.AbstractThrusterBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.jcm.vsch.blocks.entity.ThrusterBlockEntity;


public class ThrusterBlock extends AbstractThrusterBlock<ThrusterBlockEntity> {

	public ThrusterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public ThrusterBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ThrusterBlockEntity(pos, state);
	}
}
