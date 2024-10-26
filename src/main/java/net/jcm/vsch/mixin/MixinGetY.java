package net.jcm.vsch.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.*;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import net.jcm.vsch.VSCHMod;
import net.minecraft.world.level.Level;

@Mixin(VSGameUtilsKt.class)
public abstract class MixinGetY {
	private static final Logger logger = LogManager.getLogger(VSCHMod.MODID);
	@Inject(method = "getYRange", at = @At("HEAD"), cancellable = true)
	@NotNull
	private static void getYRange(@NotNull Level level, CallbackInfoReturnable<LevelYRange> cir) {
		String dimensionName = level.dimension().location().toString();
		if (dimensionName.split(":")[0].equals("cosmos")) {
			logger.info("[VSCH]: Found cosmos dimension: "+dimensionName+". Changing its world height!");
			cir.setReturnValue(VSGameUtilsKt.getYRange(level.getServer().overworld()));
			cir.cancel();
		}
	}
}
