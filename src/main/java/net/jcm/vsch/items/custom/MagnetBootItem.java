package net.jcm.vsch.items.custom;

import net.jcm.vsch.config.VSCHConfig;
import net.lointain.cosmos.item.SteelarmourItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagnetBootItem extends ArmorItem {
	private static final String TAG_ENABLED = "Enabled";
	private static final String TAG_READY = "Ready";
	private static final String TAG_DIRECTION = "Direction";
	private static final double MIN_FORCE = 0.01;

	public MagnetBootItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
		super(pMaterial, pType, pProperties);
	}

	public double getAttractDistance() {
		return VSCHConfig.MAGNET_BOOT_DISTANCE.get().doubleValue();
	}

	public double getMaxForce() {
		return VSCHConfig.MAGNET_BOOT_MAX_FORCE.get().doubleValue();
	}

	public boolean getEnabled(ItemStack stack) {
		if (!(stack.getItem() instanceof MagnetBootItem)) {
			return false;
		}
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean(TAG_ENABLED);
	}

	public boolean getReady(ItemStack stack) {
		if (!(stack.getItem() instanceof MagnetBootItem)) {
			return false;
		}
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean(TAG_READY);
	}

	public Vec3 getDirection(ItemStack stack) {
		if (!(stack.getItem() instanceof MagnetBootItem)) {
			return null;
		}
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return null;
		}
		return Vec3.CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_DIRECTION));
	}

	@Override
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

		CompoundTag tag = stack.getOrCreateTag();
		boolean enabled = tag.getBoolean(TAG_ENABLED);
		boolean wasReady = tag.getBoolean(TAG_READY);

		double maxDistance = getAttractDistance();

		Vec3 direction = new Vec3(0, -1, 0); // TODO: maybe we can change the direction to match the ship that player stands on?
		Vec3 startPos = player.position(); // Starting position (player's position)
		Vec3 endPos = startPos.add(direction.scale(maxDistance)); // End position (straight down)

		HitResult hitResult = level.clip(new ClipContext(
			startPos,
			endPos,
			ClipContext.Block.COLLIDER, // Raycast considers block collision shapes, maybe we don't want this?
			ClipContext.Fluid.NONE,     // Ignore fluids
			player
		));

		if (hitResult.getType() != HitResult.Type.BLOCK) {
			if (wasReady) {
				tag.putBoolean(TAG_READY, false);
				tag.remove(TAG_DIRECTION);
			}
			return;
		}
		if (!wasReady) {
			tag.putBoolean(TAG_READY, true);
			tag.put(TAG_DIRECTION, Vec3.CODEC.encodeStart(NbtOps.INSTANCE, direction));
		}
		if (!enabled) {
			return;
		}

		//mAtH
		double distance = startPos.distanceToSqr(hitResult.getLocation());
		double scaledForce = Math.min(maxDistance * maxDistance / distance * MIN_FORCE, getMaxForce());

		Vec3 force = direction.scale(scaledForce);

		player.setDeltaMovement(player.getDeltaMovement().add(force));

		//System.out.println("Hit block");

		//System.out.println(slotId);
		//level.addParticle(ParticleTypes.HEART, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
	}
}
