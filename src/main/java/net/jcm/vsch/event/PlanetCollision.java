package net.jcm.vsch.event;

import io.netty.buffer.Unpooled;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.util.VSCHUtils;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.world.inventory.LandingSelectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
public class PlanetCollision {
	public static final Logger logger = LogManager.getLogger(VSCHMod.MODID);
	public static void planetCollisionTick(ServerLevel level, LevelAccessor world) {
		String dimension = level.dimension().location().toString();
		for (Ship ship : VSGameUtilsKt.getAllShips(level)) {

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

			CosmosModVariables.PlayerVariables vars = VSCHUtils.getOrSetPlayerCap(nearestPlayer);
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
			CosmosModVariables.PlayerVariables vars = VSCHUtils.getOrSetPlayerCap(nearestPlayer);

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
				vars.landing_coords = "^";
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
