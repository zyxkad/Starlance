package net.jcm.vsch.compat.create;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;

public class VSCHPonderTags {
    public static final PonderTag

    STARLANCE_PONDERS = create("starlance_ponders").item(VSCHRegistrateBlocks.THRUSTER_BLOCK.get())
            .defaultLang("Starlance", "Starlance blocks")
            .addToIndex();

    private static PonderTag create(String id) {
        return new PonderTag(Create.asResource(id));
    }

    /**
     * Add ponders to the starlance tag here
     */
    public static void register() {
        PonderRegistry.TAGS.forTag(STARLANCE_PONDERS)
                .add(VSCHRegistrateBlocks.THRUSTER_BLOCK.getId())
                .add(VSCHRegistrateBlocks.DRAG_INDUCER_BLOCK.getId());
    }
}