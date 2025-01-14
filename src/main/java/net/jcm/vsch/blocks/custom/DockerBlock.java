package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.entity.DockerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DockerBlock extends Block implements EntityBlock {
    public DockerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DockerBlockEntity(blockPos,blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? ((level0, pos0, state0, be) -> ((DockerBlockEntity) be).clientTick(level0, pos0, state0, (DockerBlockEntity) be)) : ((level0, pos0, state0, be) -> ((DockerBlockEntity) be).serverTick(level0, pos0, state0, (DockerBlockEntity) be));
    }
}
