package net.jcm.vsch;

import net.jcm.vsch.entity.VSCHEntities;
import net.lointain.cosmos.network.CosmosModVariables;
import net.jcm.vsch.event.GravityInducer;
import net.jcm.vsch.event.PlanetCollision;
import net.jcm.vsch.event.AtmosphericCollision;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class VSCHEvents {

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		for (ServerLevel level: event.getServer().getAllLevels()) {
			if (level.getRandomPlayer() != null) { // HACKY HACK HACK. TODO: Test multiplayer more
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



