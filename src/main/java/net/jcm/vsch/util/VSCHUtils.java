package net.jcm.vsch.util;

import org.joml.primitives.AABBd;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.util.AABBdUtilKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;


/**
 * The main class where all handy utility functions used by VSCH are stored.
 */
public class VSCHUtils {
	
	/**
	 * Converts a VS dimension id string of <code>'minecraft:dimension:namespace:dimension_name'</code> to a normal
	 * dimension id string of <code>'namespace:dimension_name'</code>
	 * @param dimensionString The VS format dimension id string
	 * @return The converted dimension id string
	 * @author Brickyboy
	 * @see #dimToVSDim(String)
	 */
	public static String VSDimToDim(String VSdimensionString) {
    	// Transform VS's 'minecraft:dimension:namespace:dimension_name' into 'namespace:dimension_name'
    	String[] dimSplit = VSdimensionString.split(":");
    	String dimFixed = dimSplit[2] + ":" + dimSplit[3];
    	return dimFixed;
	}
	
	/**
	 * Converts a normal dimension id string of <code>'namespace:dimension_name'</code> to a 
	 * VS dimension id string <code>'minecraft:dimension:namespace:dimension_name'</code>
	 * @param dimensionString The normal format dimension id string
	 * @return The converted VS dimension id string
	 * @author Brickyboy
	 * @see #VSDimToDim(String)
	 */
	public static String dimToVSDim(String dimensionString) {
        return "minecraft:dimension:" + dimensionString;
	}
	
	/**
	 * Takes in a {@link org.valkyrienskies.core.api.ships.properties.ShipTransform ShipTransform}
	 * and its ship {@link org.joml.primitives.AABBic AABBic} (its <b>shipyard</b> {@link org.joml.primitives.AABBic AABBic}) 
	 * and returns a world-based {@link org.joml.primitives.AABBd AABBd} using the transform
	 * <br></br>
	 * Basically the same as {@link org.valkyrienskies.core.api.ships.Ship#getWorldAABB() Ship#getWorldAABB()}
	 * but can take in a specified transform and ship AABBic
	 * @param transform The ship transform to use
	 * @param shipAABB The <b>shipyard</b> AABBic of the ship
	 * @author Brickyboy
	 * @return The world based AABBd
	 */
	public static AABBd transformToAABBd(ShipTransform transform, AABBic shipAABB) {
		// From AABBic (Int, constant) to AABBd (Double)
		AABBd shipAABBd = AABBdUtilKt.toAABBd(shipAABB, new AABBd());
		// Turn the shipyard AABBd to the world AABBd using the transform
        AABBd worldAABB = shipAABBd.transform(transform.getShipToWorld());
        return worldAABB;
	}
	
	/**
	 * Converts a VS dimension id string and returns a {@link net.minecraft.server.level.ServerLevel ServerLevel} in that dimension.
	 * @param server The {@link net.minecraft.server.MinecraftServer MinecraftServer} object used to get the level
 	 * @param dimensionId The VS dimension id string for the dimension level you want
	 * @return A {@link net.minecraft.server.level.ServerLevel ServerLevel} object in the VS dimension given
	 * @see #dimToVSDim(String)
	 * @todo Add error handling for if string isn't an existing dimension
	 */
	public static ServerLevel VSDimToLevel(MinecraftServer server, String dimensionId) {
		// Split 'minecraft:dimension:namespace:dimension_name' into [minecraft, dimension, namespace, dimension_name]
	    String[] splitText = dimensionId.split(":");
	    
	    // Make a resource location from 'namespace' and 'dimension_name'
	    ResourceLocation rl = new ResourceLocation(splitText[splitText.length - 2], splitText[splitText.length - 1]);
	    
	    // Turn that RL into a Registry Key and use that to get a Level
	    return server.getLevel(ResourceKey.create(Registries.DIMENSION, rl));
	}
	
	/**
	 * Performs multiple checks on an entity to see if it can/should be moved through dimensions with a VS ship.
	 * Checks if the entity:
	 * <ul>
  	 *	<li>Has a {@link org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider IEntityDraggingInformationProvider}</li>
  	 *	<li>Is not riding another entity</li>
  	 *	<li>Is being dragged by a ship (that still exists)</li>
  	 *  <li>Can change dimension. Not sure what this does but its a vanilla function: Entity.canChangeDimensions()</li>
	 * </ul>
	 * @param entity The entity to check
	 * @return True if the entity passed all checks, otherwise False
	 */
	public static boolean CanEntityBeTaken(Entity entity) {
		
		// If the entity has dragging info (they should)
		if (entity instanceof IEntityDraggingInformationProvider) {
			
    		// Use entity dragging info
    		IEntityDraggingInformationProvider dragInfoProv = (IEntityDraggingInformationProvider) entity;
    		EntityDraggingInformation DragInfo = dragInfoProv.getDraggingInformation();
    		
    		// If the entity isn't riding another
    		if (DragInfo.getLastShipStoodOn() != null && entity.getVehicle() == null) {
    			
    			// If the entity has been touched by a ship in the last 10 ticks basically
        		if (DragInfo.isEntityBeingDraggedByAShip()) {
        			
        			// Not sure why this check exists, but its a vanilla function(?) so I'll use it here anyway
        			// If it causes problems in the future, get it out of here
        			if (entity.canChangeDimensions()) {
        				return true;
        			}
        		}
    		}
		}
		return false;
	}

	/**
	 * See {@link #DimensionTeleportShip(Ship, ServerLevel, String, double, double, double) DimensionTeleportShip} for documentation.
	 * This overload simply takes in a Vec3 instead of 3 doubles.
	 */
	public static void DimensionTeleportShip(Ship ship, ServerLevel level, String newDim, Vec3 newPos) {
		DimensionTeleportShip(ship, level, newDim, newPos.x, newPos.y, newPos.z);
	}
	
	/**
	 * This function took us like 3 days to make. You better appreciate it.
	 * <br></br>
	 * It will teleport the given {@link ship}, using the {@link level}, to the dimension with id of {@link newDim} at {@link x}, {@link y}, {@link z}.
	 * <br></br>
	 * But most importantly, it will also teleport any players or entities that are currently being
	 * dragged by the ship to the new dimension, and their correct position relative to the ship that was moved.
	 * @param ship The ship to move
	 * @param level The ships current level
	 * @param newDim Normal dimension id string format (not VS format)
	 * @param x
	 * @param y
	 * @param z
	 * @author Brickyboy
	 */
	public static void DimensionTeleportShip(Ship ship, ServerLevel level, String newDim, double x, double y, double z) {
		
		// ----- Prepare dimension destination ----- //
		
		// Convert back into a stupid stupid VS dimension string
        String VSnewDimension = VSCHUtils.dimToVSDim(newDim);
        
        // Prepare ship teleport info for later
        ShipTeleportData teleportData = new ShipTeleportDataImpl(
                new Vector3d(x, y, z),
                ship.getTransform().getShipToWorldRotation(),
                new Vector3d(),
                new Vector3d(),
                VSnewDimension,
                null
        );
		
		// ----- AABB magic ----- //
		
		// Get the AABB of the last tick and the AABB of the current tick
        AABB prevWorldAABB = VectorConversionsMCKt.toMinecraft(VSCHUtils.transformToAABBd(ship.getPrevTickTransform(), ship.getShipAABB())).inflate(10);
        AABB currentWorldAABB = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10);
        
        // Combine the AABB's into one big one
        AABB totalAABB = currentWorldAABB.minmax(prevWorldAABB);
        
        Vec3 oldShipCenter = prevWorldAABB.deflate(10).getCenter();
        
        // ---------- //
        
		
		 // ----- Get all entities BEFORE teleporting ship ----- //
        
        // Save the distances from entities to the ship for afterwards
        Map<String, Vec3> entityOffsets = new HashMap<String, Vec3>();
        
        // Save entities that actually need to be teleported
        List<Entity> importantEntities = new ArrayList<>();
        
        // Find all entities nearby the ship
        for (Entity entity : level.getEntities(null, totalAABB)) {
        	
        	System.out.println("Entity: "+entity);
        	
        	// A couple checks to make sure they are able to be teleported with the ship
        	if (VSCHUtils.CanEntityBeTaken(entity)) {
        		// Get the offset from the entities position to the ship
				Vec3 entityShipOffset = entity.getPosition(0).subtract(oldShipCenter);
				
				// Save the offset and the entity. Prob don't need two lists here but oh well
				entityOffsets.put(entity.getStringUUID(), entityShipOffset);
        		importantEntities.add(entity);
        	}
        }	
            				
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
		
		for (Entity entity: importantEntities) {
			
			Vec3 shipOffset = entityOffsets.get(entity.getStringUUID());
			Vec3 newPosition = newShipCenter.add(shipOffset);
			
			System.out.println("New info -----");
			System.out.println(entityOffsets);
			System.out.println(newShipCenter);
			System.out.println(newPosition);
			System.out.println("-----");
			
			// Players need a different teleport command to entities
			if (entity instanceof ServerPlayer) {
				System.out.println("Server player");

				((ServerPlayer) entity).teleportTo(newLevel, newPosition.x, newPosition.y, newPosition.z, entity.getYRot(), entity.getXRot());
				
			} else {
				// Teleport non-players
				entity.teleportTo(newLevel, newPosition.x, newPosition.y, newPosition.z, null, entity.getYRot(), entity.getXRot());
			}
		}
	}
}
