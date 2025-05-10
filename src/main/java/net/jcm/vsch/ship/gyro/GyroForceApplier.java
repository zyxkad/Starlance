package net.jcm.vsch.ship.gyro;

import net.jcm.vsch.api.force.IVSCHForceApplier;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

public class GyroForceApplier implements IVSCHForceApplier {
	private final GyroData data;
	private final Vector3d torque = new Vector3d();

	public GyroData getData() {
		return this.data;
	}

	public GyroForceApplier(GyroData data){
		this.data = data;
	}

	@Override
	public void applyForces(BlockPos pos, PhysShipImpl physShip) {
		physShip.applyInvariantTorque(this.torque.set(this.data.x, this.data.y, this.data.z));
	}
}
