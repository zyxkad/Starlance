package net.jcm.vsch.items.custom;

import net.jcm.vsch.config.VSCHConfig;
import net.lointain.cosmos.item.SteelarmourItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagnetBootItem extends ArmorItem {

    public MagnetBootItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    @SuppressWarnings("removal")
    public void onArmorTick(ItemStack stack, Level level, Player player) {

        // Ignore spectator mode
        // I don't exactly know what this var does, but it trigger in spectator mode.
        // If it causes problems, replace it with 'isSpectator()'
        if (player.noPhysics) {
            return;
        }

        // I don't know why there isn't a simpler check for this
        if (player.getAbilities().flying) {
            return;
        }

        boolean magnetOn = true; //TODO: make this a keybind that can toggle it on and off

        double maxDistance = VSCHConfig.MAGNET_BOOT_DISTANCE.get().doubleValue();

        Vec3 startPos = player.position(); // Starting position (player's position)
        Vec3 endPos = startPos.add(0, -maxDistance, 0); // End position (straight down)

        HitResult hitResult = level.clip(new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.COLLIDER, // Raycast considers block collision shapes, maybe we don't want this?
                ClipContext.Fluid.NONE,     // Ignore fluids
                player
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {


            double blockY = hitResult.getLocation().y;
            double distanceY = startPos.y - blockY;

            // If magnet is turned off and we are more than 0.1 distance, do nothing
            if ((!magnetOn) && (distanceY > 0.1)) {
                return;
            }

            //mAtH
            double multiplier = 1.0 - (distanceY / maxDistance);

            double scaledForce = multiplier * -VSCHConfig.MAGNET_BOOT_MAX_FORCE.get().doubleValue();

            Vec3 force = new Vec3(0, scaledForce, 0);



            player.setDeltaMovement(player.getDeltaMovement().add(force));



            //System.out.println("Hit block");


            //System.out.println(slotId);
            //level.addParticle(ParticleTypes.HEART, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
        }


    }
}