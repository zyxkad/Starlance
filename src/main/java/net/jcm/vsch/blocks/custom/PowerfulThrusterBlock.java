package net.jcm.vsch.blocks.custom;


import net.jcm.vsch.blocks.custom.template.ThrusterBlock;
import net.jcm.vsch.config.VSCHConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.jcm.vsch.blocks.entity.PowerfulThrusterBlockEntity;


public class PowerfulThrusterBlock extends ThrusterBlock {

	public PowerfulThrusterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public float getThrottle(BlockState state, int signal) {
		return signal * VSCHConfig.POWERFUL_THRUSTER_STRENGTH.get().intValue();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PowerfulThrusterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (!level.isClientSide()) {
			return (level0, pos0, state0, blockEntity) -> ((PowerfulThrusterBlockEntity)blockEntity).serverTick(level0, pos0, state0, (PowerfulThrusterBlockEntity) blockEntity);
		} else {
			return (level0, pos0, state0, blockEntity) -> ((PowerfulThrusterBlockEntity)blockEntity).clientTick(level0, pos0, state0, (PowerfulThrusterBlockEntity) blockEntity);
		}
	}


}
