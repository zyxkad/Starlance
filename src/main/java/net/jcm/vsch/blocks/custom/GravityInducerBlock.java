package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.entity.GravityInducerBlockEntity;
import net.jcm.vsch.util.rot.DirectionalShape;
import net.jcm.vsch.util.rot.RotShape;
import net.jcm.vsch.util.rot.RotShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GravityInducerBlock extends Block implements EntityBlock {

    private static final RotShape SHAPE = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private final DirectionalShape GRAVITYSHAPE = DirectionalShape.south(SHAPE);
    
    public GravityInducerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GravityInducerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide()) {
            return (level0, pos0, state0, blockEntity) -> ((GravityInducerBlockEntity)blockEntity).serverTick(level0, pos0, state0, (GravityInducerBlockEntity) blockEntity);
        } else {
            return (level0, pos0, state0, blockEntity) -> ((GravityInducerBlockEntity)blockEntity).clientTick(level0, pos0, state0, (GravityInducerBlockEntity) blockEntity);
        }
    }
}
