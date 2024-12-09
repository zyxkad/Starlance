package net.jcm.vsch.compat.create;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import net.minecraft.resources.ResourceLocation;

public class PonderTags {
    public static final PonderTag

    STARLANCE_PONDERS = create("starlance_ponders").item(RegistrateBlocks.THRUSTER_BLOCK.get())
            .defaultLang("Starlance", "Starlance blocks")
            .addToIndex();

    private static PonderTag create(String id) {
        return new PonderTag(Create.asResource(id));
    }

    /**
     * Does nothing, just makes sure the class is loaded
     */
    public static void register() {
        PonderRegistry.TAGS.add(STARLANCE_PONDERS, RegistrateBlocks.THRUSTER_BLOCK.getId());
    }
}
