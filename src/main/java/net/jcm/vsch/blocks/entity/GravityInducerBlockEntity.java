package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.entity.template.ParticleBlockEntity;
import net.jcm.vsch.config.VSCHConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;

public class GravityInducerBlockEntity extends BlockEntity implements ParticleBlockEntity {

	public GravityInducerBlockEntity(BlockPos pos, BlockState blockState) {
		super(VSCHBlockEntities.GRAVITY_INDUCER_BLOCK_ENTITY.get(), pos, blockState);
	}

	public Vec3 getForce() {
		return new Vec3(0, 1, 0);
	}

	@Override
	public void tickForce(ServerLevel level, BlockPos pos, BlockState state) {
		LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);
		if (ship == null) {
			return;
		}
		Vec3 force = getForce();
		List<Entity> entities = level.getEntities(null, VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()));
		for (Entity entity : entities) {
			if (entity.noPhysics) {
				continue;
			}
			if (entity instanceof ServerPlayer player) {
				if (player.getAbilities().flying) {
					continue;
				}
			}

			entity.setDeltaMovement(entity.getDeltaMovement().add(force));
		}
	}

	@Override
	public void tickParticles(Level level, BlockPos pos, BlockState state) {
	}
}
