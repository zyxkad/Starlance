package net.jcm.vsch.compat.create;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.blocks.VSCHBlocks;
import net.jcm.vsch.items.VSCHItems;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import static com.simibubi.create.Create.REGISTRATE;

public class PonderRegistry {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(VSCHMod.MODID);


    public static void register() {



        HELPER.forComponents(
                        RegistrateBlocks.THRUSTER_BLOCK,
                        RegistrateBlocks.AIR_THRUSTER_BLOCK,
                        RegistrateBlocks.POWERFUL_THRUSTER_BLOCK
                )
                .addStoryBoard("thrusters", ThrusterScenes::thrusters);





        /*HELPER.forComponents(CRBlocks.SEMAPHORE)
                .addStoryBoard("semaphore", TrainScenes::signaling);
        HELPER.forComponents(CRBlocks.TRACK_COUPLER)
                .addStoryBoard("coupler", TrainScenes::coupling);
        HELPER.forComponents(CRItems.ITEM_CONDUCTOR_CAP.values())
                .addStoryBoard("conductor", ConductorScenes::constructing)
                .addStoryBoard("conductor_redstone", ConductorScenes::redstoning)
                .addStoryBoard("conductor", ConductorScenes::toolboxing);
        HELPER.forComponents(
                        AllBlocks.ANDESITE_DOOR,
                        AllBlocks.BRASS_DOOR,
                        AllBlocks.COPPER_DOOR,
                        AllBlocks.TRAIN_DOOR,
                        AllBlocks.FRAMED_GLASS_DOOR
                )
                .addStoryBoard("door_modes", DoorScenes::modes);
        HELPER.forComponents(CRBlocks.ANDESITE_SWITCH, CRBlocks.BRASS_SWITCH)
                .addStoryBoard("switch", TrainScenes::trackSwitch);*/

    }

}
