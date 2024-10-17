package net.jcm.vsch.ship;

import java.util.HashMap;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@SuppressWarnings("deprecation")
public class VSCHForceInducedShips implements ShipForcesInducer {
	
	private HashMap<BlockPos, ThrusterData> thrusters = new HashMap<BlockPos, ThrusterData>();
	private String dimensionId = "minecraft:overworld";
	        
	@Override
	public void applyForces(PhysShip physShip) {
		// TODO Auto-generated method stub
		//System.out.println(thrusters);
		thrusters.forEach((pos, data) -> {
			// pos = dir, force = throttle
			float force = data.throttle;
			if (force == 0.0f) {
                return;
            }

            Vector3d tForce = physShip.getTransform().getShipToWorld().transformDirection(data.dir, new Vector3d());
            tForce.mul(force);
            Vector3d tPos = VectorConversionsMCKt.toJOMLD(pos)
                .add(0.5, 0.5, 0.5, new Vector3d())
                .sub(physShip.getTransform().getPositionInShip());

            physShip.applyInvariantForceToPos(tForce, tPos);
		});
	}
	
	public void addThruster(BlockPos pos, ThrusterData data) {
		thrusters.put(pos, data);
	}
	
	public void removeThruster(BlockPos pos) {
		thrusters.remove(pos);
	}
	
	public ThrusterData getThrusterAtPos(BlockPos pos) {
		return thrusters.get(pos);
	}
	        
	
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
