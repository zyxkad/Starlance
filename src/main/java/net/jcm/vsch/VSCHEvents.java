package net.jcm.vsch;

import org.valkyrienskies.core.impl.hooks.VSEvents;
import net.jcm.vsch.event.GravityInducer;
import net.jcm.vsch.event.PlanetCollision;
import net.jcm.vsch.event.AtmosphericCollision;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


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
		GravityInducer.server = event.getServer();
	}

	//	@SubscribeEvent
	//	public static void shipLoad(VSEvents.ShipLoadEvent event) {
	////		Gravity.setAll(event.getServer().overworld());
	//	}

}



