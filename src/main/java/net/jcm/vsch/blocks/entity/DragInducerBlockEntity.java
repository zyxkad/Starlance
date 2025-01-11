package net.jcm.vsch.blocks.entity;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.ship.DraggerData;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DragInducerBlockEntity extends BlockEntity {

	public DragInducerBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.DRAG_INDUCER_BLOCK_ENTITY.get(), pos, state);
	}

	// Doesn't get run because doesn't get registered as a ticker yet, it seemed to break stuff
	public void clientTick(Level level, BlockPos pos, BlockState state, DragInducerBlockEntity be) {
		//TODO: add particles depending on where we're thrusting? Might need to go in force inducers
	}

	public void serverTick(Level level, BlockPos pos, BlockState state, DragInducerBlockEntity blockEntity) {
		tickForce(level, pos, state);
	}

	public static void tickForce(Level level, BlockPos pos, BlockState state) {
		// TODO: fix this bad. It both sets the throttle of all draggers to 0 until a block update, and sets them back to default mode.

		if (!(level instanceof ServerLevel)) return;

		// ----- Add thruster to the force appliers for the current level ----- //

		int signal = level.getBestNeighborSignal(pos);
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

		if (ships != null) {
			if (ships.getDraggerAtPos(pos) == null) {
				ships.addDragger(pos, new DraggerData(
						signal > 0,
						ThrusterData.ThrusterMode.POSITION //The mode currently isn't used
						));

			}
		}
	}
}
