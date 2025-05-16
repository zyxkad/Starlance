package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.custom.RocketSupporterBlock;
import net.jcm.vsch.blocks.entity.template.ParticleBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Predicate;

public class RocketSupporterBlockEntity extends BlockEntity implements ParticleBlockEntity {
	private static final int MAX_SIZE = 256 * 16;
	private static final int MAX_BLOCKS = 16 * 16 * 16 * 16 * 9;

	private boolean triggering = false;
	private boolean assembling = false;
	private Boolean assembleResult = null;
	private final Queue<BlockPos> queueing = new ArrayDeque<>();
	private final DenseBlockPosSet blocks = new DenseBlockPosSet();
	private final DenseBlockPosSet checked = new DenseBlockPosSet();

	public RocketSupporterBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.ROCKET_SUPPORTER_BLOCK_ENTITY.get(), pos, state);
	}

	public boolean isAssembling() {
		return this.assembling;
	}

	public boolean getAssembleResult() {
		return this.assembleResult != null && this.assembleResult.booleanValue();
	}

	public void neighborChanged(Block neighbor, BlockPos neighborPos, boolean moving) {
		final Level level = this.getLevel();
		final BlockPos pos = this.getBlockPos();
		final boolean shouldTrigger = Direction.stream()
			.filter(dir -> dir != this.getBlockState().getValue(DirectionalBlock.FACING))
			.anyMatch(dir -> level.getSignal(pos.relative(dir), dir));
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
			this.assembleTick();
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
		this.assembleResult = null;
		this.queueing.clear();
		this.blocks.clear();
		this.checked.clear();
		this.queueing.add(this.getBlockPos().relative(this.getBlockState().getValue(DirectionalBlock.FACING)));
	}

	private void finishAssemble(boolean result) {
		this.assembling = false;
		this.assembleResult = result;
		this.queueing.clear();
		this.blocks.clear();
		this.checked.clear();
	}

	private void assembleTick() {
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
			if (pos.equals(selfPos)) {
				this.finishAssemble(false);
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
		final ServerShip ship = this.shipWorld.createNewShipAtBlock(new Vector3i(selfPos.getX(), selfPos.getY(), selfPos.getZ()), false, 1.0, VSGameUtilsKt.getDimensionId(this.getLevel()));
		this.moveBlocks(ship);
	}

	private boolean checkBlock(final BlockPos pos) {
		final Level level = this.getLevel();
		if (!level.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
			this.finishAssemble(false);
			return false;
		}
		final BlockState block = level.getBlockState(pos);
		if (this.isAirBlock(block)) {
			return true;
		}
		if (!this.canAssembleBlock(block)) {
			this.finishAssemble(false);
			return false;
		}
		for (final Direction dir : Direction.values()) {
			final BlockPos p = pos.relative(dir);
			final BlockState block = level.getBlockState(p);
			if (block.getBlock() instanceof RocketSupporterBlock && block.getValue(DirectionalBlock.FACING) == dir.getOpposite()) {
				if (((RocketSupporterBlockEntity) (level.getBlockEntity(pos))).isAssembling()) {
					this.finishAssemble(true);
					return false;
				}
				continue;
			}
			if (this.checked.add(p)) {
				this.queueing.add(p);
			}
		}
		return true;
	}

	private boolean canAssembleBlock(final BlockState state) {
		final Block block = state.getBlock();
		if (block == Block.BEDROCK) {
			return false;
		}
		// TODO: load blacklist from config
		return true;
	}

	private void moveBlocks(final ServerShip ship) {
		//
	}
}
