package net.jcm.vsch.items.custom;

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
	public void onArmorTick(ItemStack stack, Level level, Player player) {

		boolean magnetOn = true;

		double maxDistance = 4; //TODO: Make this a config 

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

			double scaledForce = multiplier * -0.1; //-0.1 being max force

			Vec3 force = new Vec3(0, scaledForce, 0);



			player.setDeltaMovement(player.getDeltaMovement().add(force));



			//System.out.println("Hit block");


			//System.out.println(slotId);
			//level.addParticle(ParticleTypes.HEART, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
		}


	}
}
