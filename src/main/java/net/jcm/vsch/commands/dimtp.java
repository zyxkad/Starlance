package net.jcm.vsch.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.joml.Vector3d;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.core.apigame.world.properties.DimensionIdKt;
public class dimtp {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(LiteralArgumentBuilder.literal("bruh").then(
                RequiredArgumentBuilder.argument("position", Vec3Argument.vec3()).executes((command) -> {
                    val position = Vec3Argument.getVec3(it instanceof CommandContext<CommandSourceStack>, "position");
                    ShipTeleportData data = new ShipTeleportDataImpl(newPos = position.toJOML());

        }));
    }
}
