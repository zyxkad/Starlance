package net.jcm.vsch.commands;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.network.CosmosModVariables.WorldVariables;
import net.jcm.vsch.util.VSCHUtils;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class dimtp {

    public static void tp(ServerLevel level, LevelAccessor world) {
    	
    	//Logger logger = LogManager.getLogger(VSCHConfig.MOD_ID);
    	

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
            	if (dim_atmo_data instanceof StringTag _stringTag) {
            		atmospheric_data = new com.google.gson.Gson().fromJson(_stringTag.getAsString(), com.google.gson.JsonObject.class);
            	} else {
            		atmospheric_data = new com.google.gson.JsonObject();
            	}
            	
            	// If the ship is above the planets atmo height:
                if (ship.getTransform().getPositionInWorld().y() > atmospheric_data.get("atmosphere_y").getAsDouble()) {
                    // ----- Get destination x, y, z and dimension ----- //
                    double posX = atmospheric_data.get("origin_x").getAsDouble(); //+ Mth.nextInt(RandomSource.create(), -10, 10)
                    double posY = atmospheric_data.get("origin_y").getAsDouble(); // + Mth.nextInt(RandomSource.create(), -5, 5)
                    double posZ = atmospheric_data.get("origin_z").getAsDouble(); // + Mth.nextInt(RandomSource.create(), -10, 10)
                                        
                    String  gotoDimension = atmospheric_data.get("travel_to").getAsString();
                    
                    // Convert back into a stupid stupid VS dimension string
                    String VSnewDimension = VSCHUtils.dimToVSDim(gotoDimension);
                    
                    ShipTeleportData teleportData = new ShipTeleportDataImpl(
                            new Vector3d(posX, posY, posZ),
                            ship.getTransform().getShipToWorldRotation(),
                            new Vector3d(),
                            new Vector3d(),
                            VSnewDimension,
                            null
                    );
                    
                    // Do we actually use this? Eh can't be bothered to check
                    ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

                    ServerShip serverShip = (ServerShip) ship;
                    
                    
                    // Get the AABB of the last tick and the AABB of the current tick
                    AABB prevWorldAABB = VectorConversionsMCKt.toMinecraft(VSCHUtils.transformToAABBd(ship.getPrevTickTransform(), ship.getShipAABB())).inflate(10);
                    AABB currentWorldAABB = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10);
                    
                    // Combine the AABB's into one big one
                    AABB totalAABB = currentWorldAABB.minmax(prevWorldAABB);
                    
                  

                    ServerPlayer player = level.getRandomPlayer(); //ONLY FOR SINGLEPLAYER DEBUGGING
                    if (player != null) {
                    	// More debug
                    	//System.out.println("Player: "+player.getPosition(0));
                        //System.out.println("Prev: "+prevWorldAABB);
                        //System.out.println("Current: "+currentWorldAABB);
                        //System.out.println("Total: "+totalAABB);
                        //System.out.println(level.getEntities(null, totalAABB));
                    }
                    
                    
                    // TODO: Experiment with saving the level.getEntities from before the ship moved,
                    // But then teleporting all the entities after its moved
	                for (Entity entity : level.getEntities(null, totalAABB)) {
	                	
	                	System.out.println("Entity: "+entity);
	                	
	                	// If the entity has dragging info (they should)
	                	if (entity instanceof IEntityDraggingInformationProvider) {
	                		// Use entity dragging info
	                		IEntityDraggingInformationProvider dragInfoProv = (IEntityDraggingInformationProvider) entity;
	                		EntityDraggingInformation DragInfo = dragInfoProv.getDraggingInformation();
	                		
	                		// If the entity isn't riding another
	                		if (DragInfo.getLastShipStoodOn() != null && entity.getVehicle() == null) {
	                			
		                		if (DragInfo.isEntityBeingDraggedByAShip()) {
		                			
		                			// Not sure why this check exists, but its a vanilla function(?) so I'll use it here anyway
		                			// If it causes problems in the future, get it out of here
		                			if (entity.canChangeDimensions()) {
		                				
		                				// Get a level object from the VS dimension string of the dim we're going to
		                				ServerLevel newLevel = VSCHUtils.VSDimToLevel(level.getServer(), VSnewDimension);
		                				
		                				// Lotta debug...
		                				//System.out.println(VSnewDimension);
		                				//System.out.println(newLevel.dimension());
		                				//System.out.println(newLevel.dimensionTypeId());
		                				
		                				
		                				if (entity instanceof ServerPlayer) {
		                					System.out.println("Server player");
		                					
		                					// Position is wrong
		                					((ServerPlayer) entity).teleportTo(newLevel, entity.getX(), entity.getY(), entity.getZ(), 0, 0);//.changeDimension(newLevel);
		                				} else {
		                					// NOT TESTED
		                					entity.changeDimension(newLevel);
		                				}
		                				
		                			}
		                		}
	                		};
	                		
	                	} else {
	                		// The entity doesn't have a dragging info provider? Idk its VS's problem now
	                		//logger.info("Something went very wrong on VS for an entity, ignoring it");
	                	}

	                }
	                shipWorld.teleportShip(serverShip, teleportData);
                }
            }
        }
    }
}
