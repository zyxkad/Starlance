package net.jcm.vsch.blocks.custom;


import net.jcm.vsch.blocks.custom.template.ThrusterBlock;
import net.jcm.vsch.config.VSCHConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.jcm.vsch.blocks.entity.AirThrusterBlockEntity;
import net.jcm.vsch.util.rot.DirectionalShape;
import net.jcm.vsch.util.rot.RotShape;
import net.jcm.vsch.util.rot.RotShapes;


public class AirThrusterBlock extends ThrusterBlock {
	//TODO: fix this bounding box
	private static final RotShape SHAPE = RotShapes.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
	private final DirectionalShape Thruster_SHAPE = DirectionalShape.down(SHAPE);

	public AirThrusterBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getThrottle(BlockState state, int signal) {
		return signal * VSCHConfig.AIR_THRUSTER_STRENGTH.get().intValue();
	}
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Thruster_SHAPE.get(state.getValue(BlockStateProperties.FACING));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AirThrusterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (!level.isClientSide()) {
			return (level0, pos0, state0, blockEntity) -> ((AirThrusterBlockEntity)blockEntity).serverTick(level0, pos0, state0, (AirThrusterBlockEntity) blockEntity);
		} else {
			return (level0, pos0, state0, blockEntity) -> ((AirThrusterBlockEntity)blockEntity).clientTick(level0, pos0, state0, (AirThrusterBlockEntity) blockEntity);
		}
	}


}
