package net.jcm.vsch;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VSCHEvents {
    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event) {
        //shiptp.registerServerCommands(event.getDispatcher());
    }

}
