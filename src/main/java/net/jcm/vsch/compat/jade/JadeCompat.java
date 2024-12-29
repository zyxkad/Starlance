package net.jcm.vsch.compat.jade;

import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.blocks.custom.template.ThrusterBlock;
import net.jcm.vsch.compat.jade.componentproviders.ThrusterBlockComponentProvider;
import net.jcm.vsch.ship.ThrusterData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import snownee.jade.api.*;


@WailaPlugin
public class JadeCompat implements IWailaPlugin {
    public static final EnumProperty<ThrusterData.ThrusterMode> MODE = EnumProperty.create("mode", ThrusterData.ThrusterMode.class);

    public static final ResourceLocation THRUSTER_BLOCK = new ResourceLocation(VSCHMod.MODID,"thruster_component_config");

    @Override
    public void register(IWailaCommonRegistration registration) {
        //TODO register data providers
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ThrusterBlockComponentProvider.INSTANCE, ThrusterBlock.class);
//        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
//            if (hitResult==null){return accessor;}
//            if (hitResult.getType() == HitResult.Type.BLOCK) {
//                // Get block state
//                BlockHitResult blockHit = (BlockHitResult) hitResult;
//                BlockPos blockPos = blockHit.getBlockPos();
//                if (accessor == null){return null;};
//                Level level = accessor.getLevel();
//                BlockState blockState = level.getBlockState(blockPos);
//                return registration.blockAccessor().blockState(blockState).build();
//            }
//            return accessor;
//        });
    }
}
