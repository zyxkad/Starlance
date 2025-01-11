package net.jcm.vsch.blocks.custom.template;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

/**
 * A block entity for spawning and removing an entity with the block.
 * Make sure to overwrite createLinkedEntity.
 * @see BlockWithEntity
 * @param <E> The entity class to be using
 */
public abstract class BlockEntityWithEntity<E extends Entity> extends BlockEntity {
	private UUID entityUUID;

	public BlockEntityWithEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void spawnLinkedEntity() {
		if (level instanceof ServerLevel serverLevel) {
			E entity = createLinkedEntity(serverLevel, worldPosition);
			entity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
			serverLevel.addFreshEntity(entity);
			entityUUID = entity.getUUID();
		}
	}

	public void removeLinkedEntity() {
		if (level instanceof ServerLevel serverLevel && entityUUID != null) {
			Entity entity = serverLevel.getEntity(entityUUID);
			// Don't remove the entity if it doesn't exist, duh
			if (entity != null) {
				// Not sure what removal reason does here :shrug:
				entity.remove(Entity.RemovalReason.DISCARDED);
			}
		}
	}

	/**
	 * The function used by this BE to create the entity object to spawn.
	 * Overwrite with creating a new object of your entity class.
	 * @param level Level, for help creating the entity object
	 * @return
	 */
	public abstract E createLinkedEntity(ServerLevel level, BlockPos pos);

	// Saving and loading the attached entity UUID on world reload:
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (entityUUID != null) {
			tag.putUUID("EntityUUID", entityUUID);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.hasUUID("EntityUUID")) {
			entityUUID = tag.getUUID("EntityUUID");
		}
	}
}
