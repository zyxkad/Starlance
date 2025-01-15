package net.jcm.vsch.mixin;

import java.util.ArrayList;

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
import net.jcm.vsch.items.VSCHItems;
import net.jcm.vsch.items.custom.MagnetBootItem;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.lointain.cosmos.init.CosmosModItems;
import net.lointain.cosmos.item.NickelSuitItem;
import net.lointain.cosmos.item.SteelSuitItem;
import net.lointain.cosmos.item.TitaniumSuitItem;
import net.lointain.cosmos.network.CosmosModVariables.WorldVariables;
import net.lointain.cosmos.procedures.SpacesuitwornLogicProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

@Mixin(SpacesuitwornLogicProcedure.class)
public class MixinSpaceSuitLogic {

	private static final Logger logger = LogManager.getLogger(VSCHMod.MODID);

	//CallbackInfoReturnable<LevelYRange> cir)
	@Inject(method = "execute", remap = false, at = @At("HEAD"), cancellable = true)
	private static void execute(LevelAccessor world, Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity == null) {
			cir.setReturnValue(false); // Mixin return false
			return;
		}

		if (!(entity instanceof LivingEntity livingEntity)) {
			cir.setReturnValue(false);
			return; //stop java from complaining
		}

		String dimension = entity.level().dimension().location().toString();

		boolean inSpace = false;

		if (WorldVariables.get(world).dimension_type.contains(dimension)) {
			String dimension_type = WorldVariables.get(world).dimension_type.getString(dimension);
			inSpace = dimension_type.equals("space");
		}

		if (inSpace && !(livingEntity.getVehicle() instanceof RocketSeatEntity)) {
			ArrayList<EquipmentSlot> armorSlots = new ArrayList<EquipmentSlot>();
			armorSlots.add(EquipmentSlot.HEAD);
			armorSlots.add(EquipmentSlot.CHEST);
			armorSlots.add(EquipmentSlot.LEGS);
			armorSlots.add(EquipmentSlot.FEET);

			for (EquipmentSlot slot : armorSlots) {
				ItemStack stack = livingEntity.getItemBySlot(slot);

				if (stack.isEmpty()) {
					cir.setReturnValue(false);
					return; // leaving for loop
				}

				boolean isSpaceSuitItem = 
					stack.getItem() instanceof SteelSuitItem ||
					stack.getItem() instanceof TitaniumSuitItem ||
					stack.getItem() instanceof NickelSuitItem ||
					stack.getItem() instanceof MagnetBootItem;

				// "Why is this entire mixin just for this one check?" you ask? :revenge~1: :clueless:
				if (!isSpaceSuitItem) {
					cir.setReturnValue(false);
					return; // leaving for loop
				}
			}

		} else {
			cir.setReturnValue(false);
			return;
		}

		cir.setReturnValue(true);
	}
}
