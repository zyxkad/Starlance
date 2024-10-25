package net.jcm.vsch.blocks.entity;

import java.text.NumberFormat;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ThrusterBlockEntity extends BlockEntity {

	public ThrusterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.THRUSTER_BLOCK_ENTITY.get(), pos, state);
	}

	public void tick(Level level, BlockPos pos, BlockState state, ThrusterBlockEntity be) {

		Ship ship = VSGameUtilsKt.getShipManagingPos(level,pos);
		// If we aren't on a ship, then we skip
		if (ship == null){return;};

		// Get blockstate direction, NORTH, SOUTH, UP, DOWN, etc
		Direction dir = state.getValue(DirectionalBlock.FACING);

		// BlockPos is always at the corner, getCenter gives us a Vec3 thats centered YAY
		Vec3 center = pos.getCenter();
		// Transform that shipyard pos into a world pos
		Vector3d worldPos = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(center.x, center.y, center.z));

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

		// Get 70% of vel (like I said, its very stronk)
		vel = vel * 0.7;

		double speedX = dir.getStepX() * -vel;
		double speedY = dir.getStepY() * -vel;
		double speedZ = dir.getStepZ() * -vel;

		// Put them all in a nice Vector3d so we can do them all at once
		Vector3d speeds = new Vector3d(speedX, speedY, speedZ);
		// Transform the speeds by the rotation of the ships
		speeds = ship.getTransform().getShipToWorldRotation().transform(speeds, new Vector3d(0, 0, 0));

		// Offset the XYZ by a little bit so its at the end of the thruster block
		double x = worldPos.x - dir.getStepX();
		double y = worldPos.y - dir.getStepY();
		double z = worldPos.z - dir.getStepZ();

		// All that for one particle per tick...
		level.addParticle(
				CosmosModParticleTypes.THRUSTED.get(),
				x, y, z,
				speeds.x, speeds.y, speeds.z
				);

		return;
	}

}
