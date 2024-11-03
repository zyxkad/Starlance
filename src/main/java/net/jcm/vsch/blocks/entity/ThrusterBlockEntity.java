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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;

public class ThrusterBlockEntity extends BlockEntity implements ParticleBlockEntity  {

	// VERY JANKY but hey we had to get v1 out somehow right
	public ThrusterBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.THRUSTER_BLOCK_ENTITY.get(), pos, state);

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
		vel = vel / 25;

		double speedX = dir.getStepX() * -vel;
		double speedY = dir.getStepY() * -vel;
		double speedZ = dir.getStepZ() * -vel;

		// Put them all in a nice Vector3d so we can do them all at once
		Vector3d speeds = new Vector3d(speedX, speedY, speedZ);
		// Transform the speeds by the rotation of the ships
		speeds = ship.getTransform().getShipToWorldRotation().transform(speeds, new Vector3d(0, 0, 0));

		double offsetX = dir.getStepX();
		double offsetY = dir.getStepY();
		double offsetZ = dir.getStepZ();

		Vector3d offset = new Vector3d(offsetX, offsetY, offsetZ);
		offset = ship.getTransform().getShipToWorldRotation().transform(offset, new Vector3d(0, 0, 0));

		// Offset the XYZ by a little bit so its at the end of the thruster block
		double x = worldPos.x - offset.x;// - dir.getStepX();
		double y = worldPos.y - offset.y;// - dir.getStepY();
		double z = worldPos.z - offset.z;// - dir.getStepZ();

		// All that for one particle per tick...
		level.addParticle(
				CosmosModParticleTypes.THRUSTED.get(),
				x, y, z,
				speeds.x, speeds.y, speeds.z
				);

		speeds = speeds.mul(1.06);

		// Ok ok, two particles per tick
		level.addParticle(
				CosmosModParticleTypes.THRUST_SMOKE.get(),
				x, y, z,
				speeds.x, speeds.y, speeds.z
				);

		return;
	}

}
