package net.jcm.vsch;

import net.jcm.vsch.compat.CompatMods;
import net.jcm.vsch.compat.create.PonderRegistry;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.valkyrienskies.core.impl.hooks.VSEvents;
import net.jcm.vsch.event.GravityInducer;
import net.jcm.vsch.event.PlanetCollision;
import net.jcm.vsch.event.AtmosphericCollision;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.function.Consumer;


@Mod.EventBusSubscriber
public class VSCHEvents {

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		for (ServerLevel level: event.getServer().getAllLevels()) {
			//System.out.println(level.getPlayers(LivingEntity::isAlive));
			if (level.getRandomPlayer() != null) {
				AtmosphericCollision.atmosphericCollisionTick(level, level);
				PlanetCollision.planetCollisionTick(level, level);

			}
		}
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartedEvent event) {
		GravityInducer.gravitydata = CosmosModVariables.WorldVariables.get(event.getServer().overworld()).gravity_data;
	}


	//	@SubscribeEvent
	//	public static void shipLoad(VSEvents.ShipLoadEvent event) {
	////		Gravity.setAll(event.getServer().overworld());
	//	}


}



