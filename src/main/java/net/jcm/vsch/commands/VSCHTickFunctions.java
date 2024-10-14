package net.jcm.vsch.commands;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.network.CosmosModVariables.WorldVariables;
import net.jcm.vsch.util.VSCHUtils;

import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;


public class VSCHTickFunctions {

	/**
	 * Checks all VS ships for the given level, if any of them are above their dimensions atmosphere
	 * (as set in a CH datapack), they will be moved to the specified origin in the travel to dimension.
	 * @param level
	 * @param world
	 */
    public static void atmosphericCollisionTick(ServerLevel level, LevelAccessor world) {
    	
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
		// TODO: Gson is bad bad performance change this soon please future me!!
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
                                        
                    String gotoDimension = atmospheric_data.get("travel_to").getAsString();


                    ServerPlayer player = level.getRandomPlayer(); //HACKY HACK HACK. TODO: Test multiplayer
                    //System.out.println(totalAABB);
                    //System.out.println(level.getEntities(null, totalAABB));
                    if (player != null) {
                    	// More debug
                    	System.out.println("Player: "+player.getPosition(0));
                        //System.out.println("Prev: "+prevWorldAABB);
                        //System.out.println("Current: "+currentWorldAABB);
                        //System.out.println("Total: "+totalAABB);
                        
                    }
                    
                    VSCHUtils.DimensionTeleportShip(ship, level, gotoDimension, posX, posY, posZ);
                   
 
                }
            }
        }
    }
}
