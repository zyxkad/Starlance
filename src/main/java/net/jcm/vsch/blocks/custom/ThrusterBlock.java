package net.jcm.vsch.blocks.custom;


import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.items.VSCHItems;

import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.blocks.entity.ThrusterBlockEntity;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.util.rot.DirectionalShape;
import net.jcm.vsch.util.rot.RotShape;
import net.jcm.vsch.util.rot.RotShapes;
import net.lointain.cosmos.init.CosmosModItems;


public class ThrusterBlock extends DirectionalBlock implements EntityBlock {

	public static final int MULT = 1000;
	//TODO: fix this bounding box
	private static final RotShape SHAPE = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private final DirectionalShape Thruster_SHAPE = DirectionalShape.south(SHAPE);


	public ThrusterBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH)
		);
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Thruster_SHAPE.get(state.getValue(BlockStateProperties.FACING));
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		super.createBlockStateDefinition(builder);
	}

	public float getThrottle(BlockState state, int signal) {
		//return state.getValue(TournamentProperties.TIER) * signal * mult.get().floatValue();
		return signal * VSCHConfig.THRUSTER_STRENGTH.get().intValue();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);

		if (!(level instanceof ServerLevel)) return;

		// ----- Add thruster to the force appliers for the current level ----- //
		int signal = level.getBestNeighborSignal(pos);
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

		if (ships != null) {
			ships.addThruster(pos, new ThrusterData(
					VectorConversionsMCKt.toJOMLD(state.getValue(FACING).getNormal()),
					getThrottle(state, signal),
					ThrusterData.ThrusterMode.valueOf(VSCHConfig.THRUSTER_MODE.get()) // handy string to Enum :D
			));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(level instanceof ServerLevel)) return;

		// ----- Remove the thruster from the force appliers for the current level ----- //
		// I guess VS does this automatically when switching a shipyards dimension?
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);
		if (ships != null) {
			ships.removeThruster(pos);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

		// If client side, ignore
		if (!(level instanceof ServerLevel)) return InteractionResult.PASS;
		// If its the right item and mainhand
		if (player.getMainHandItem().getItem() == VSCHItems.WRENCH.get() && hand == InteractionHand.MAIN_HAND) {
			if(VSCHConfig.THRUSTER_TOGGLE.get()){
				// Get the force handler
				VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

				// If a force handler exists (might not if we aren't on a VS ship)
				if (ships != null) {
					ThrusterData data = ships.getThrusterAtPos(pos);

					// Probably unneeded, but checks are always good right?
					if (data != null) {

						if (data.mode == ThrusterData.ThrusterMode.POSITION) {
							data.mode = ThrusterData.ThrusterMode.GLOBAL;
							//TODO: Find a way to change this message if the last message was the same (so it looks like a new message)
							player.displayClientMessage(Component.literal("Set thruster to GLOBAL").withStyle(ChatFormatting.GOLD), true);
						} else {
							data.mode = ThrusterData.ThrusterMode.POSITION;

							player.displayClientMessage(Component.literal("Set thruster to POSITION").withStyle(ChatFormatting.YELLOW), true);
						}
						return InteractionResult.CONSUME;
					}
				}
			} else if (!VSCHConfig.THRUSTER_TOGGLE.get()) {
				player.displayClientMessage(Component.literal("Thruster Mode Toggling is disabled").withStyle(ChatFormatting.RED), true);
			}
		}

		return InteractionResult.PASS;
	}


	/*@Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, builder));
        int tier = state.getValue(TournamentProperties.TIER);
        if (tier > 1) {
            drops.add(new ItemStack(TournamentItems.UPGRADE_THRUSTER.get(), tier - 1));
        }
        return drops;
    }*/

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);

		if (!(level instanceof ServerLevel)) return;

		int signal = level.getBestNeighborSignal(pos);
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);
		/*System.out.println("ships");
		System.out.println(ships);
		System.out.println("Signal");
		System.out.println(signal);*/

		if (ships != null) {
			ThrusterData data = ships.getThrusterAtPos(pos);

			if (data != null) {
				float newThrottle = getThrottle(state, signal);

				if (data.throttle != newThrottle) {
					data.throttle = newThrottle;
				}

			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction dir = ctx.getNearestLookingDirection();
		if (ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
			dir = dir.getOpposite();
		}
		return defaultBlockState().setValue(BlockStateProperties.FACING, dir);
	}



	// Attach block entity
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ThrusterBlockEntity(pos, state);
	}

	/*public static <T extends BlockEntity> BlockEntityTicker<T> getTickerHelper(Level level) {
		return level.isClientSide() && !allowClient ? null : (level0, pos0, state0, blockEntity) -> ((TickableBlockEntity)blockEntity).tick();
	}*/
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		// TODO Auto-generated method stub
		return level.isClientSide() ? (level0, pos0, state0, blockEntity) -> ((ThrusterBlockEntity)blockEntity).clientTick(level0, pos0, state0, (ThrusterBlockEntity) blockEntity) : (level0, pos0, state0, blockEntity) -> ((ThrusterBlockEntity)blockEntity).serverTick(level0, pos0, state0, (ThrusterBlockEntity) blockEntity);
	}


}