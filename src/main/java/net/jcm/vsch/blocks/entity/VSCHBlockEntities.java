package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.blocks.VSCHBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSCHBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
			DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VSCHMod.MODID);

	public static final RegistryObject<BlockEntityType<ThrusterBlockEntity>> THRUSTER_BLOCK_ENTITY =
			BLOCK_ENTITIES.register("thruster_block",
					() -> BlockEntityType.Builder.of(ThrusterBlockEntity::new, VSCHBlocks.THRUSTER_BLOCK.get())
					.build(null));

	public static void register(IEventBus eventBus) {
		BLOCK_ENTITIES.register(eventBus);
	}
}
