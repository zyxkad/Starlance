package net.jcm.vsch;

import net.jcm.vsch.blocks.ThrusterBlock;
import net.jcm.vsch.config.VSCHConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(VSCHConfig.MOD_ID)
public class VSCHMod {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VSCHConfig.MOD_ID);
    public static final RegistryObject<Block> SAD_BLOCK = BLOCKS.register("thruster",
            () -> new ThrusterBlock(Block.Properties.copy(Blocks.IRON_BLOCK)));
    public VSCHMod() {
        // Initialize logic here
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);


    }
}
