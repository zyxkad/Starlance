package net.jcm.vsch.compat.create;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import net.jcm.vsch.VSCHMod;

public class VSCHPonderRegistry {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(VSCHMod.MODID);


    public static void register() {

        HELPER.forComponents(
                        VSCHPonderRegistrateBlocks.THRUSTER_BLOCK,
                        VSCHPonderRegistrateBlocks.AIR_THRUSTER_BLOCK,
                        VSCHPonderRegistrateBlocks.POWERFUL_THRUSTER_BLOCK
                )
                .addStoryBoard("thrusters", ThrusterScenes::thrusters)
                .addStoryBoard("thruster_modes", ThrusterScenes::modes);

        HELPER.forComponents(
                VSCHPonderRegistrateBlocks.DRAG_INDUCER_BLOCK
        ).addStoryBoard("drag_inducer", DragInducerScene::inducer);


    }

}
