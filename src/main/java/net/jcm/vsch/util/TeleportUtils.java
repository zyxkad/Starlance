package net.jcm.vsch.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.core.util.AABBdUtilKt;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.handling.VSEntityHandler;
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager;
import org.valkyrienskies.mod.common.entity.handling.WorldEntityHandler;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeleportUtils {

	/**
	 * See
	 * {@link #DimensionTeleportShip(Ship, ServerLevel, String, double, double, double)
	 * DimensionTeleportShip} for documentation. This overload simply takes in a
	 * Vec3 instead of 3 doubles.
	 */
    @SuppressWarnings("unused")
	public static void DimensionTeleportShip(Ship ship, ServerLevel level, String newDim, Vec3 newPos) {
		DimensionTeleportShip(ship, level, newDim, newPos.x, newPos.y, newPos.z);
	}

	/**
	 * This function took us like a week days to make. You better appreciate it. <br>
	 * </br>
	 * It will teleport the given ship, using the level, to the
	 * dimension with id of newDim at x, y, z. <br>
	 * </br>
	 * But most importantly, it will also teleport any players or entities
	 * (including create contraptions) that are
	 * currently being dragged by the ship to the new dimension, and their correct
	 * position relative to the ship that was moved.
	 *
	 * @param ship   The ship to move
	 * @param level  The ships current level
	 * @param newDim Normal dimension id string format (not VS format)
	 * @param x x position in world to tp the ship to
	 * @param y y position in world to tp the ship to
	 * @param z z position in world to tp the ship to
	 *
	 */
	public static void DimensionTeleportShip(Ship ship, ServerLevel level, String newDim, double x, double y, double z) {

		// ----- Prepare dimension destination ----- //

		// Convert back into a stupid stupid VS dimension string
		String VSnewDimension = VSCHUtils.dimToVSDim(newDim);

		// Prepare ship teleport info for later
		ShipTeleportData teleportData = new ShipTeleportDataImpl(new Vector3d(x, y, z), ship.getTransform().getShipToWorldRotation(), new Vector3d(), new Vector3d(), VSnewDimension, null);

		// ----- AABB magic ----- //

		// Get the AABB of the last tick and the AABB of the current tick
		AABB prevWorldAABB = VectorConversionsMCKt.toMinecraft(VSCHUtils.transformToAABBd(ship.getPrevTickTransform(), ship.getShipAABB())).inflate(10);
		AABB currentWorldAABB = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10);
        AABB shipyardAABB = VectorConversionsMCKt.toMinecraft(AABBdUtilKt.toAABBd(ship.getShipAABB(), new AABBd())).inflate(10);

        // Combine the AABB's into one big one
		//		AABB totalAABB = currentWorldAABB.minmax(prevWorldAABB);
        // ----- Get Ship AABB centers --------- //
        Vec3 oldShipCenter = prevWorldAABB.deflate(10).getCenter();
        Vec3 newoldShipCenter = currentWorldAABB.deflate(10).getCenter();
        Vec3 shipyardCenter = shipyardAABB.deflate(10).getCenter();

		// ---------- //

		// ----- Get all entities BEFORE teleporting ship ----- //

        // Save the distances from entities to the ship for afterwards
        Map<Entity, Vec3> entityOffsets = new HashMap<>();
        Map<Entity, Vec3> shipyardentityOffsets = new HashMap<>();
        Map<ServerShip,Vector3dc> subShipOffsets = new HashMap<>();

		// Save entities that actually need to be teleported


		// Get entities from prev tick and current tick
		List<Entity> prevEntities = level.getEntities(null, prevWorldAABB);
		List<Entity> currentEntities = level.getEntities(null, currentWorldAABB);
        List<Entity> shipyardEntities = level.getEntities(null, prevWorldAABB);
		// Dismount entities
		HashMap<Entity, EntityType<?>> seatedEntities = new HashMap<>();
		seatedEntities.putAll(deSeatEntities(prevEntities));
		seatedEntities.putAll(deSeatEntities(currentEntities));

		// Get offsets
		entityOffsets.putAll(calculateOffsets(prevEntities,oldShipCenter,false));
		entityOffsets.putAll(calculateOffsets(currentEntities,newoldShipCenter,false));
		shipyardentityOffsets.putAll(calculateOffsets(currentEntities,newoldShipCenter,true));


		// ---------- //
        // Old and new aabbs
        entityOffsets.putAll(calculateOffsets(prevEntities,oldShipCenter,false));
        entityOffsets.putAll(calculateOffsets(currentEntities,newoldShipCenter,false));
        // Shipyard entities (Example: paintings, create contraptions)
        shipyardentityOffsets.putAll(calculateOffsets(shipyardEntities,shipyardCenter,true));
        // Sub ships
//        subShipOffsets.putAll(calculateSubShipOffsets(level,VectorConversionsMCKt.toJOML(currentWorldAABB)));
//        teleportShipsToOffsets(subShipOffsets,level,newoldShipCenter,VSnewDimension);
        // ---------- //

		// ----- Teleport ship ----- //

		// Do we actually use this? Eh can't be bothered to check
		// Yes we do. Don't remove this
		ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

		// Teleport ship to new dimension at origin
		ServerShip serverShip = (ServerShip) ship;
		shipWorld.teleportShip(serverShip, teleportData);
		// ---------- //

		// ----- Teleport entities AFTER ship ----- //

		// Get a level object from the VS dimension string of the dim we're going to
		ServerLevel newLevel = VSCHUtils.VSDimToLevel(level.getServer(), VSnewDimension);
		Vec3 newShipCenter = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).getCenter();
		Vec3 newShipyardCenter = VectorConversionsMCKt.toMinecraft(ship.getShipAABB().center(new Vector3d(0, 0, 0)));

        // ------- Actually teleport the entities -------//
        teleportEntitiesToOffsets(entityOffsets,newShipCenter,newLevel);
        teleportEntitiesToOffsets(shipyardentityOffsets,newShipyardCenter,newLevel);
		teleportEntitiesToOffsets(entityOffsets,newShipCenter,newLevel);
		teleportEntitiesToOffsets(shipyardentityOffsets,newShipyardCenter,newLevel);

		// Re-seat entities
		seatedEntities.forEach((sitter, seatType) -> {
			Entity seat = VSCHUtils.getNearestEntityOfType(newLevel, seatType, sitter, 20);
			sitter.startRiding(seat);
		});

	}

	// Returns hashmap of <seatedentity, seatentitytype>
	public static HashMap<Entity, EntityType<?>> deSeatEntities(List<Entity> entities) {

		HashMap<Entity, EntityType<?>> seats = new HashMap<Entity, EntityType<?>>();

		for (Entity entity: entities) {
			if (entity.getVehicle() != null) {
				seats.put(entity, entity.getVehicle().getType());
				// Dismount them
				entity.dismountTo(entity.getX(), entity.getY(), entity.getZ());
			}
		}

		return seats;
	}

	public static HashMap<Entity, Vec3> calculateOffsets(List<Entity> entities, Vec3 center, Boolean shipyard) {

		// Find all entities nearby the ship
		HashMap<Entity, Vec3> offsets = new HashMap<Entity, Vec3>();
		for (Entity entity : entities) {

			VSEntityHandler handler = VSEntityManager.INSTANCE.getHandler(entity);
			if (shipyard) {
				if (handler.getClass() == WorldEntityHandler.class) {


					// Get the offset from the entities position to the ship
					Vec3 entityShipOffset = entity.getPosition(0).subtract(center);

					offsets.put(entity, entityShipOffset);
				}
			} else {
				if (handler.getClass() != WorldEntityHandler.class) {
					// Get the offset from the entities position to the ship
					Vec3 entityShipOffset = entity.getPosition(0).subtract(center);

					offsets.put(entity, entityShipOffset);
				}
			}


		}
		return offsets;
	}

	public static void teleportEntitiesToOffsets(Map<Entity, Vec3> entityOffsets, Vec3 center, ServerLevel newLevel){
		for (Entity entity : entityOffsets.keySet()) {
			if (entity instanceof ServerPlayer) {

				Vec3 shipOffset = entityOffsets.get(entity);
				Vec3 newPosition = center.add(shipOffset);

				// Players need a different teleport command to entities
				((ServerPlayer) entity).teleportTo(newLevel, newPosition.x, newPosition.y, newPosition.z, entity.getYRot(), entity.getXRot());
			} else {
				Vec3 shipOffset = entityOffsets.get(entity);
				Vec3 newPosition = center.add(shipOffset);

                entity.teleportTo(newLevel, newPosition.x, newPosition.y, newPosition.z, null, entity.getYRot(), entity.getXRot());
            }
        }
    }

    public static HashMap<ServerShip,Vector3dc> calculateSubShipOffsets(ServerLevel level, AABBd aabb){
        HashMap<ServerShip, Vector3dc> shipOffsets = new HashMap<>();
        Vector3d center = VectorConversionsMCKt.toJOML(VectorConversionsMCKt.toMinecraft(aabb).getCenter());
        for (Ship ship : VSGameUtilsKt.getAllShips(level).getIntersecting(aabb)) {
            shipOffsets.put((ServerShip) ship,((Vector3d) ship.getTransform().getPositionInWorld()).sub(center));
        }
        return shipOffsets;
    }

    public static void teleportShipsToOffsets(Map<ServerShip,Vector3dc> shipOffsets, ServerLevel level, Vec3 center, String VSnewDimension){
        for (ServerShip ship : shipOffsets.keySet()){
            Vector3dc offset = shipOffsets.get(ship);
            Vector3dc newpos = VectorConversionsMCKt.toJOML(center.add(VectorConversionsMCKt.toMinecraft(offset)));
            ShipTeleportData teleportData = new ShipTeleportDataImpl(newpos, ship.getTransform().getShipToWorldRotation(), new Vector3d(), new Vector3d(), VSnewDimension, null);
            // ----- Teleport ship ----- //
            ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
            shipWorld.teleportShip(ship, teleportData);
        }

    }

}


