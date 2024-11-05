package net.jcm.vsch.blocks.entity;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.ship.ThrusterData.ThrusterMode;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;

public interface ParticleBlockEntity {


	default public void clientTick(Level level, BlockPos pos, BlockState state, ParticleBlockEntity be) {
		tickParticles(level, pos, state);
	}

	default public void serverTick(Level level, BlockPos pos, BlockState state, ParticleBlockEntity be) {
		tickForce(level, pos, state);
	}

	public default void tickForce(Level level, BlockPos pos, BlockState state) {
		// TODO: fix this bad. It both sets the throttle of all thrusters to 0 until a block update

		if (!(level instanceof ServerLevel)) return;

		// ----- Add thruster to the force appliers for the current level ----- //

		//int signal = level.getBestNeighborSignal(pos);
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

		if (ships != null) {
			if (ships.getThrusterAtPos(pos) == null) { 

				ships.addThruster(pos, new ThrusterData(
						VectorConversionsMCKt.toJOMLD(state.getValue(DirectionalBlock.FACING).getNormal()),
						0,
						state.getValue(EnumProperty.create("mode", ThrusterMode.class))
						));

			}
		}

	}

	public void tickParticles(Level level, BlockPos pos, BlockState state);

}
