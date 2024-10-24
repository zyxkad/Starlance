package net.jcm.vsch.event;

//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.network.CosmosModVariables.PlayerVariables;
import net.lointain.cosmos.network.CosmosModVariables.WorldVariables;
import net.lointain.cosmos.world.inventory.LandingSelectorMenu;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.util.VSCHUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import io.netty.buffer.Unpooled;

public class VSCHTickFunctions {

	public static final Logger logger = LogManager.getLogger(VSCHMod.MODID);

	/**
	 * Checks all VS ships for the given level, if any of them are above their
	 * dimensions atmosphere (as set in a CH datapack), they will be moved to the
	 * specified origin in the travel to dimension.
	 * 
	 * @param level
	 * @param world
	 */
	public static void atmosphericCollisionTick(ServerLevel level, LevelAccessor world) {

		// Logger logger = LogManager.getLogger(VSCHConfig.MOD_ID);

		for (Ship ship : VSGameUtilsKt.getAllShips(level)) {

			// ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

			// shipWorld.updateDimension(VSGameUtilsKt.getDimensionId(level),null);

			// Atmo collision JSON for overworld:
			// "minecraft:overworld":'{"atmosphere_y":560,"travel_to":"cosmos:solar_sys_d","origin_x":-24100,"origin_y":1000,"origin_z":5100,"overlay_texture_id":"earth_bar","shipbit_y":24,"ship_min_y":120}'

			WorldVariables worldVariables = CosmosModVariables.WorldVariables.get(world);
			CompoundTag atmo_data_map = worldVariables.atmospheric_collision_data_map;

			String shipDim = VSCHUtils.VSDimToDim(ship.getChunkClaimDimension());

			// If our current dimension has atmo data (e.g. a space dimension attached)
			if (atmo_data_map.contains(shipDim)) {

				Tag dim_atmo_data = atmo_data_map.get(shipDim);
				com.google.gson.JsonObject atmospheric_data = new com.google.gson.Gson().fromJson(dim_atmo_data.getAsString(), com.google.gson.JsonObject.class);

				// ----- Convert atmo data into a proper json object ----- //
				// TODO: Gson is bad bad performance change this soon please future me!!
				/*if (dim_atmo_data instanceof CompoundTag _compTag) {
					atmospheric_data = new com.google.gson.Gson().fromJson(_compTag.getAsString(), com.google.gson.JsonObject.class);
				} else {
					atmospheric_data = new com.google.gson.JsonObject();
					// We didn't want your stupid broken !CompoundTag anyway
					//return;
				}*/

				// If the ship is above the planets atmo height:
				if (ship.getTransform().getPositionInWorld().y() > atmospheric_data.get("atmosphere_y").getAsDouble()) {

					// ----- Get destination x, y, z and dimension ----- //
					double posX = atmospheric_data.get("origin_x").getAsDouble(); // + Mth.nextInt(RandomSource.create(), -10, 10)
					double posY = atmospheric_data.get("origin_y").getAsDouble(); // + Mth.nextInt(RandomSource.create(), -5, 5)
					double posZ = atmospheric_data.get("origin_z").getAsDouble(); // + Mth.nextInt(RandomSource.create(), -10, 10)

					String gotoDimension = atmospheric_data.get("travel_to").getAsString();

					/*ServerPlayer player = level.getRandomPlayer(); // HACKY HACK HACK. TODO: Test multiplayer
					// System.out.println(totalAABB);
					// System.out.println(level.getEntities(null, totalAABB));
					if (player != null) {
						// More debug
						System.out.println("Player: " + player.getPosition(0));
						// System.out.println("Prev: "+prevWorldAABB);
						// System.out.println("Current: "+currentWorldAABB);
						// System.out.println("Total: "+totalAABB);

					}*/

					VSCHUtils.DimensionTeleportShip(ship, level, gotoDimension, posX, posY, posZ);

				}
			}
		}
	}

	public static void planetCollisionTick(ServerLevel level, LevelAccessor world) {
		String dimension = level.dimension().location().toString();
		for (Ship ship : VSGameUtilsKt.getAllShips(level)) {

			System.out.println(ship.getChunkClaimDimension());
			AABB currentAABB = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB());
			Vec3 shipCenter = currentAABB.getCenter();

			CompoundTag nearestPlanet = VSCHUtils.getNearestPlanet(world, shipCenter, dimension);



			if (nearestPlanet == null) {
				return;
			}

			// System.out.println(nearestPlanet);


			// Only continue rest of code if this ship is colliding with a planet
			if (!VSCHUtils.isCollidingWithPlanet(nearestPlanet, shipCenter)) {
				playerMenuTick(ship, level, nearestPlanet);
				continue;
			}

			Player nearestPlayer = getShipPlayer(ship, level);

			if (nearestPlayer == null) {
				return;
			}

			PlayerVariables vars = VSCHUtils.getOrSetPlayerCap(nearestPlayer);
			vars.check_collision = false;
			vars.syncPlayerVariables(nearestPlayer);

			// If they don't have the menu already open,
			if (!(nearestPlayer.containerMenu instanceof LandingSelectorMenu)) {
				// Open the menu and disable normal CH collision for them:
				logger.info("[VSCH]: opened menu instead of CH");

				BlockPos _bpos = BlockPos.containing(nearestPlayer.getX(), nearestPlayer.getY(), nearestPlayer.getZ());
				NetworkHooks.openScreen((ServerPlayer) nearestPlayer, new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("LandingSelector");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						return new LandingSelectorMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
					}
				}, _bpos);

			}
			// Otherwise, we just skip them since the playerMenuTick will take care of them.
			playerMenuTick(ship, level, nearestPlanet);


			// System.out.println(isColliding);
			// System.out.println(nearestPlayer);

		}

		// System.out.println("T: ");
		// System.out.println(VSCHUtils.isCollidingWithPlanet(planet, new Vec3(0, 0,
		// 0)));
		// System.out.println(VSCHUtils.isCollidingWithPlanet(planet, new Vec3(0, 1000,
		// 0)));

	}

	public static void playerMenuTick(Ship ship, ServerLevel level, CompoundTag planet) {
		Player nearestPlayer = getShipPlayer(ship, level);

		if (nearestPlayer == null) {
			return;
		}

		if (nearestPlayer.containerMenu instanceof LandingSelectorMenu) {
			PlayerVariables vars = VSCHUtils.getOrSetPlayerCap(nearestPlayer);

			System.out.println(vars.landing_coords);
			if (!vars.landing_coords.equals("^") && !vars.landing_coords.equals("=")) {
				double posX = Double.parseDouble(vars.landing_coords.substring(vars.landing_coords.indexOf("*") + 1, vars.landing_coords.indexOf("|")));
				double posZ = Double.parseDouble(vars.landing_coords.substring(vars.landing_coords.indexOf("|") + 1, vars.landing_coords.indexOf("~")));
				double posY = 550;
				String dimension = planet.getString("travel_to");
				if (dimension == "") {
					logger.error("[VSCH]: Planet has no travel_to dimension. Please report this");
					// We should in theory never get here if I've done my null checks correctly when getting the antennas in the first place
					return;
				}
				logger.info("[VSCH]: Teleporting VS ship into planet!");
				VSCHUtils.DimensionTeleportShip(ship, level, dimension, posX, posY, posZ);
				vars.check_collision = true;
				vars.syncPlayerVariables(nearestPlayer);
			}
		}


	}

	/**
	 * Not a util function because its very specific to planetCollisionTick
	 * Gets a random OR nearest (I'm not sure which) player that is inside the ships AABB and previous AABB.
	 * @param ship
	 * @param level
	 * @return the player found, or null
	 */
	private static Player getShipPlayer(Ship ship, ServerLevel level) {
		// Get the AABB of the last tick and the AABB of the current tick
		AABB prevWorldAABB = VectorConversionsMCKt.toMinecraft(VSCHUtils.transformToAABBd(ship.getPrevTickTransform(), ship.getShipAABB())).inflate(10);
		AABB currentWorldAABB = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10);

		// Combine the AABB's into one big one
		AABB totalAABB = currentWorldAABB.minmax(prevWorldAABB);

		Player nearestPlayer = null;
		// Find all entities nearby the ship
		for (Entity entity : level.getEntities(null, totalAABB)) {
			if (entity instanceof Player player) {
				nearestPlayer = player;
				break;
			}
		}
		return nearestPlayer;
	}
}
