package net.jcm.vsch.commands;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.network.CosmosModVariables.WorldVariables;
import net.lointain.cosmos.entity.RocketSeatEntity;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
//import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class dimtp {
//    public static ResourceKey<Level> toDimensionKey(String key) {
//        String[] it = key.split(":");
//        ResourceLocation rl = new ResourceLocation(it[it.length - 2], it[it.length - 1]);
//        return ResourceKey.create(Registries.DIMENSION, rl);
//    }

    public static void tp(ServerLevel level, LevelAccessor world) {
        for (Ship ship : VSGameUtilsKt.getAllShips(level)) {
        	// Atmo colision for overworld:
        	// "minecraft:overworld":'{"atmosphere_y":560,"travel_to":"cosmos:solar_sys_d","origin_x":-24100,"origin_y":1000,"origin_z":5100,"overlay_texture_id":"earth_bar","shipbit_y":24,"ship_min_y":120}'
        	System.out.println(ship.getChunkClaimDimension()); 
        	
        	WorldVariables worldVariables = CosmosModVariables.WorldVariables.get(world);
        	CompoundTag atmo_data_map = worldVariables.atmospheric_collision_data_map;
        	
        	// Transform VS's 'minecraft:dimension:namespace:dimension_name' into 'namespace:dimension_name'
        	String shipDim = ship.getChunkClaimDimension();
        	String[] shipDimSplit = shipDim.split(":");
        	String shipDimFixed = shipDimSplit[2] + ":" + shipDimSplit[3];
        	// ---------- //
        	
        	//System.out.println(shipDimFixed);
        	
            if (atmo_data_map.contains(shipDimFixed)) {
            	
            	System.out.println("contains");
            	
            	Tag dim_atmo_data = atmo_data_map.get(shipDimFixed);
            	com.google.gson.JsonObject atmospheric_data = null;
            		
            	if (dim_atmo_data instanceof StringTag _stringTag) {
            		atmospheric_data = new com.google.gson.Gson().fromJson(_stringTag.getAsString(), com.google.gson.JsonObject.class);
            	} else {
            		atmospheric_data = new com.google.gson.JsonObject();
            	}
            	
                if (ship.getTransform().getPositionInWorld().y() > atmospheric_data.get("atmosphere_y").getAsDouble()) {
                    // Teleport time
                    double posX = atmospheric_data.get("origin_x").getAsDouble() + Mth.nextInt(RandomSource.create(), -10, 10);
                    double posY = atmospheric_data.get("origin_y").getAsDouble() + Mth.nextInt(RandomSource.create(), -5, 5);
                    double posZ = atmospheric_data.get("origin_z").getAsDouble() + Mth.nextInt(RandomSource.create(), -10, 10);
                    
                    // TODO: Do we need to convert this back into a 'VS dimension' string of minecraft:dimension:namespace:dimension_name?
                    String dimension = atmospheric_data.get("travel_to").getAsString();

                    ShipTeleportData teleportData = new ShipTeleportDataImpl(
                            new Vector3d(posX, posY, posZ),
                            ship.getTransform().getShipToWorldRotation(),
                            new Vector3d(),
                            new Vector3d(),
                            dimension,
                            null
                    );
                    VSGameUtilsKt.getShipObjectWorld(level)
                            .teleportShip((ServerShip) ship, teleportData);
//                for (Entity entity : level.getEntities(null, VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()))) {
//                    // teleport entity to new dimension and posX/posY/posZ
//                    // Maybe only teleport entities of type ShipMountedToDataProvider?
//                    // if (entity instanceof ShipMountedToDataProvider)
//                }
                }
            }
        }
    }
}
