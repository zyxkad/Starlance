package net.jcm.vsch.mixin;

import net.lointain.cosmos.entity.RocketSeatEntity;
import net.lointain.cosmos.procedures.ApplyGravityLogicProcedure;
import net.lointain.cosmos.procedures.FrictionDataProviderProcedure;
import net.lointain.cosmos.procedures.GravityDataProviderProcedure;
import net.lointain.cosmos.procedures.LivingEntityProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import org.checkerframework.common.reflection.qual.Invoke;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntityProcedure.class)
public class MixinLivingEntityProcedure {
    /*@Inject(method = "execute", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
    ), cancellable = true)
    private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, CallbackInfo cir) {

    }*/
}
