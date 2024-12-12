package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.custom.MagnetBlock;
import net.jcm.vsch.blocks.custom.template.BlockEntityWithEntity;
import net.jcm.vsch.entity.MagnetEntity;
import net.jcm.vsch.entity.VSCHEntities;
import net.jcm.vsch.ship.DraggerData;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MagnetBlockEntity extends BlockEntityWithEntity<MagnetEntity> {

	public MagnetBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.DRAG_INDUCER_BLOCK_ENTITY.get(), pos, state);
	}

	// Doesn't get run because doesn't get registered as a ticker yet, it seemed to break stuff
	public void clientTick(Level level, BlockPos pos, BlockState state, MagnetBlockEntity be) {
		//TODO: add particles depending on where we're thrusting? Might need to go in force inducers
	}

	public void serverTick(Level level, BlockPos pos, BlockState state, MagnetBlockEntity blockEntity) {
		tickForce(level, pos, state);
	}

	public void tickForce(Level level, BlockPos pos, BlockState state) {
		// TODO: fix this bad. It both sets the throttle of all draggers to 0 until a block update, and sets them back to default mode.

		if (!(level instanceof ServerLevel)) return;

		// ---- re-spawn entity if needed ----- //
		Vec3 center = pos.getCenter();
		float radius = 0.5f;

		if (level.getEntities(VSCHEntities.MAGNET_ENTITY.get(),
				new AABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius, center.y + radius, center.z + radius),
				entity -> true
		).isEmpty()) {
			spawnLinkedEntity();
		};

		// ----- Add thruster to the force appliers for the current level ----- //

		int signal = level.getBestNeighborSignal(pos);
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

		if (ships != null) {
			/*if (ships.getDraggerAtPos(pos) == null) {

				ships.addDragger(pos, new DraggerData(
						(signal > 0),
						ThrusterData.ThrusterMode.POSITION //The mode currently isn't used
						));

			}*/
		}

	}

	@Override
	public MagnetEntity createLinkedEntity(ServerLevel level) {
		return new MagnetEntity(VSCHEntities.MAGNET_ENTITY.get(), level);
	}
}
