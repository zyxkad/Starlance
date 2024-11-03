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

@Mixin(ShipAssemblyKt.class)
public class MixinCreateNewShipWithBlocks {


	private static final Logger logger = LogManager.getLogger(VSCHMod.MODID);
	@Inject(method = "createNewShipWithBlocks", at = @At("HEAD"), cancellable = true)
	@NotNull


	private static void createNewShipWithBlocks(BlockPos centerBlock, DenseBlockPosSet blocks, ServerLevel level, CallbackInfoReturnable<LevelYRange> cir) {

		if (blocks.isEmpty()) {
			throw new IllegalArgumentException();
		}

		ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(
				VectorConversionsMCKt.toJOML(centerBlock), false, 1, VSCHUtils.dimToVSDim(level.dimension().toString()));

		int shipChunkX = serverShip.getChunkClaim().getXMiddle();
		int shipChunkZ = serverShip.getChunkClaim().getZMiddle();

		int worldChunkX = centerBlock.getX() >> 4;
		int worldChunkZ = centerBlock.getZ() >> 4;

		int deltaX = worldChunkX - shipChunkX;
		int deltaZ = worldChunkZ - shipChunkZ;

		//TODO: De-Kotlin ify the rest of this assemble functions variables from ShipAssemblyKt
		//And fix up whatever its angry about.
		//THEN, find where it does Y value. THEN override it with getYRange or whatever

		Intrinsics.checkNotNullParameter(centerBlock, "centerBlock");
		Intrinsics.checkNotNullParameter(blocks, "blocks");
		Intrinsics.checkNotNullParameter(level, "level");
		if (blocks.isEmpty()) {
			throw new IllegalArgumentException();
		} else {
			ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(
					(Vector3ic) VectorConversionsMCKt.toJOML((Vec3i) centerBlock), false, 1.0,
					VSGameUtilsKt.getDimensionId(level));
			int shipChunkX = ship.getChunkClaim().getXMiddle();
			int shipChunkZ = ship.getChunkClaim().getZMiddle();
			int worldChunkX = centerBlock.getX() >> 4;
			int worldChunkZ = centerBlock.getZ() >> 4;
			int deltaX = worldChunkX - shipChunkX;
			int deltaZ = worldChunkZ - shipChunkZ;
			Map chunksToBeUpdated = (Map) (new LinkedHashMap());
			int $i$f$forEachChunk = false;
			BlockPos2ObjectOpenHashMap this_$iv$iv = blocks.getChunks();
			int $i$f$flatMap = false;
			int var10000;
			int z$iv;
			int var10001;
			int var10002;
			int x$iv;
			SingleChunkDenseBlockPosSet var10003;
			boolean var19;
			boolean var24;
			ChunkPos sourcePos;
			ChunkPos destPos;
			if (this_$iv$iv.getContainsNullKey()) {
				var10000 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3];
				var10001 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3 + 1];
				var10002 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3 + 2];
				var10003 = (SingleChunkDenseBlockPosSet) this_$iv$iv.getValues()[this_$iv$iv.getN()];
				z$iv = var10002;
				x$iv = var10000;
				var19 = false;
				var24 = false;
				sourcePos = new ChunkPos(x$iv, z$iv);
				destPos = new ChunkPos(x$iv - deltaX, z$iv - deltaZ);
				chunksToBeUpdated.put(sourcePos, new Pair(sourcePos, destPos));
			}

			for (int pos$iv$iv = this_$iv$iv.getN(); -1 < pos$iv$iv; --pos$iv$iv) {
				if (this_$iv$iv.getKeys()[pos$iv$iv * 3] != 0 || this_$iv$iv.getKeys()[pos$iv$iv * 3 + 1] != 0
						|| this_$iv$iv.getKeys()[pos$iv$iv * 3 + 2] != 0) {
					var10000 = this_$iv$iv.getKeys()[pos$iv$iv * 3];
					var10001 = this_$iv$iv.getKeys()[pos$iv$iv * 3 + 1];
					var10002 = this_$iv$iv.getKeys()[pos$iv$iv * 3 + 2];
					var10003 = (SingleChunkDenseBlockPosSet) this_$iv$iv.getValues()[pos$iv$iv];
					z$iv = var10002;
					x$iv = var10000;
					var19 = false;
					var24 = false;
					sourcePos = new ChunkPos(x$iv, z$iv);
					destPos = new ChunkPos(x$iv - deltaX, z$iv - deltaZ);
					chunksToBeUpdated.put(sourcePos, new Pair(sourcePos, destPos));
				}
			}

			List chunkPairs = CollectionsKt.toList((Iterable) chunksToBeUpdated.values());
			Iterable $this$flatMap$iv = chunkPairs;
			$i$f$flatMap = false;
			Collection destination$iv$iv = (Collection) (new ArrayList());
			int $i$f$forEach = false;
			Iterator var77 = $this$flatMap$iv.iterator();

			while (var77.hasNext()) {
				Object element$iv$iv = var77.next();
				Pair it = (Pair) element$iv$iv;
				int var21 = false;
				Iterable list$iv$iv = (Iterable) TuplesKt.toList(it);
				CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
			}

			final List chunkPoses = (List) destination$iv$iv;
			Iterable $this$forEach$iv = chunkPoses;
			int $i$f$forEachChunk = false;
			Collection destination$iv$iv = (Collection) (new ArrayList(
					CollectionsKt.collectionSizeOrDefault($this$forEach$iv, 10)));
			int $i$f$mapTo = false;
			Iterator var82 = $this$forEach$iv.iterator();

			boolean var22;
			while (var82.hasNext()) {
				Object item$iv$iv = var82.next();
				ChunkPos it = (ChunkPos) item$iv$iv;
				var22 = false;
				destination$iv$iv.add(VectorConversionsMCKt.toJOML(it));
			}

			List chunkPosesJOML = (List) destination$iv$iv;
			List var91 = level.players();
			Intrinsics.checkNotNullExpressionValue(var91, "players(...)");
			$this$forEach$iv = var91;
			$i$f$forEachChunk = false;
			Iterator var73 = $this$forEach$iv.iterator();

			while (var73.hasNext()) {
				Object element$iv = var73.next();
				ServerPlayer player = (ServerPlayer) element$iv;
				var19 = false;
				SimplePacket var92 = (SimplePacket) (new PacketStopChunkUpdates(chunkPosesJOML));
				Intrinsics.checkNotNull(player);
				MinecraftPlayer var93 = VSGameUtilsKt.getPlayerWrapper((Player) player);
				Intrinsics.checkNotNullExpressionValue(var93, "<get-playerWrapper>(...)");
				SimplePackets.sendToClient(var92, (IPlayer) var93);
			}

			$i$f$forEachChunk = false;
			BlockPos2ObjectOpenHashMap this_$iv$iv = blocks.getChunks();
			$i$f$forEach = false;
			LevelChunk sourceChunk;
			LevelChunk destChunk;
			boolean $i$f$forEach;
			byte[] $this$forEachIndexed$iv$iv;
			boolean $i$f$forEachIndexed;
			int index$iv$iv;
			int var35;
			int var36;
			byte item$iv$iv;
			int index$iv;
			boolean var40;
			byte $this$iterateBits$iv$iv;
			boolean $i$f$iterateBits;
			int i$iv$iv;
			int masked$iv$iv;
			boolean isSet$iv;
			boolean var47;
			int index$iv$iv;
			Vector3ic dimensions$iv$iv;
			boolean $i$f$unwrapIndex;
			int z$iv$iv;
			int y$iv$iv;
			int x$iv$iv;
			boolean var57;
			boolean var61;
			BlockPos fromPos;
			BlockPos toPos;
			SingleChunkDenseBlockPosSet chunk$iv;
			int z$iv;
			int y$iv;
			int x$iv;
			int chunkY;
			boolean var90;
			if (this_$iv$iv.getContainsNullKey()) {
				var10000 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3];
				var10001 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3 + 1];
				var10002 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3 + 2];
				chunk$iv = (SingleChunkDenseBlockPosSet) this_$iv$iv.getValues()[this_$iv$iv.getN()];
				z$iv = var10002;
				y$iv = var10001;
				x$iv = var10000;
				var22 = false;
				chunkY = y$iv;
				var90 = false;
				sourceChunk = level.getChunk(x$iv, z$iv);
				destChunk = level.getChunk(x$iv - deltaX, z$iv - deltaZ);
				$i$f$forEach = false;
				$this$forEachIndexed$iv$iv = chunk$iv.getData();
				$i$f$forEachIndexed = false;
				index$iv$iv = 0;
				var35 = 0;

				for (var36 = $this$forEachIndexed$iv$iv.length; var35 < var36; ++var35) {
					item$iv$iv = $this$forEachIndexed$iv$iv[var35];
					index$iv = index$iv$iv++;
					var40 = false;
					$this$iterateBits$iv$iv = item$iv$iv;
					$i$f$iterateBits = false;

					for (i$iv$iv = 7; -1 < i$iv$iv; --i$iv$iv) {
						masked$iv$iv = $this$iterateBits$iv$iv & 1 << i$iv$iv;
						isSet$iv = masked$iv$iv != 0;
						var47 = false;
						if (isSet$iv) {
							index$iv$iv = index$iv * 8 + i$iv$iv;
							dimensions$iv$iv = SingleChunkDenseBlockPosSet.Companion.getDimensions();
							$i$f$unwrapIndex = false;
							z$iv$iv = index$iv$iv / (dimensions$iv$iv.x() * dimensions$iv$iv.y());
							y$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
									/ dimensions$iv$iv.x();
							x$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
									% dimensions$iv$iv.x();
							var57 = false;
							var61 = false;
							fromPos = new BlockPos((sourceChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
									(sourceChunk.getPos().z << 4) + z$iv$iv);
							toPos = new BlockPos((destChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
									(destChunk.getPos().z << 4) + z$iv$iv);
							Intrinsics.checkNotNull(sourceChunk);
							Intrinsics.checkNotNull(destChunk);
							RelocationUtilKt.relocateBlock$default(sourceChunk, fromPos, destChunk, toPos, false, ship,
									(Rotation) null, 64, (Object) null);
						}
					}
				}
			}

			int pos$iv$iv;
			for (pos$iv$iv = this_$iv$iv.getN(); -1 < pos$iv$iv; --pos$iv$iv) {
				if (this_$iv$iv.getKeys()[pos$iv$iv * 3] != 0 || this_$iv$iv.getKeys()[pos$iv$iv * 3 + 1] != 0
						|| this_$iv$iv.getKeys()[pos$iv$iv * 3 + 2] != 0) {
					var10000 = this_$iv$iv.getKeys()[pos$iv$iv * 3];
					var10001 = this_$iv$iv.getKeys()[pos$iv$iv * 3 + 1];
					var10002 = this_$iv$iv.getKeys()[pos$iv$iv * 3 + 2];
					chunk$iv = (SingleChunkDenseBlockPosSet) this_$iv$iv.getValues()[pos$iv$iv];
					z$iv = var10002;
					y$iv = var10001;
					x$iv = var10000;
					var22 = false;
					chunkY = y$iv;
					var90 = false;
					sourceChunk = level.getChunk(x$iv, z$iv);
					destChunk = level.getChunk(x$iv - deltaX, z$iv - deltaZ);
					$i$f$forEach = false;
					$this$forEachIndexed$iv$iv = chunk$iv.getData();
					$i$f$forEachIndexed = false;
					index$iv$iv = 0;
					var35 = 0;

					for (var36 = $this$forEachIndexed$iv$iv.length; var35 < var36; ++var35) {
						item$iv$iv = $this$forEachIndexed$iv$iv[var35];
						index$iv = index$iv$iv++;
						var40 = false;
						$this$iterateBits$iv$iv = item$iv$iv;
						$i$f$iterateBits = false;

						for (i$iv$iv = 7; -1 < i$iv$iv; --i$iv$iv) {
							masked$iv$iv = $this$iterateBits$iv$iv & 1 << i$iv$iv;
							isSet$iv = masked$iv$iv != 0;
							var47 = false;
							if (isSet$iv) {
								index$iv$iv = index$iv * 8 + i$iv$iv;
								dimensions$iv$iv = SingleChunkDenseBlockPosSet.Companion.getDimensions();
								$i$f$unwrapIndex = false;
								z$iv$iv = index$iv$iv / (dimensions$iv$iv.x() * dimensions$iv$iv.y());
								y$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
										/ dimensions$iv$iv.x();
								x$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
										% dimensions$iv$iv.x();
								var57 = false;
								var61 = false;
								fromPos = new BlockPos((sourceChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
										(sourceChunk.getPos().z << 4) + z$iv$iv);
								toPos = new BlockPos((destChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
										(destChunk.getPos().z << 4) + z$iv$iv);
								Intrinsics.checkNotNull(sourceChunk);
								Intrinsics.checkNotNull(destChunk);
								RelocationUtilKt.relocateBlock$default(sourceChunk, fromPos, destChunk, toPos, false,
										ship, (Rotation) null, 64, (Object) null);
							}
						}
					}
				}
			}

			$i$f$forEachChunk = false;
			this_$iv$iv = blocks.getChunks();
			$i$f$forEach = false;
			Level var95;
			BlockState var97;
			if (this_$iv$iv.getContainsNullKey()) {
				var10000 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3];
				var10001 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3 + 1];
				var10002 = this_$iv$iv.getKeys()[this_$iv$iv.getN() * 3 + 2];
				chunk$iv = (SingleChunkDenseBlockPosSet) this_$iv$iv.getValues()[this_$iv$iv.getN()];
				z$iv = var10002;
				y$iv = var10001;
				x$iv = var10000;
				var22 = false;
				chunkY = y$iv;
				var90 = false;
				sourceChunk = level.getChunk(x$iv, z$iv);
				destChunk = level.getChunk(x$iv - deltaX, z$iv - deltaZ);
				$i$f$forEach = false;
				$this$forEachIndexed$iv$iv = chunk$iv.getData();
				$i$f$forEachIndexed = false;
				index$iv$iv = 0;
				var35 = 0;

				for (var36 = $this$forEachIndexed$iv$iv.length; var35 < var36; ++var35) {
					item$iv$iv = $this$forEachIndexed$iv$iv[var35];
					index$iv = index$iv$iv++;
					var40 = false;
					$this$iterateBits$iv$iv = item$iv$iv;
					$i$f$iterateBits = false;

					for (i$iv$iv = 7; -1 < i$iv$iv; --i$iv$iv) {
						masked$iv$iv = $this$iterateBits$iv$iv & 1 << i$iv$iv;
						isSet$iv = masked$iv$iv != 0;
						var47 = false;
						if (isSet$iv) {
							index$iv$iv = index$iv * 8 + i$iv$iv;
							dimensions$iv$iv = SingleChunkDenseBlockPosSet.Companion.getDimensions();
							$i$f$unwrapIndex = false;
							z$iv$iv = index$iv$iv / (dimensions$iv$iv.x() * dimensions$iv$iv.y());
							y$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
									/ dimensions$iv$iv.x();
							x$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
									% dimensions$iv$iv.x();
							var57 = false;
							var61 = false;
							fromPos = new BlockPos((sourceChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
									(sourceChunk.getPos().z << 4) + z$iv$iv);
							toPos = new BlockPos((destChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
									(destChunk.getPos().z << 4) + z$iv$iv);
							var95 = destChunk.getLevel();
							Intrinsics.checkNotNullExpressionValue(var95, "getLevel(...)");
							var97 = destChunk.getBlockState(toPos);
							Intrinsics.checkNotNullExpressionValue(var97, "getBlockState(...)");
							RelocationUtilKt.updateBlock(var95, fromPos, toPos, var97);
						}
					}
				}
			}

			for (pos$iv$iv = this_$iv$iv.getN(); -1 < pos$iv$iv; --pos$iv$iv) {
				if (this_$iv$iv.getKeys()[pos$iv$iv * 3] != 0 || this_$iv$iv.getKeys()[pos$iv$iv * 3 + 1] != 0
						|| this_$iv$iv.getKeys()[pos$iv$iv * 3 + 2] != 0) {
					var10000 = this_$iv$iv.getKeys()[pos$iv$iv * 3];
					var10001 = this_$iv$iv.getKeys()[pos$iv$iv * 3 + 1];
					var10002 = this_$iv$iv.getKeys()[pos$iv$iv * 3 + 2];
					chunk$iv = (SingleChunkDenseBlockPosSet) this_$iv$iv.getValues()[pos$iv$iv];
					z$iv = var10002;
					y$iv = var10001;
					x$iv = var10000;
					var22 = false;
					chunkY = y$iv;
					var90 = false;
					sourceChunk = level.getChunk(x$iv, z$iv);
					destChunk = level.getChunk(x$iv - deltaX, z$iv - deltaZ);
					$i$f$forEach = false;
					$this$forEachIndexed$iv$iv = chunk$iv.getData();
					$i$f$forEachIndexed = false;
					index$iv$iv = 0;
					var35 = 0;

					for (var36 = $this$forEachIndexed$iv$iv.length; var35 < var36; ++var35) {
						item$iv$iv = $this$forEachIndexed$iv$iv[var35];
						index$iv = index$iv$iv++;
						var40 = false;
						$this$iterateBits$iv$iv = item$iv$iv;
						$i$f$iterateBits = false;

						for (i$iv$iv = 7; -1 < i$iv$iv; --i$iv$iv) {
							masked$iv$iv = $this$iterateBits$iv$iv & 1 << i$iv$iv;
							isSet$iv = masked$iv$iv != 0;
							var47 = false;
							if (isSet$iv) {
								index$iv$iv = index$iv * 8 + i$iv$iv;
								dimensions$iv$iv = SingleChunkDenseBlockPosSet.Companion.getDimensions();
								$i$f$unwrapIndex = false;
								z$iv$iv = index$iv$iv / (dimensions$iv$iv.x() * dimensions$iv$iv.y());
								y$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
										/ dimensions$iv$iv.x();
								x$iv$iv = (index$iv$iv - z$iv$iv * dimensions$iv$iv.x() * dimensions$iv$iv.y())
										% dimensions$iv$iv.x();
								var57 = false;
								var61 = false;
								fromPos = new BlockPos((sourceChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
										(sourceChunk.getPos().z << 4) + z$iv$iv);
								toPos = new BlockPos((destChunk.getPos().x << 4) + x$iv$iv, (chunkY << 4) + y$iv$iv,
										(destChunk.getPos().z << 4) + z$iv$iv);
								var95 = destChunk.getLevel();
								Intrinsics.checkNotNullExpressionValue(var95, "getLevel(...)");
								var97 = destChunk.getBlockState(toPos);
								Intrinsics.checkNotNullExpressionValue(var97, "getBlockState(...)");
								RelocationUtilKt.updateBlock(var95, fromPos, toPos, var97);
							}
						}
					}
				}
			}

			Vector3d centerInShip = new Vector3d((double) ((shipChunkX << 4) + (centerBlock.getX() & 15)),
					(double) centerBlock.getY(), (double) ((shipChunkZ << 4) + (centerBlock.getZ() & 15)));
			Vector3d centerBlockPosInWorld = ship.getInertiaData().getCenterOfMassInShip()
					.sub((Vector3dc) centerInShip, new Vector3d()).add(ship.getTransform().getPositionInWorld());
			Intrinsics.checkNotNull(ship,
					"null cannot be cast to non-null type org.valkyrienskies.core.impl.game.ships.ShipData");
			ShipData var98 = (ShipData) ship;
			ShipTransform var94 = ((ShipData) ship).getTransform();
			Intrinsics.checkNotNull(var94,
					"null cannot be cast to non-null type org.valkyrienskies.core.impl.game.ships.ShipTransformImpl");
			ShipTransformImpl var96 = (ShipTransformImpl) var94;
			Intrinsics.checkNotNull(centerBlockPosInWorld);
			var98.setTransform((ShipTransform) ShipTransformImpl.copy$default(var96, (Vector3dc) centerBlockPosInWorld,
					(Vector3dc) null, (Quaterniondc) null, (Vector3dc) null, 14, (Object) null));
			MinecraftServer var99 = level.getServer();
			Intrinsics.checkNotNullExpressionValue(var99, "getServer(...)");
			VSGameUtilsKt.executeIf(var99, (Function0) (new Function0<Boolean>() {
				@NotNull
				public final Boolean invoke() {
					Iterable $this$all$iv = chunkPoses;
					ServerLevel var2 = level;
					int $i$f$all = false;
					boolean var10000;
					if ($this$all$iv instanceof Collection && ((Collection) $this$all$iv).isEmpty()) {
						var10000 = true;
					} else {
						Iterator var4 = $this$all$iv.iterator();

						while (true) {
							if (!var4.hasNext()) {
								var10000 = true;
								break;
							}

							Object element$iv = var4.next();
							ChunkPos p0 = (ChunkPos) element$iv;
							int var7 = false;
							if (!VSGameUtilsKt.isTickingChunk(var2, p0)) {
								var10000 = false;
								break;
							}
						}
					}

					return var10000;
				}
			}), ShipAssemblyKt::createNewShipWithBlocks$lambda$9);
			return ship;

			/*List<BlockPos> blockPosList = new ArrayList<>();

		blocks.forEach((x, y, z) -> {
			BlockPos pos = new BlockPos(x, y, z); // Convert Vector3ic to BlockPos
			blockPosList.add(pos);
			return Unit.INSTANCE;
		});*/

			//level = level.getServer().getLevel(level.getServer().overworld().dimension());

			//System.out.println(blockPosList);


			//cir.setReturnValue(new LevelYRange(-64, 319));
			//cir.cancel();

		}


	}








