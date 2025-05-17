package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.custom.RocketSupporterBlock;
import net.jcm.vsch.blocks.entity.template.ParticleBlockEntity;
import net.jcm.vsch.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaterniond;
import org.joml.RoundingMode;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

public class RocketSupporterBlockEntity extends BlockEntity implements ParticleBlockEntity {
	private static final int MAX_SIZE = 256 * 16;
	private static final int MAX_BLOCKS = 16 * 16 * 16 * 16 * 9;

	private boolean triggering = false;
	private boolean assembling = false;
	private Long teleporting = null;
	private final Vector3d relativePos = new Vector3d();
	private String assembleResult = null;
	private final Queue<BlockPos> queueing = new ArrayDeque<>();
	private final DenseBlockPosSet blocks = new DenseBlockPosSet();
	private final DenseBlockPosSet checked = new DenseBlockPosSet();
	private final AABBi box = new AABBi();

	public RocketSupporterBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.ROCKET_SUPPORTER_BLOCK_ENTITY.get(), pos, state);
	}

	public boolean isAssembling() {
		return this.assembling || this.teleporting != null;
	}

	public boolean isAssembleSuccessed() {
		return !this.isAssembling() && this.assembleResult == null;
	}

	public String getAssembleResult() {
		return this.assembleResult;
	}

	public void neighborChanged(Block neighbor, BlockPos neighborPos, boolean moving) {
		final Level level = this.getLevel();
		final BlockPos pos = this.getBlockPos();
		final boolean shouldTrigger = Direction.stream()
			.filter(dir -> dir != this.getBlockState().getValue(DirectionalBlock.FACING))
			.anyMatch(dir -> level.getSignal(pos.relative(dir), dir) > 0);
		if (this.triggering == shouldTrigger) {
			return;
		}
		this.triggering = shouldTrigger;
		if (shouldTrigger) {
			this.assemble();
		}
	}

	@Override
	public void tickForce(ServerLevel level, BlockPos pos, BlockState state) {
		if (this.assembling) {
			this.assembleTick(level);
		} else if (this.teleporting != null) {
			this.tryTeleport(level);
		}
	}

	@Override
	public void tickParticles(Level level, BlockPos pos, BlockState state) {
		//
	}

	private void assemble() {
		if (this.assembling) {
			return;
		}
		this.assembling = true;
		this.teleporting = null;
		this.assembleResult = null;
		this.queueing.clear();
		this.blocks.clear();
		this.checked.clear();
		final BlockPos facingBlockPos = this.getBlockPos().relative(this.getBlockState().getValue(DirectionalBlock.FACING));
		this.queueing.add(facingBlockPos);
		this.box
			.setMin(facingBlockPos.getX(), facingBlockPos.getY(), facingBlockPos.getZ())
			.setMax(facingBlockPos.getX(), facingBlockPos.getY(), facingBlockPos.getZ());
	}

	private void finishAssemble(String error) {
		// TODO: change texture based on if assemble successed
		// TODO: show assemble error message somewhere
		this.assembling = false;
		this.teleporting = null;
		this.assembleResult = error;
		this.queueing.clear();
		this.blocks.clear();
		this.checked.clear();
	}

	private void assembleTick(final ServerLevel level) {
		final BlockPos selfPos = this.getBlockPos();
		int ticked = 0;
		while (true) {
			final BlockPos pos = this.queueing.poll();
			if (pos == null) {
				if (ticked > 0) {
					return;
				}
				break;
			}
			if (this.blocks.size() >= MAX_BLOCKS) {
				this.finishAssemble("too many blocks");
				return;
			}
			if (pos.equals(selfPos)) {
				this.finishAssemble("cannot assemble itself");
				return;
			}
			if (!this.checkBlock(pos)) {
				return;
			}
			ticked++;
			if (ticked > 16 * 16 * 16) {
				return;
			}
		}
		if (this.blocks.isEmpty()) {
			this.finishAssemble("no block to assemble");
			return;
		}
		if (this.box.lengthX() > MAX_SIZE || this.box.lengthY() > MAX_SIZE || this.box.lengthZ() > MAX_SIZE) {
			this.finishAssemble("excess size limit");
			return;
		}
		this.createShip(level);
	}

	private boolean checkBlock(final BlockPos pos) {
		final Level level = this.getLevel();
		if (!level.hasChunkAt(pos.getX(), pos.getZ())) {
			this.finishAssemble("assemble unloaded chunk");
			return false;
		}
		final BlockState block = level.getBlockState(pos);
		if (this.isAirBlock(block)) {
			return true;
		}
		if (!this.canAssembleBlock(block)) {
			this.finishAssemble("cannot assemble block");
			return false;
		}
		this.box.union(pos.getX(), pos.getY(), pos.getZ());
		this.blocks.add(pos.getX(), pos.getY(), pos.getZ());
		for (final Direction dir : Direction.values()) {
			final BlockPos p = pos.relative(dir);
			final BlockState targetState = level.getBlockState(p);
			if (targetState.getBlock() instanceof RocketSupporterBlock && targetState.getValue(DirectionalBlock.FACING) == dir.getOpposite()) {
				final RocketSupporterBlockEntity otherSupporter = (RocketSupporterBlockEntity) (level.getBlockEntity(p));
				if (otherSupporter != this && otherSupporter.isAssembling()) {
					this.finishAssemble(null);
					return false;
				}
				continue;
			}
			if (this.checked.add(p.getX(), p.getY(), p.getZ())) {
				this.queueing.add(p);
			}
		}
		return true;
	}

	private boolean isAirBlock(final BlockState state) {
		return state.isAir();
	}

	private boolean canAssembleBlock(final BlockState state) {
		final Block block = state.getBlock();
		if (block == Blocks.BEDROCK) {
			return false;
		}
		// TODO: load blacklist from config
		return true;
	}

	private void createShip(final ServerLevel level) {
		final ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
		final String levelId = VSGameUtilsKt.getDimensionId(level);
		final Vector3i worldCenter = new Vector3i(this.box.center(new Vector3d()), RoundingMode.TRUNCATE);
		final ServerShip ship = shipWorld.createNewShipAtBlock(worldCenter, false, 1.0, levelId);
		final Vector3i shipCenter = ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());
		final Vector3i offset = shipCenter.sub(worldCenter, new Vector3i());
		final List<Pair<BlockPos, BlockState>> blockStates = new ArrayList<>(this.blocks.size());
		this.blocks.forEach((x, y, z) -> {
			final BlockPos pos = new BlockPos(x, y, z);
			blockStates.add(new Pair<>(pos, level.getBlockState(pos)));
			final CompoundTag nbt = level.getChunkAt(pos).getBlockEntityNbtForSaving(pos);
			if (nbt != null) {
				final BlockPos targetPos = pos.offset(offset.x, offset.y, offset.z);
				nbt.putInt("x", targetPos.getX());
				nbt.putInt("y", targetPos.getY());
				nbt.putInt("z", targetPos.getZ());
				level.getChunkAt(targetPos).setBlockEntityNbt(nbt);
			}
			Clearable.tryClear(level.getBlockEntity(pos));
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_MOVE_BY_PISTON);
			return null;
		});
		blockStates.forEach((value) -> {
			final BlockPos pos = value.left().offset(offset.x, offset.y, offset.z);
			level.setBlock(pos, value.right(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_MOVE_BY_PISTON);
		});
		blockStates.forEach((value) -> {
			final BlockPos pos = value.left();
			final Block block = value.right().getBlock();
			level.blockUpdated(pos, block);
			level.blockUpdated(pos.offset(offset.x, offset.y, offset.z), block);
		});

		final AABBic box = ship.getShipAABB();
		final Vector3d position = ship.getTransform().getPositionInWorld().add(ship.getInertiaData().getCenterOfMassInShip(), new Vector3d()).sub(shipCenter.x, shipCenter.y, shipCenter.z);
		final Quaterniond rotation = new Quaterniond();
		final Vector3d velocity = new Vector3d();
		final Vector3d omega = new Vector3d();
		double scale = 1.0;

		this.relativePos.set(position);

		final Ship selfShip = VSGameUtilsKt.getShipManagingPos(level, this.getBlockPos());
		if (selfShip != null) {
			final ShipTransform selfTransform = selfShip.getTransform();
			selfTransform.getShipToWorld().transformPosition(position);
			rotation.set(selfTransform.getShipToWorldRotation());
			velocity.set(selfShip.getVelocity());
			omega.set(selfShip.getOmega());
			scale = Math.sqrt(selfTransform.getShipToWorldScaling().lengthSquared() / 3);
		}
		final ShipTeleportData teleportData = new ShipTeleportDataImpl(position, rotation, velocity, omega, levelId, scale);
		shipWorld.teleportShip(ship, teleportData);

		this.assembling = false;
		this.teleporting = ship.getId();
	}

	public void tryTeleport(final ServerLevel level) {
		final ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
		final LoadedServerShip ship = shipWorld.getLoadedShips().getById(this.teleporting);
		if (ship == null) {
			return;
		}
		final String levelId = VSGameUtilsKt.getDimensionId(level);

		final Vector3d position = new Vector3d(this.relativePos);
		final Quaterniond rotation = new Quaterniond();
		final Vector3d velocity = new Vector3d();
		final Vector3d omega = new Vector3d();
		final Ship selfShip = VSGameUtilsKt.getShipManagingPos(level, this.getBlockPos());
		if (selfShip != null) {
			final ShipTransform selfTransform = selfShip.getTransform();
			selfTransform.getShipToWorld().transformPosition(position);
			rotation.set(selfTransform.getShipToWorldRotation());
			velocity.set(selfShip.getVelocity());
			omega.set(selfShip.getOmega());
		}

		final ShipTeleportData teleportData = new ShipTeleportDataImpl(position, rotation, velocity, omega, levelId, null);
		shipWorld.teleportShip(ship, teleportData);

		this.finishAssemble(null);
	}
}
