package net.jcm.vsch.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;
import org.joml.Vector3ic;

import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.util.VSCHUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ShipAssemblyKt.class)
public class MixinCreateNewShipWithBlocks {


	private static final Logger logger = LogManager.getLogger(VSCHMod.MODID);
	@Inject(method = "createNewShipWithBlocks", at = @At("HEAD"), cancellable = true)
	@NotNull
	private static void createNewShipWithBlocks(BlockPos centerBlock, DenseBlockPosSet blocks, ServerLevel level, CallbackInfoReturnable<ServerShip> cir) {
		// ERRORS IN BUILT JAR: BE WARNED (something about casting)
		//ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock((Vector3ic) VectorConversionsMCKt.toJOMLD(centerBlock), false, 1.0, VSGameUtilsKt.getDimensionId(level));

		/*ServerShip assembledShip = cir.getReturnValue();

		if (assembledShip != null) {
			// Apply Y offset to each block in the ship's block list
			int yOffset = 5; // Example offset value
			assembledShip.get
			assembledShip.getBlocks().forEach(block -> {
				BlockPos currentPos = block.getPos();
				BlockPos offsetPos = new BlockPos(currentPos.getX(), currentPos.getY() + yOffset, currentPos.getZ());
				block.setPos(offsetPos);
			});
		}*/

		/*if (blocks.isEmpty()) {
			throw new IllegalArgumentException();
		}

		System.out.println("VSCH Mixin");

		List<BlockPos> blockPosList = new ArrayList<>();

		blocks.forEach((x, y, z) -> {
			blocks.remove(x, y, z);
			BlockPos pos = new BlockPos(x, y, z); // Convert Vector3ic to BlockPos
			blockPosList.add(pos);
			return Unit.INSTANCE;
		});



		System.out.println(blockPosList);

		for (BlockPos blockPos : blockPosList) {
			// Apply the uniform Y-offset while keeping X and Z the same
			BlockPos newPos = new BlockPos(blockPos.getX(), blockPos.getY() - 100, blockPos.getZ());
			BlockState state = level.getBlockState(blockPos);
			level.setBlock(newPos, state, 0);
			blocks.add(newPos.getX(), newPos.getY(), newPos.getZ());
		}

		List<BlockPos> blockPosList2 = new ArrayList<>();

		blocks.forEach((x, y, z) -> {
			BlockPos pos = new BlockPos(x, y, z); // Convert Vector3ic to BlockPos
			blockPosList2.add(pos);
			return Unit.INSTANCE;
		});

		System.out.println(blockPosList2);*/

		//level = level.getServer().getLevel(level.getServer().overworld().dimension());

		//System.out.println(blockPosList);


		//cir.setReturnValue(new LevelYRange(-64, 319));
		//cir.cancel();

	}


}








