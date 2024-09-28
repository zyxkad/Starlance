package net.jcm.vsch.commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.entity.RocketSeatEntity;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class AtmosphericCollisionDetectorProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null)
            return;
        ListTag objectList;
        com.google.gson.JsonObject data = new com.google.gson.JsonObject();
        com.google.gson.JsonObject dimentional_data = new com.google.gson.JsonObject();
        com.google.gson.JsonObject atmospheric_data = new com.google.gson.JsonObject();
        String texts = "";
        String dimesnion = "";
        double posX = 0;
        double posY = 0;
        double posZ = 0;
        objectList = CosmosModVariables.MapVariables.get(world).subjectList;
        for (Tag dataelementiterator : objectList) {
            texts = dataelementiterator instanceof StringTag _stringTag ? _stringTag.getAsString() : "";
            data = dataelementiterator instanceof StringTag _stringTag ? new com.google.gson.Gson().fromJson(_stringTag.getAsString(), com.google.gson.JsonObject.class) : new com.google.gson.JsonObject();
            if ((entity.getVehicle()) instanceof RocketSeatEntity) {
                if ((entity.level().dimension().location().toString()).equals(data.get("attached_dimention_id").getAsString())) {
                    if (data.has("dimensional_data")) {
                        dimentional_data = data.get("dimensional_data").getAsJsonObject();
                        if (dimentional_data.has("atmospheric_data")) {
                            atmospheric_data = dimentional_data.get("atmospheric_data").getAsJsonObject();
                            if (entity.getY() > atmospheric_data.get("atmosphere_y").getAsDouble()) {
                                dimesnion = atmospheric_data.get("travel_to").getAsString();
                                posX = atmospheric_data.get("origin_x").getAsDouble();
                                posY = atmospheric_data.get("origin_y").getAsDouble();
                                posZ = atmospheric_data.get("origin_z").getAsDouble();
                                // ur teleportation method here posx,y,z ar position to teleport to in the dimension id of the text variable (dimension)
                            }
                        }
                    }
                }
            }
        }
    }

    public static void fanumTaxKaiCenat(ServerLevel level) {
        for (Ship ship : VSGameUtilsKt.getAllShips(level)) {
            if (ship.getTransform().getPositionInWorld().y() > atmospheric_data.get("atmosphere_y").getAsDouble()) {
                // Teleport time
                String dimension = atmospheric_data.get("travel_to").getAsString();
                double posX = atmospheric_data.get("origin_x").getAsDouble();
                double posY = atmospheric_data.get("origin_y").getAsDouble();
                double posZ = atmospheric_data.get("origin_z").getAsDouble();

                // TODO: Need to get level somehow
                Level destLevel = TODO();
                ShipTeleportDataImpl teleportData = ShipTeleportDataImpl(
                        Vector3d(posX, posY, posZ),
                        shipObject.getTransform().getShipToWorldRotation(),
                        new Vector3d(),
                        new Vector3d(),
                        VSGameUtilsKt.getDimensionId(destLevel),
                        null
                );
                VSGameUtilsKt.getShipObjectWorld(level.getServer())
                        .teleportShip(ship as ServerShip, teleportData);

                for (Entity entity : level.getEntities(null, VectorConversionsMCKt.toMinecraft(ship.getWorldAABB())) {
                    // teleport entity to new dimension and posX/posY/posZ
                    // Maybe only teleport entities of type ShipMountedToDataProvider?
                    // if (entity instanceof ShipMountedToDataProvider)
                }
            }
        }
    }
}