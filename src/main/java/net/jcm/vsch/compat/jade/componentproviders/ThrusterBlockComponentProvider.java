package net.jcm.vsch.compat.jade.componentproviders;

import net.jcm.vsch.blocks.custom.ThrusterBlock;
import net.jcm.vsch.compat.jade.JadeCompat;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ThrusterBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        tooltip.add(Component.translatable("vsch.message.mode").append(Component.translatable("vsch."+accessor.getBlockState().getValue(ThrusterBlock.MODE).toString().toLowerCase())));
    }

    @Override
    public ResourceLocation getUid() {
        return JadeCompat.THRUSTER_BLOCK;
    }
}

