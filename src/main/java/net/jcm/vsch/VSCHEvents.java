package net.jcm.vsch;

<<<<<<< HEAD
import net.jcm.vsch.commands.VSCHTickFunctions;
=======
import net.jcm.vsch.commands.dimtp;
import net.jcm.vsch.event.Gravity;
import net.jcm.vsch.util.VSCHUtils;
>>>>>>> main
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class VSCHEvents {
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel) {
<<<<<<< HEAD
            	if (((ServerLevel) event.level).getRandomPlayer() != null) {
            		VSCHTickFunctions.atmosphericCollisionTick((ServerLevel) event.level, event.level);
            	}
=======
                dimtp.tp((ServerLevel) event.level, event.level);

>>>>>>> main
            }
        }
    }
    @SubscribeEvent
    public static void init(ServerStartedEvent event) {
        Gravity.setAll(event.getServer().overworld());
    }

}
