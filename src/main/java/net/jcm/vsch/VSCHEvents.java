package net.jcm.vsch;

import net.jcm.vsch.commands.dimtp;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VSCHEvents {
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel) {
                dimtp.tp((ServerLevel) event.level, event.level);
            }
        }
    }
}
