package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.entity.TrailMakerBlockEntity;
import net.jcm.vsch.blocks.entity.TrailMakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TrailMakerBlock extends ThrusterBlock {
    public TrailMakerBlock(Properties properties) {
        super(properties);
    }
    @Override
    public float getThrottle(BlockState state, int signal) {
        return 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrailMakerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide()) {
            return null;
        } else {
            return (level0, pos0, state0, blockEntity) -> ((TrailMakerBlockEntity)blockEntity).clientTick(level0, pos0, state0, (TrailMakerBlockEntity) blockEntity);
        }
    }
}
