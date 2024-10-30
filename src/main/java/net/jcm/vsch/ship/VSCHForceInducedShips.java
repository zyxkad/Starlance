package net.jcm.vsch.ship;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.util.VSCHUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@SuppressWarnings("deprecation")
public class VSCHForceInducedShips implements ShipForcesInducer {

	/**
	 * Don't mess with this unless you know what your doing. I'm making it public for all the people that do know what their doing.
	 * Instead, look at {@link #addThruster(BlockPos, ThrusterData)} or {@link #removeThruster(BlockPos)} or {@link #getThrusterAtPos(BlockPos)}
	 */
	public HashMap<BlockPos, ThrusterData> thrusters = new HashMap<>();

	/**
	 * Don't mess with this unless you know what your doing. I'm making it public for all the people that do know what their doing.
	 * Instead, look at {@link #addDragger(BlockPos, DraggerData)} or {@link #removeDragger(BlockPos)} or {@link #getDraggerAtPos(BlockPos)}
	 */
	public HashMap<BlockPos, DraggerData> draggers = new HashMap<>();

	private String dimensionId = "minecraft:overworld";

	@Override
	public void applyForces(@NotNull PhysShip physicShip) {
		var physShip = (PhysShipImpl) physicShip;
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

			if (VSCHConfig.LIMIT_SPEED.get()) {
				Vector3dc linearVelocity = physShip.getPoseVel().getVel();

				// TODO: Fix this bad. Thrusters won't be able to slow you down if your above max speed.
				if (linearVelocity.get(linearVelocity.maxComponent()) > VSCHConfig.MAX_SPEED.get().intValue()) {
					return;
				}
			}

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


		// Apply draggers force
		draggers.forEach((pos, data) -> {

			Vector3dc linearVelocity = physShip.getPoseVel().getVel();
			Vector3dc angularVelocity = physShip.getPoseVel().getOmega();

			if (!data.on) {
				return;
			}


			// Get position relative to center of mass

			// ChatGPT math, I suck at this stuff lol:
			// Get rotational velocity as the cross product of angular velocity and relative position
			//angularVelocity.cross(relativePosition, rotationalVelocity);

			// Add linear and rotational velocities
			//Vector3d totalVelocity = new Vector3d(linearVelocity).add(rotationalVelocity);

			//Vector3d acceleration = totalVelocity.negate();
			Vector3d acceleration = linearVelocity.negate(new Vector3d());
			Vector3d force = acceleration.mul(physShip.getInertia().getShipMass());

			force = VSCHUtils.clampVector(force, VSCHConfig.MAX_DRAG.get().intValue());

			Vector3d rotAcceleration = angularVelocity.negate(new Vector3d());
			Vector3d rotForce = rotAcceleration.mul(physShip.getInertia().getShipMass());

			rotForce = VSCHUtils.clampVector(rotForce, VSCHConfig.MAX_DRAG.get().intValue());

			physShip.applyInvariantForce(force);
			physShip.applyInvariantTorque(rotForce);

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
