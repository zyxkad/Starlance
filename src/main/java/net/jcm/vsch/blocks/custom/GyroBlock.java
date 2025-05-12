package net.jcm.vsch.blocks.custom;

import net.jcm.vsch.blocks.entity.template.ParticleBlockEntity;
import net.jcm.vsch.blocks.entity.GyroBlockEntity;
import net.jcm.vsch.items.VSCHItems;
import net.jcm.vsch.util.VSCHUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

public class GyroBlock extends Block implements EntityBlock {
	public GyroBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
		super.neighborChanged(state, world, pos, neighbor, neighborPos, moving);
		GyroBlockEntity be = (GyroBlockEntity) world.getBlockEntity(pos);
		be.neighborChanged(neighbor, neighborPos, moving);
	}

	private boolean holdingWrench(Player player) {
		return player.getMainHandItem().getItem() == VSCHItems.WRENCH.get();
	}

	// Survival mode left click with wrench
	@Override
	public void attack(BlockState state, Level level, BlockPos pos, Player player) {
		super.attack(state, level, pos, player);

		if (player.isCreative() || player.isSpectator() || !holdingWrench(player)) {
			return;
		}

		onWrenchBreak(state, level, pos, player);
	}

	// Creative mode left click with wrench
	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (holdingWrench(player)) {
			onWrenchBreak(state, level, pos, player);
			return false;
		}
		return true;
	}

	// Called on both gamemodes, once on client, once on server
	private void onWrenchBreak(BlockState state, Level level, BlockPos pos, Player player) {
		if (level instanceof ServerLevel) {
			System.out.println("broken");
		}
	}


	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// If client side, ignore
		if (!(level instanceof ServerLevel)) {
			return InteractionResult.PASS;
		}

		// If its the right item and mainhand
		if (player.getMainHandItem().getItem() != VSCHItems.WRENCH.get() || hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}

		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof GyroBlockEntity)) {
			return InteractionResult.PASS;
		}

        int current = ((GyroBlockEntity) be).getPercentPower();
		current += 1;


		if (current > 10) {
			current = 1;
		}

		((GyroBlockEntity) be).setPercentPower(current);

		player.displayClientMessage(Component.translatable("vsch.message.gyro", (current*10)), true);

		return InteractionResult.SUCCESS;
	}



	@Override
	public GyroBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GyroBlockEntity(pos, state);
	}

	@Override
	public <U extends BlockEntity> BlockEntityTicker<U> getTicker(Level level, BlockState state, BlockEntityType<U> type) {
		return level.isClientSide() ? (ParticleBlockEntity::clientTick) : ParticleBlockEntity::serverTick;
	}
}
