package net.jcm.vsch;

import net.jcm.vsch.util.VSCHUtils;

import java.util.function.Predicate;

import net.jcm.vsch.event.Gravity;
import net.jcm.vsch.event.VSCHTickFunctions;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3d;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mod.EventBusSubscriber
public class VSCHEvents {

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		for (ServerLevel level: event.getServer().getAllLevels()) {
			//System.out.println(level.getPlayers(LivingEntity::isAlive));
			if (level.getRandomPlayer() != null) {
				VSCHTickFunctions.atmosphericCollisionTick(level, level);
				VSCHTickFunctions.planetCollisionTick(level, level);
			}
		}
	}

	@SubscribeEvent
	public static void serverStart(ServerStartedEvent event) {
		Gravity.setAll(event.getServer().overworld());
	}


}
