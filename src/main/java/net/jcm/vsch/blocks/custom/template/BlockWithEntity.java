package net.jcm.vsch.blocks.custom.template;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * For making a block that has an entity always attached (an actual entity)
 * @param <T> The block entity class that is responsible for spawning the entity, removing it, etc
 * @see BlockEntityWithEntity
 */
public abstract class BlockWithEntity<T extends BlockEntityWithEntity<?>> extends DirectionalBlock implements EntityBlock {
    public BlockWithEntity(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            // This block should always have a BE of BlockEntityWithEntity but better safe than sorry?
            if (blockEntity instanceof BlockEntityWithEntity<?> blockEntityWithEntity) {
                blockEntityWithEntity.spawnLinkedEntity();
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!world.isClientSide && state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BlockEntityWithEntity<?> blockEntityWithEntity) {
                blockEntityWithEntity.removeLinkedEntity();
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
