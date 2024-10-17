package net.jcm.vsch.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.jcm.vsch.VSCHMod;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, VSCHMod.MODID); //this bit does some mumbo-jumbo to set up for adding items
	
	
	//public static final RegistryObject<Item> GRIP_GUN_PART = ITEMS.register("grip_gun_part", 
	//		() -> new Item(new Item.Properties().tab(ModCreativeModeTab.DECARLO_GUN_TAB)));


	
	//registering
	
	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus); //this registers the items with the mod event bus
	}
}
