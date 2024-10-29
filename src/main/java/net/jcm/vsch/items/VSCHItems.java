package net.jcm.vsch.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.VSCHTab;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSCHItems {
	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, VSCHMod.MODID); //this bit does some mumbo-jumbo to set up for adding items


	public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench", 
			() -> new Item(new Item.Properties()));



	//registering

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus); //this registers the items with the mod event bus
	}
}
