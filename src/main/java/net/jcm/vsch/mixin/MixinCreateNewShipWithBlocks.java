package net.jcm.vsch.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3d;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates;
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import kotlin.Unit;
import net.jcm.vsch.VSCHMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ShipAssemblyKt.class)
public class MixinCreateNewShipWithBlocks {


	private static final Logger logger = LogManager.getLogger(VSCHMod.MODID);
	@Inject(method = "createNewShipWithBlocks", at = @At("HEAD"), cancellable = true)
    private static void createNewShipWithBlocks(BlockPos centerBlock, DenseBlockPosSet blocks, ServerLevel level, CallbackInfoReturnable<ServerShip> cir) {
//
//
//
//		if (blocks.isEmpty()) throw new IllegalArgumentException();
//
//		Ship ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(centerBlock), false, 1.0, VSGameUtilsKt.getDimensionId(level));
//
//		int shipChunkX = ship.getChunkClaim().getXMiddle();
//		int shipChunkZ = ship.getChunkClaim().getXMiddle();
//
//		int worldChunkX = centerBlock.getX() >> 4;
//		int worldChunkZ = centerBlock.getY() >> 4;
//
//		int deltaX = worldChunkX - shipChunkX;
//		int deltaZ = worldChunkZ - shipChunkZ;
//
//		Map<ChunkPos, Pair<ChunkPos, ChunkPos>> chunksToBeUpdated = new HashMap<>();
//		blocks.forEachChunk((x, blob, z, secondblob) -> {
//			ChunkPos sourcePos = new ChunkPos(x, z);
//			ChunkPos destPos = new ChunkPos(x - deltaX, z - deltaZ);
//			chunksToBeUpdated.put(sourcePos, new MutablePair<>(sourcePos, destPos));
//            return null;
//        });
//		List<Pair<ChunkPos, ChunkPos>> chunkPairs = List.copyOf(chunksToBeUpdated.values());
//		List<ChunkPos> chunkPoses = chunkPairs.stream().flatMap(pair -> List.of(pair.getLeft(), pair.getRight()).stream()).collect(Collectors.toList());
//		List<> chunkPosesJOML = chunkPoses.stream().map(ChunkPos::toJOML).collect(Collectors.toList());
//
//// Send a list of all the chunks that we plan on updating to players, so that they
//// defer all updates until assembly is finished
//		level.players().forEach(player -> {
//			new PacketStopChunkUpdates(chunkPosesJOML).sendToClient(player.playerWrapper);
//		});
//
//// Use relocateBlock to copy all the blocks into the ship
//		blocks.forEachChunk((chunkX, chunkY, chunkZ, chunk) -> {
//			LevelChunk sourceChunk = level.getChunk(chunkX, chunkZ);
//			LevelChunk destChunk = level.getChunk(chunkX - deltaX, chunkZ - deltaZ);
//
//			chunk.forEach((x, y, z) -> {
//				BlockPos fromPos = new BlockPos((sourceChunk.pos.x << 4) + x, (chunkY << 4) + y, (sourceChunk.pos.z << 4) + z);
//				BlockPos toPos = new BlockPos((destChunk.pos.x << 4) + x, (chunkY << 4) + y, (destChunk.pos.z << 4) + z);
//
//				relocateBlock(sourceChunk, fromPos, destChunk, toPos, false, ship);
//			});
//		});
//
//// Use updateBlock to update blocks after copying
//		blocks.forEachChunk((chunkX, chunkY, chunkZ, chunk) -> {
//			Chunk sourceChunk = level.getChunk(chunkX, chunkZ);
//			Chunk destChunk = level.getChunk(chunkX - deltaX, chunkZ - deltaZ);
//
//			chunk.forEach((x, y, z) -> {
//				BlockPos fromPos = new BlockPos((sourceChunk.pos.x << 4) + x, (chunkY << 4) + y, (sourceChunk.pos.z << 4) + z);
//				BlockPos toPos = new BlockPos((destChunk.pos.x << 4) + x, (chunkY << 4) + y, (destChunk.pos.z << 4) + z);
//
//				updateBlock(destChunk.level, fromPos, toPos, destChunk.getBlockState(toPos));
//			});
//		});
//
//// Calculate the position of the block that the player clicked after it has been assembled
//		Vector3d centerInShip = new Vector3d(
//				((shipChunkX << 4) + (centerBlock.x & 15)),
//				centerBlock.y,
//				((shipChunkZ << 4) + (centerBlock.z & 15))
//		);
//
//// The ship's position has shifted from the center block since we assembled the ship, compensate for that
//		Vector3d centerBlockPosInWorld = ship.inertiaData.centerOfMassInShip.sub(centerInShip, new Vector3d())
//				.add(ship.transform.positionInWorld);
//
//// Put the ship into the compensated position, so that all the assembled blocks stay in the same place
//// TODO: AAAAAAAAA THIS IS HORRIBLE how can the API support this?
//		((ShipData) ship).transform = ((ShipTransformImpl) ship.transform).copy(positionInWorld = centerBlockPosInWorld);
//
//		level.server.executeIf(
//				// This condition will return true if all modified chunks have been both loaded AND
//				// chunk update packets were sent to players
//				() -> chunkPoses.stream().allMatch(level::isTickingChunk)
//		) {
//			// Once all the chunk updates are sent to players, we can tell them to restart chunk updates
//			level.players().forEach(player -> {
//				new PacketRestartChunkUpdates(chunkPosesJOML).sendToClient(player.playerWrapper);
//			});
//		};
//
//		return ship;


	}
}








