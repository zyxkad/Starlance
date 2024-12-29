package net.jcm.vsch.compat.create;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import net.jcm.vsch.VSCHMod;

public class VSCHPonderRegistry {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(VSCHMod.MODID);


    public static void register() {



        HELPER.forComponents(
                        VSCHRegistrateBlocks.THRUSTER_BLOCK,
                        VSCHRegistrateBlocks.AIR_THRUSTER_BLOCK,
                        VSCHRegistrateBlocks.POWERFUL_THRUSTER_BLOCK
                )
                .addStoryBoard("thrusters", ThrusterScenes::thrusters)
                .addStoryBoard("thruster_modes", ThrusterScenes::modes);

        HELPER.forComponents(
                VSCHRegistrateBlocks.DRAG_INDUCER_BLOCK
        ).addStoryBoard("drag_inducer", DragInducerScene::inducer);



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
