package net.jcm.vsch.ship;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.jcm.vsch.util.VSCHUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@SuppressWarnings("deprecation")
public class VSCHForceInducedShips implements ShipForcesInducer {

	/**
	 * Don't mess with this unless you know what your doing. I'm making it public for all the people that do know what their doing.
	 * Instead, look at {@link #addThruster(BlockPos, ThrusterData)} or {@link #removeThruster(BlockPos)} or {@link #getThrusterAtPos(BlockPos)}
	 */
	public HashMap<BlockPos, ThrusterData> thrusters = new HashMap<BlockPos, ThrusterData>();

	/**
	 * Don't mess with this unless you know what your doing. I'm making it public for all the people that do know what their doing.
	 * Instead, look at {@link #addDragger(BlockPos, DraggerData)} or {@link #removeDragger(BlockPos)} or {@link #getDraggerAtPos(BlockPos)}
	 */
	public HashMap<BlockPos, DraggerData> draggers = new HashMap<BlockPos, DraggerData>();

	private String dimensionId = "minecraft:overworld";

	@Override
	public void applyForces(PhysShip physShip) {

		// Apply thrusters force
		thrusters.forEach((pos, data) -> {

			// Get current thrust from thruster
			float throttle = data.throttle;
			if (throttle == 0.0f) {
				return;
			}

			// Transform force direction from ship relative to world relative
			Vector3d tForce = physShip.getTransform().getShipToWorld().transformDirection(data.dir, new Vector3d());
			tForce.mul(throttle);

			// Switch between applying force at position and just applying the force
			if (data.mode == ThrusterData.ThrusterMode.POSITION) {
				Vector3d tPos = VectorConversionsMCKt.toJOMLD(pos)
						.add(0.5, 0.5, 0.5, new Vector3d())
						.sub(physShip.getTransform().getPositionInShip());

				physShip.applyInvariantForceToPos(tForce, tPos);

				//ThrusterData.ThrusterMode.GLOBAL should be the only other value:
			} else {
				// Apply the force at no specific position
				physShip.applyInvariantForce(tForce);
			}

		});

		// Prep for draggers
		Vector3dc linearVelocity = physShip.getVelocity();
		Vector3dc angularVelocity = physShip.getOmega();

		// Apply draggers force
		draggers.forEach((pos, data) -> {

			if (!data.on) {
				return;
			}

			// Get position relative to center of mass
			Vector3d relativePosition = new Vector3d(
					pos.getX() - physShip.getCenterOfMass().x(),
					pos.getY() - physShip.getCenterOfMass().y(),
					pos.getZ() - physShip.getCenterOfMass().z()
					);

			// ChatGPT math, I suck at this stuff lol:
			// Get rotational velocity as the cross product of angular velocity and relative position
			Vector3d rotationalVelocity = new Vector3d();
			angularVelocity.cross(relativePosition, rotationalVelocity);

			// Add linear and rotational velocities
			Vector3d totalVelocity = new Vector3d(linearVelocity).add(rotationalVelocity);

			Vector3d acceleration = totalVelocity.negate();
			Vector3d force = acceleration.mul(physShip.getMass());

			force = VSCHUtils.clampVector(force, 15000);


			// ChatGPT math is over
			System.out.println(totalVelocity);
			Vector3d tPos = VectorConversionsMCKt.toJOMLD(pos)
					.add(0.5, 0.5, 0.5, new Vector3d())
					.sub(physShip.getTransform().getPositionInShip());
			physShip.applyInvariantForceToPos(force, tPos);
		});
	}

	// ----- Thrusters ----- //

	public void addThruster(BlockPos pos, ThrusterData data) {
		thrusters.put(pos, data);
	}


	public void removeThruster(BlockPos pos) {
		thrusters.remove(pos);
	}

	@Nullable
	public ThrusterData getThrusterAtPos(BlockPos pos) {
		return thrusters.get(pos);
	}

	// ----- Draggers ----- //

	public void addDragger(BlockPos pos, DraggerData data) {
		draggers.put(pos, data);
	}

	public void removeDragger(BlockPos pos) {
		draggers.remove(pos);
	}

	@Nullable
	public DraggerData getDraggerAtPos(BlockPos pos) {
		return draggers.get(pos);
	}

	// ----- Force induced ships ----- //

	public static VSCHForceInducedShips getOrCreate(ServerShip ship, String dimensionId) {
		VSCHForceInducedShips attachment = ship.getAttachment(VSCHForceInducedShips.class);
		if (attachment == null) {
			attachment = new VSCHForceInducedShips();
			attachment.dimensionId = dimensionId;
			ship.saveAttachment(VSCHForceInducedShips.class, attachment);
		}
		return attachment;
	}

	public static VSCHForceInducedShips getOrCreate(ServerShip ship) {
		return getOrCreate(ship, ship.getChunkClaimDimension());
	}

	public static VSCHForceInducedShips get(Level level, BlockPos pos) {
		// Don't ask, I don't know
		ServerShip ship = (ServerShip) ((VSGameUtilsKt.getShipObjectManagingPos(level, pos) != null) ? VSGameUtilsKt.getShipObjectManagingPos(level, pos) : VSGameUtilsKt.getShipManagingPos(level, pos));
		// Seems counter-intutive at first. But basically, it returns null if it wasn't a ship. Otherwise, it gets the attachment OR creates and then gets it
		return (ship != null) ? getOrCreate(ship) : null;
	}



}
