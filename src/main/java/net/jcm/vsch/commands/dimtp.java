package net.jcm.vsch.commands;
import net.jcm.vsch.config.VSCHConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
//import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.network.CosmosModVariables.WorldVariables;
import net.jcm.vsch.util.VSCHUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;


public class dimtp {
    public static void tp(ServerLevel level, LevelAccessor world) {

        for (Ship ship : VSGameUtilsKt.getAllShips(level)) {
        	// Atmo collision JSON for overworld:
        	// "minecraft:overworld":'{"atmosphere_y":560,"travel_to":"cosmos:solar_sys_d","origin_x":-24100,"origin_y":1000,"origin_z":5100,"overlay_texture_id":"earth_bar","shipbit_y":24,"ship_min_y":120}'
        	
        	WorldVariables worldVariables = CosmosModVariables.WorldVariables.get(world);
        	CompoundTag atmo_data_map = worldVariables.atmospheric_collision_data_map;
        	
        	String shipDim = VSCHUtils.VSDimToDim(ship.getChunkClaimDimension());

        	// If our current dimension has atmo data (e.g. a space dimension attached)
            if (atmo_data_map.contains(shipDim)) {
            	            	
            	Tag dim_atmo_data = atmo_data_map.get(shipDim);
            	com.google.gson.JsonObject atmospheric_data = null;
            	
            	// ----- Convert atmo data into a proper json object ----- //
//            	if (dim_atmo_data instanceof Tag _stringTag) {
				atmospheric_data = new com.google.gson.Gson().fromJson(dim_atmo_data.getAsString(), com.google.gson.JsonObject.class);
//            	} else {
//            		atmospheric_data = new com.google.gson.JsonObject();
//            	}

            	// If the ship is above the planets atmo height:
                if (ship.getTransform().getPositionInWorld().y() > atmospheric_data.get("atmosphere_y").getAsDouble()) {
                    // ----- Get destination x, y, z and dimension ----- //
                    double posX = atmospheric_data.get("origin_x").getAsDouble(); //+ Mth.nextInt(RandomSource.create(), -10, 10)
                    double posY = atmospheric_data.get("origin_y").getAsDouble(); // + Mth.nextInt(RandomSource.create(), -5, 5)
                    double posZ = atmospheric_data.get("origin_z").getAsDouble(); // + Mth.nextInt(RandomSource.create(), -10, 10)
                                        
                    String gotoDimension = atmospheric_data.get("travel_to").getAsString();
                    
                    // Convert back into a stupid stupid VS dimension string
                    String VSnewDimension = VSCHUtils.dimToVSDim(gotoDimension);
                    
                    // Prepare ship teleport info for later
                    ShipTeleportData teleportData = new ShipTeleportDataImpl(
                            new Vector3d(posX, posY, posZ),
                            ship.getTransform().getShipToWorldRotation(),
                            new Vector3d(),
                            new Vector3d(),
                            VSnewDimension,
                            null
                    );
                    
                    // Do we actually use this? Eh can't be bothered to check
					// Jcm: We do.
                    ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

                    
                    
                    
                    // Get the AABB of the last tick and the AABB of the current tick
                    AABB prevWorldAABB = VectorConversionsMCKt.toMinecraft(VSCHUtils.transformToAABBd(ship.getPrevTickTransform(), ship.getShipAABB())).inflate(10);
                    AABB currentWorldAABB = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10);
                    
                    // Combine the AABB's into one big one
                    AABB totalAABB = currentWorldAABB.minmax(prevWorldAABB);
                    
                    Vec3 oldShipCenter = prevWorldAABB.deflate(10).getCenter();
                    
                    

                    //ServerPlayer player = level.getRandomPlayer(); //ONLY FOR SINGLEPLAYER DEBUGGING
                    //if (player != null) {
                    	// More debug

                    	//VSCHMod.logger.debug("Player: "+player.getPosition(0));
                        //VSCHMod.logger.debug("Prev: "+prevWorldAABB);
                        //VSCHMod.logger.debug("Current: "+currentWorldAABB);
                        //VSCHMod.logger.debug("Total: "+totalAABB);
                        //VSCHMod.logger.debug(level.getEntities(null, totalAABB));
                    //}
                    
                    
                    // ----- Get all entities BEFORE teleporting ship ----- //
                    
                    // Save the distances from entities to the ship to use later
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
        					// NOT TESTED
        					// Have to teleport before changing dimension, because changeDimension
        					// makes a new Entity object and removes old one
        					System.out.println(entity);
        					//entity.teleportTo(newPosition.x, newPosition.y, newPosition.z);
        					//System.out.println(entity);
        					entity.teleportTo(newLevel, newPosition.x, newPosition.y, newPosition.z, null, entity.getYRot(), entity.getXRot());
        					//entity.changeDimension(newLevel, (ITeleporter) new PortalInfo(entity.position(), newPosition, entity.getYRot(), entity.getXRot()));
        				}
    				}
 
                }
            }
        }
    }
}
