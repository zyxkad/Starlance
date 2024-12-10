package net.jcm.vsch.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VSCHConfig {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Boolean> THRUSTER_TOGGLE;
	public static final ForgeConfigSpec.ConfigValue<String> THRUSTER_MODE;
	public static final ForgeConfigSpec.ConfigValue<Number> THRUSTER_STRENGTH;
	public static final ForgeConfigSpec.ConfigValue<Number> AIR_THRUSTER_STRENGTH;
	public static final ForgeConfigSpec.ConfigValue<Number> POWERFUL_THRUSTER_STRENGTH;

	public static final ForgeConfigSpec.ConfigValue<Number> MAX_DRAG;

	public static final ForgeConfigSpec.ConfigValue<Boolean> LIMIT_SPEED;
	public static final ForgeConfigSpec.ConfigValue<Number> MAX_SPEED;

	public static final ForgeConfigSpec.ConfigValue<Boolean> CANCEL_ASSEMBLY;

	public static final ForgeConfigSpec.ConfigValue<Number> MAGNET_BOOT_DISTANCE;
	public static final ForgeConfigSpec.ConfigValue<Number> MAGNET_BOOT_MAX_FORCE;

	private static final Collection<String> modes = new ArrayList<String>(2);

	static {
		modes.add("POSITION");
		modes.add("GLOBAL");

		BUILDER.push("Thrusters");

		THRUSTER_TOGGLE = BUILDER.comment("Thruster Mode Toggling").define("thruster_mode_toggle", true);
		THRUSTER_MODE = BUILDER.comment("Default Thruster Mode").defineInList("thruster_default_mode","POSITION",modes);
		THRUSTER_STRENGTH = BUILDER.comment("Thruster force multiplier. redstone * this (Newtons)").define("thruster_strength", 5000);
		AIR_THRUSTER_STRENGTH = BUILDER.comment("Air thruster force multiplier. redstone * this (Newtons)").define("air_thruster_strength", 500);
		POWERFUL_THRUSTER_STRENGTH = BUILDER.comment("Powerful thruster force multiplier. redstone * this (Newtons)").define("powerful_thruster_strength", 20000);

		BUILDER.pop();

		BUILDER.push("Misc");

		MAX_DRAG = BUILDER.comment("Max force the drag inducer can use to slow down").define("max_drag", 15000);
		LIMIT_SPEED = BUILDER.comment("Limit speed thrusters can accelerate to. Recommended, as VS ships get funky at high speeds").define("limit_speed", true);
		MAX_SPEED = BUILDER.comment("Max speed to limit to. Blocks/tick I think. Default is highly recommended").define("max_speed", 150);
		CANCEL_ASSEMBLY = BUILDER.comment("Cancel multi-block assemblies when above world height. This is a temporary fix, but for now ships made above world height have issues with starlance.").define("cancel_assembly", true);

		MAGNET_BOOT_DISTANCE = BUILDER.comment("Distance (in blocks) at which magnet boots will pull you in").define("magnet_boot_distance", 6);
		MAGNET_BOOT_MAX_FORCE = BUILDER.comment("Max acceleration magnet boots will apply at close distances to move the player downwards.").define("magnet_boot_max_force", 0.1);

		BUILDER.pop();

		SPEC = BUILDER.build();
	}
	public static void register(ModLoadingContext context){
		context.registerConfig(ModConfig.Type.SERVER, VSCHConfig.SPEC, "vsch-config.toml");
	}
}
