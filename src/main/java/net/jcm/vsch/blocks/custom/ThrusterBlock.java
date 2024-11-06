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
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.blocks.entity.ThrusterBlockEntity;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.ThrusterData.ThrusterMode;
import net.jcm.vsch.util.rot.DirectionalShape;
import net.jcm.vsch.util.rot.RotShape;
import net.jcm.vsch.util.rot.RotShapes;
import net.lointain.cosmos.init.CosmosModItems;


public class ThrusterBlock extends DirectionalBlock implements EntityBlock {

	public static final int MULT = 1000;
	//TODO: fix this bounding box
	private static final RotShape SHAPE = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	public static final EnumProperty<ThrusterMode> MODE = EnumProperty.create("mode", ThrusterMode.class);
	private final DirectionalShape Thruster_SHAPE = DirectionalShape.south(SHAPE);
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;


	public ThrusterBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(MODE, ThrusterMode.POSITION) // handy string to Enum :D
				.setValue(LIT, Boolean.valueOf(false))
				);
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(MODE);
		builder.add(LIT);
		super.createBlockStateDefinition(builder);
	}


	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}


	// Should run whenever the block state is changed, but not when the block itself is replaced
	//TODO: See if we can move setting the force inducer to here instead of all over the place (would require some extra stuff for redstone change tho)
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Thruster_SHAPE.get(state.getValue(BlockStateProperties.FACING));
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
					state.getValue(MODE) 
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

			// If thrusters can be toggled
			if(VSCHConfig.THRUSTER_TOGGLE.get()){

				// Get the force handler
				VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

				// If a force handler exists (might not if we aren't on a VS ship)
				if (ships != null) {

					// Get thruster
					ThrusterData thruster = ships.getThrusterAtPos(pos);

					// Probably unneeded, but checks are always good right?
					if (thruster != null) {

						// Get current state (block property)
						ThrusterMode blockMode = state.getValue(MODE);

						// Toggle it between POSITION and GLOBAL
						blockMode = blockMode.toggle();

						// Save the block property into the block
						level.setBlockAndUpdate(pos, state.setValue(MODE, blockMode));

						// Set the thruster data to the new toggled property
						thruster.mode = blockMode;

						//Send a chat message to them. The wrench class will handle the actionbar
						player.sendSystemMessage(Component.translatable("vsch.message.toggle").append(Component.translatable("vsch."+blockMode.toString().toLowerCase())));

						return InteractionResult.CONSUME;

					} else {
						// Somethings gone wrong, make us a new thruster
						int signal = level.getBestNeighborSignal(pos);
						ships.addThruster(pos, new ThrusterData(
								VectorConversionsMCKt.toJOMLD(state.getValue(FACING).getNormal()),
								getThrottle(state, signal),
								state.getValue(MODE) 
								));
					}
				} else {
					//Not on a ship
					player.displayClientMessage(Component.translatable("vsch.error.thruster_not_on_ship").withStyle(ChatFormatting.RED), true);
				}
			} else if (!VSCHConfig.THRUSTER_TOGGLE.get()) {
				player.displayClientMessage(Component.translatable("vsch.error.thruster_modes_disabled").withStyle(ChatFormatting.RED), true);
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

		if (signal > 0) {
			if (!state.getValue(LIT).booleanValue()) { //If we aren't lit
				level.setBlockAndUpdate(pos, state.setValue(LIT, Boolean.valueOf(true)));
			}
		} else {
			if (state.getValue(LIT).booleanValue()) { //If we ARE lit
				level.setBlockAndUpdate(pos, state.setValue(LIT, Boolean.valueOf(false)));
			}
		}

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
		return defaultBlockState()
				.setValue(BlockStateProperties.FACING, dir)
				.setValue(MODE, ThrusterMode.valueOf(VSCHConfig.THRUSTER_MODE.get()));
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
