package net.jcm.vsch.blocks.custom;


import net.jcm.vsch.config.VSCHConfig;
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
import net.jcm.vsch.blocks.entity.DragInducerBlockEntity;
import net.jcm.vsch.blocks.entity.ThrusterBlockEntity;
import net.jcm.vsch.ship.DraggerData;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.util.rot.DirectionalShape;
import net.jcm.vsch.util.rot.RotShape;
import net.jcm.vsch.util.rot.RotShapes;
import net.lointain.cosmos.init.CosmosModItems;


public class DragInducerBlock extends Block implements EntityBlock { //

	//TODO: fix this bounding box
	private static final RotShape SHAPE = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private final DirectionalShape dragger_shape = DirectionalShape.south(SHAPE);


	public DragInducerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
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

			if(VSCHConfig.THRUSTER_MODE.get().equals("POSITION")) {
				ships.addDragger(pos, new DraggerData(
						(signal > 0),
						ThrusterData.ThrusterMode.POSITION //Position based thruster by default
						));
			} else if (VSCHConfig.THRUSTER_MODE.get().equals("GLOBAL")) {
				ships.addDragger(pos, new DraggerData(
						(signal > 0),
						ThrusterData.ThrusterMode.GLOBAL //Position based thruster by default
						));
			}

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
			ships.removeDragger(pos);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// If client side, ignore 
		if (!(level instanceof ServerLevel)) return InteractionResult.PASS;
		// If its the right item and mainhand
		if (player.getMainHandItem().getItem() == CosmosModItems.ENERGY_METER.get() && hand == InteractionHand.MAIN_HAND) {
			if(VSCHConfig.THRUSTER_TOGGLE.get()){
				// Get the force handler
				VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

				// If a force handler exists (might not if we aren't on a VS ship)
				if (ships != null) {
					DraggerData data = ships.getDraggerAtPos(pos);

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

		if (ships != null) {
			DraggerData data = ships.getDraggerAtPos(pos);

			if (data != null) {
				data.on = (signal > 0);	
			}
		}
	}



	// Attach block entity
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DragInducerBlockEntity(pos, state);
	}

	/*public static <T extends BlockEntity> BlockEntityTicker<T> getTickerHelper(Level level) {
		return level.isClientSide() && !allowClient ? null : (level0, pos0, state0, blockEntity) -> ((TickableBlockEntity)blockEntity).tick();
	}*/
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		// TODO Auto-generated method stub
		if (!level.isClientSide()) {
			System.out.println("serverside ticker");
			return (level0, pos0, state0, blockEntity) -> ((DragInducerBlockEntity)blockEntity).serverTick(level0, pos0, state0, (DragInducerBlockEntity) blockEntity);
		} else {
			System.out.println("clientside ticker");
			return null;
		}
		//return level.isClientSide() ? (level0, pos0, state0, blockEntity) -> ((DragInducerBlockEntity)blockEntity).clientTick(level0, pos0, state0, (DragInducerBlockEntity) blockEntity) : (level0, pos0, state0, blockEntity) -> ((DragInducerBlockEntity)blockEntity).serverTick(level0, pos0, state0, (DragInducerBlockEntity) blockEntity);
	}


}
