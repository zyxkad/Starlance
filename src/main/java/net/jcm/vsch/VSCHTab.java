package net.jcm.vsch;

import net.jcm.vsch.blocks.VSCHBlocks;
import net.jcm.vsch.blocks.custom.ThrusterBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.core.registries.Registries.ITEM;

public class VSCHTab {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VSCHMod.MODID);
	public static final RegistryObject<CreativeModeTab> TAB = REGISTRY.register("starlance",
			() -> CreativeModeTab.builder().title(Component.translatable("vsch.itemtab")).icon(() -> new ItemStack(VSCHBlocks.THRUSTER_BLOCK.get())).displayItems((parameters, tabData) -> {

				tabData.accept(VSCHBlocks.THRUSTER_BLOCK.get().asItem());
				tabData.accept(VSCHBlocks.DRAG_INDUCER_BLOCK.get().asItem());
				tabData.accept(VSCHBlocks.AIR_THRUSTER_BLOCK.get().asItem());

			}).build());

	public static void register(IEventBus eventBus) {
		REGISTRY.register(eventBus);
	}
}
