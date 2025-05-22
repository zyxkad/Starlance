package net.jcm.vsch.mixin;

import net.jcm.vsch.accessor.VirtualRenderWorldAccessor;

import net.minecraft.core.Vec3i;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(VirtualRenderWorld.class)
public abstract class MixinVirtualRenderWorld implements VirtualRenderWorldAccessor {
	@Shadow(remap = false)
	@Final
	protected Vec3i biomeOffset;

	@Override
	public Vec3i getBiomeOffset() {
		return this.biomeOffset;
	}
}
