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
	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private static void execute(LevelAccessor world, Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity == null) {
			cir.setReturnValue(false); // Mixin return false
			return;
		}

		final LivingEntity living_entity;

		if (!(entity instanceof LivingEntity)) {
			cir.setReturnValue(false);
			return; //stop java from complaining
		} else {
			living_entity = (LivingEntity) entity;
		}

		String dimension_id = entity.level().dimension().location().toString();

		boolean in_space = false;

		if (WorldVariables.get(world).dimension_type.contains(dimension_id)) {
			String dimension_type = WorldVariables.get(world).dimension_type.get(dimension_id).getAsString();
			in_space = dimension_type.equals("space");
		}

		if (in_space && !(living_entity.getVehicle() instanceof RocketSeatEntity)) {
			ArrayList<EquipmentSlot> armor_slots = new ArrayList<EquipmentSlot>();
			armor_slots.add(EquipmentSlot.HEAD);
			armor_slots.add(EquipmentSlot.CHEST);
			armor_slots.add(EquipmentSlot.LEGS);
			armor_slots.add(EquipmentSlot.FEET);

			for (EquipmentSlot slot : armor_slots) {
				ItemStack stack = living_entity.getItemBySlot(slot);

				if (!stack.isEmpty()) {

					if (!(stack.getItem() instanceof SteelSuitItem)) {
						if (!(stack.getItem() instanceof TitaniumSuitItem)) {
							if (!(stack.getItem() instanceof NickelSuitItem)) {

								// "Why is this entire mixin just for this one check?" you ask? :revenge~1: :clueless:
								if (!(stack.getItem() instanceof MagnetBootItem)) {
									cir.setReturnValue(false);
									return; // leaving for loop
								}
							}
						}
					}
					//System.out.println(stack.getItem() instanceof SteelSuitItem);
				} else {
					cir.setReturnValue(false);
					return; // leaving for loop
				}
			}

		}

		if (cir.getReturnValue() == null) { // Dont ask
			cir.setReturnValue(true);
		}
	}
}
