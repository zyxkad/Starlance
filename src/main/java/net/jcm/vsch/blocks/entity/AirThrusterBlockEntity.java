package net.jcm.vsch.blocks.entity;

import java.util.Iterator;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AirThrusterBlockEntity extends BlockEntity implements ParticleBlockEntity {

	public AirThrusterBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.AIR_THRUSTER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tickParticles(Level level, BlockPos pos, BlockState state) {

		Ship ship = VSGameUtilsKt.getShipManagingPos(level,pos);
		// If we aren't on a ship, then we skip
		if (ship == null){return;};

		// Get blockstate direction, NORTH, SOUTH, UP, DOWN, etc
		Direction dir = state.getValue(DirectionalBlock.FACING);

		// BlockPos is always at the corner, getCenter gives us a Vec3 thats centered YAY
		Vec3 center = pos.getCenter();
		// Transform that shipyard pos into a world pos
		Vector3d worldPos = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(center.x, center.y, center.z));

		//System.out.println("Center: "+center + " " + worldPos);

		// Get the redstone strength
		int signal = level.getBestNeighborSignal(pos);

		// Divide by 15 so we are now between 0 and 1 (vel is very powerful)
		// (Vel is used for speed particles are sent at)
		// IT DIDN'T WORK, TODO: FIND OUT WHY
		double vel = signal;

		// If we are unpowered, do no particles
		if (vel == 0.0) {
			return;
		}

		// Get 0.05% of vel (like I said, its very stronk)
		vel = vel / 120.5;

		double speedX = dir.getStepX() * -vel;
		double speedY = dir.getStepY() * -vel;
		double speedZ = dir.getStepZ() * -vel;

		// Put them all in a nice Vector3d so we can do them all at once
		Vector3d speeds = new Vector3d(speedX, speedY, speedZ);
		// Transform the speeds by the rotation of the ships
		speeds = ship.getTransform().getShipToWorldRotation().transform(speeds, new Vector3d(0, 0, 0));

		int max = 100;

		for (int i = 0; i<max; i++) {
			level.addParticle(
					CosmosModParticleTypes.AIR_THRUST.get(),
					worldPos.x, worldPos.y, worldPos.z,
					speeds.x*0.95, speeds.y*0.95, speeds.z*0.95
					);

		}

	}

	@Override
	public void tickForce(Level level, BlockPos pos, BlockState state) {
		// TODO: fix this bad. It both sets the throttle of all thrusters to 0 until a block update, and sets them back to default mode.

		if (!(level instanceof ServerLevel)) return;

		// ----- Add thruster to the force appliers for the current level ----- //

		//int signal = level.getBestNeighborSignal(pos);
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

		if (ships != null) {
			if (ships.getThrusterAtPos(pos) == null) {
				if(VSCHConfig.THRUSTER_MODE.get().equals("POSITION")) {
					ships.addThruster(pos, new ThrusterData(
							VectorConversionsMCKt.toJOMLD(state.getValue(DirectionalBlock.FACING).getNormal()),
							0,
							ThrusterData.ThrusterMode.POSITION //Position based thruster by default
							));
				} else if (VSCHConfig.THRUSTER_MODE.get().equals("GLOBAL")) {
					ships.addThruster(pos, new ThrusterData(
							VectorConversionsMCKt.toJOMLD(state.getValue(DirectionalBlock.FACING).getNormal()),
							0,
							ThrusterData.ThrusterMode.GLOBAL //Global thruster by default
							));
				}
			}
		}

	}

}
