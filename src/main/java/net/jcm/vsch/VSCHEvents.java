package net.jcm.vsch;

import net.jcm.vsch.util.VSCHUtils;
import net.jcm.vsch.event.Gravity;
import net.jcm.vsch.event.VSCHTickFunctions;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VSCHEvents {
    /*@SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel) {

            	if (((ServerLevel) event.level).getRandomPlayer() != null) {

            		VSCHTickFunctions.atmosphericCollisionTick((ServerLevel) event.level, event.level);
            		//VSCHTickFunctions.planetCollisionTick((ServerLevel) event.level, event.level);
            	}
            }
        }
    }*/
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		
        if (event.phase == TickEvent.Phase.END) {
        	
            if (event.player.level() instanceof ServerLevel) {
            	System.out.println("player tick");
            	//if (((ServerLevel) event.player.level()).getRandomPlayer() != null) {

            		VSCHTickFunctions.atmosphericCollisionTick((ServerLevel) event.player.level(), event.player.level());
            		//VSCHTickFunctions.planetCollisionTick((ServerLevel) event.level, event.level);
            	//}
            }
        }
    }
    
    @SubscribeEvent
    public static void serverStart(ServerStartedEvent event) {
        Gravity.setAll(event.getServer().overworld());
    }
    
    
}
