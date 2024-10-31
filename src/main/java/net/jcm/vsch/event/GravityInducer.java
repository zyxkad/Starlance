package net.jcm.vsch.event;

import net.jcm.vsch.VSCHMod;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import net.jcm.vsch.util.VSCHUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@SuppressWarnings("deprecation")
public class GravityInducer implements ShipForcesInducer {
	public static final Logger logger = LogManager.getLogger(VSCHMod.MODID);
	public ServerShip ship;
    public static CompoundTag gravitydata;
	//    public HashMap<String, Float> dimensions = new HashMap<String, Float>();

	@Override
	public void applyForces(@NotNull PhysShip physShip) {
		if (gravitydata == null) {
			return;
		}
		double gravity;
		FloatTag gravity_data;
		if (ship == null) {
			return;
		}

		if(gravitydata.getAllKeys().contains(VSCHUtils.VSDimToDim(ship.getChunkClaimDimension()))){
			gravity_data = (FloatTag) gravitydata.get(VSCHUtils.VSDimToDim(ship.getChunkClaimDimension()));
			if(gravity_data == null){return;}
			gravity = (1-gravity_data.getAsFloat())*10*((PhysShipImpl) physShip).get_inertia().getShipMass();
			try {
				physShip.applyInvariantForce(new Vector3d(0,gravity,0));
			} catch (Exception e) {
				logger.error("Gravity Inducer Failed due to {} on ship {}", e, ship.getSlug());
			}
		}
	}




	public static GravityInducer getOrCreate(ServerShip ship) {//, MinecraftServer server) {
		GravityInducer attachment = ship.getAttachment(GravityInducer.class);
		if (attachment == null) {
			attachment = new GravityInducer();
			attachment.ship = ship;
			ship.saveAttachment(GravityInducer.class, attachment);
		}
		if (attachment.ship == null) {
			attachment.ship = ship;
		}
		return attachment;
	}

	/*public static void addToAllShips(Level level){
		for (Ship ship : VSGameUtilsKt.getAllShips(level)) {
			GravityInducer.getOrCreate((ServerShip) ship,level.getServer());
		}
	}*/


	//    public static net.jcm.vsch.ship.VSCHForceInducedShips get(Level level, BlockPos pos) {
	//        // Don't ask, I don't know
	//        ServerShip ship = (ServerShip) ((VSGameUtilsKt.getShipObjectManagingPos(level, pos) != null) ? VSGameUtilsKt.getShipObjectManagingPos(level, pos) : VSGameUtilsKt.getShipManagingPos(level, pos));
	//        // Seems counter-intutive at first. But basically, it returns null if it wasn't a ship. Otherwise, it gets the attachment OR creates and then gets it
	//        return (ship != null) ? getOrCreate(ship) : null;
	//    }
}



