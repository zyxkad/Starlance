package net.jcm.vsch;

import org.checkerframework.checker.units.qual.s;

import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.lointain.cosmos.world.inventory.LandingSelectorMenu;
import net.lointain.cosmos.procedures.DistanceOrderProviderProcedure;
import net.lointain.cosmos.procedures.CollisionDetectorProcedure;
import net.lointain.cosmos.network.CosmosModVariables;

import java.util.List;
import java.util.ArrayList;

import io.netty.buffer.Unpooled;

import com.ibm.icu.number.Scale;

public class CollisionDetectorProcedure1 {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Vec3 cubepos = Vec3.ZERO;
		Vec3 rotated = Vec3.ZERO;
		Vec3 vector = Vec3.ZERO;
		Vec3 rotatedZAxis = Vec3.ZERO;
		Vec3 toPlayer = Vec3.ZERO;
		Vec3 rotatedXAxis = Vec3.ZERO;
		Vec3 rotatedYAxis = Vec3.ZERO;
		double roll = 0;
		double pitch = 0;
		double yaw = 0;
		double half_scale = 0;
		double Scale = 0;
		double posX = 0;
		double posY = 0;
		double posZ = 0;
		double range = 0;
		double distanceSqrZ = 0;
		double distanceSqrY = 0;
		double distanceSqrX = 0;
		double angle = 0;
		double Target_Distance = 0;
		List<Object> Target_List = new ArrayList<>();
		CompoundTag Target_object;
		String dimension = "";
		boolean isCollided = false;
		boolean inside_a_valk_ship = false;
		if (CosmosModVariables.WorldVariables.get(world).collision_data_map.contains(entity.level().dimension().location().toString())) {
			if (!(((CosmosModVariables.WorldVariables.get(world).collision_data_map.get(entity.level().dimension().location().toString())) instanceof ListTag _listTag ? _listTag.copy() : new ListTag()).isEmpty())) {
				Target_List = DistanceOrderProviderProcedure.execute(CosmosModVariables.WorldVariables.get(world).global_collision_position_map, 1, entity.level().dimension().location().toString(), entity.position());
				Target_object = (((CosmosModVariables.WorldVariables.get(world).collision_data_map.get(entity.level().dimension().location().toString())) instanceof ListTag _listTag ? _listTag.copy() : new ListTag())
						.get((int) (Target_List.get(0) instanceof Number _doubleValue ? _doubleValue.doubleValue() : 0.0))) instanceof CompoundTag _compoundTag ? _compoundTag.copy() : new CompoundTag();
				yaw = (Target_object.get("yaw")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D;
				pitch = (Target_object.get("pitch")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D;
				roll = (Target_object.get("roll")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D;
				Scale = (Target_object.get("scale")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D;
				cubepos = new Vec3(((Target_object.get("x")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D), ((Target_object.get("y")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D),
						((Target_object.get("z")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D));
				Target_Distance = cubepos.distanceTo((entity.position()));
				if (Target_Distance <= (Scale / 2) * Math.cbrt(3)) {
					yaw = (Target_object.get("yaw")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D;
					pitch = (Target_object.get("pitch")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D;
					roll = (Target_object.get("roll")) instanceof DoubleTag _doubleTag ? _doubleTag.getAsDouble() : 0.0D;
					toPlayer = (entity.position()).subtract(cubepos);
					rotatedXAxis = ((new Vec3(1, 0, 0)).zRot(-Mth.DEG_TO_RAD * (float) (-roll))).yRot(Mth.DEG_TO_RAD * (float) (-yaw));
					rotatedYAxis = ((new Vec3(0, 1, 0)).zRot(-Mth.DEG_TO_RAD * (float) (-roll))).xRot(-Mth.DEG_TO_RAD * (float) pitch);
					rotatedZAxis = ((new Vec3(0, 0, 1)).xRot(-Mth.DEG_TO_RAD * (float) pitch)).yRot(Mth.DEG_TO_RAD * (float) (-yaw));
					distanceSqrX = (rotatedXAxis.scale((toPlayer.dot(rotatedXAxis)))).lengthSqr();
					distanceSqrY = (rotatedYAxis.scale((toPlayer.dot(rotatedYAxis)))).lengthSqr();
					distanceSqrZ = (rotatedZAxis.scale((toPlayer.dot(rotatedZAxis)))).lengthSqr();
					range = (Scale * Scale) / 4;
					isCollided = distanceSqrX <= range && distanceSqrY <= range && distanceSqrZ <= range;
				}
				if (!isCollided) {
					{
						String _setval = "^";
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.landing_coords = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
				}
				if (isCollided && ((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).equals("^")
						&& !(entity instanceof Player _plr54 && _plr54.containerMenu instanceof LandingSelectorMenu)) {
					{
						boolean _setval = false;
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.pitch_i = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					{
						boolean _setval = false;
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.pitch_d = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					{
						boolean _setval = false;
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.roll_i = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					{
						boolean _setval = false;
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.roll_d = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					{
						boolean _setval = false;
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.thrust = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					{
						boolean _setval = false;
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.thrust_drop = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					{
						boolean _setval = false;
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.thrust_catch = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					{
						String _setval = "=";
						entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.landing_coords = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
					if (entity.isPassenger()) {
						if (entity instanceof Player _player) {
							Vec3 _setval = new Vec3(((entity.getVehicle()).getDeltaMovement().x()), ((entity.getVehicle()).getDeltaMovement().y()), ((entity.getVehicle()).getDeltaMovement().z()));
							_player.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
								capability.entry_velocity = _setval;
								capability.syncPlayerVariables(_player);
							});
						}
					}
					CosmosModVariables.WorldVariables
					.get(world).entry_world = (CosmosModVariables.WorldVariables.get(world).antena_locations.get(((Target_object.get("travel_to")) instanceof StringTag _stringTag ? _stringTag.getAsString() : ""))) instanceof ListTag _listTag
					? _listTag.copy()
							: new ListTag();
					CosmosModVariables.WorldVariables.get(world).syncData(world);
					if (entity instanceof ServerPlayer _ent) {
						BlockPos _bpos = BlockPos.containing(x, y, z);
						NetworkHooks.openScreen(_ent, new MenuProvider() {
							@Override
							public Component getDisplayName() {
								return Component.literal("LandingSelector");
							}

							@Override
							public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
								return new LandingSelectorMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
							}
						}, _bpos);
					}
				}
				if (entity instanceof Player _plr68 && _plr68.containerMenu instanceof LandingSelectorMenu) {
					if (!((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).equals("^")
							&& !((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).equals("=")) {
						posX = new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).substring(
								((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).indexOf("*") + "*".length(),
								((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).indexOf("|"))) + 0;
						posY = 550 + Mth.nextInt(RandomSource.create(), -8, 8);
						posZ = new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).substring(
								((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).indexOf("|") + "|".length(),
								((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).indexOf("~"))) + 0;
						dimension = (Target_object.get("travel_to")) instanceof StringTag _stringTag ? _stringTag.getAsString() : "";
						if (inside_a_valk_ship) {//dimension tp
						}
					}
				} else if (!(entity instanceof Player _plr74 && _plr74.containerMenu instanceof LandingSelectorMenu)
						&& ((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).landing_coords).equals("=")) {
					if (isCollided) {
						if (entity.isPassenger()) {
							(entity.getVehicle())
							.setDeltaMovement((((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).entry_velocity).multiply((new Vec3((-4), (-4), (-4))))));
						} else {
							entity.setDeltaMovement(
									new Vec3(((((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).entry_velocity).multiply((new Vec3((-4), (-4), (-4))))).x()),
											((((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).entry_velocity).multiply((new Vec3((-4), (-4), (-4))))).y()),
											((((entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables())).entry_velocity).multiply((new Vec3((-4), (-4), (-4))))).z())));
						}
					} else if (!isCollided) {
						{
							String _setval = "^";
							entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
								capability.landing_coords = _setval;
								capability.syncPlayerVariables(entity);
							});
						}
					}
				}
			}
		}
	}
}
