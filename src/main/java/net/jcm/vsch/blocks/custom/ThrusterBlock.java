package net.jcm.vsch.blocks.custom;


import java.util.function.Supplier;

import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.util.rot.DirectionalShape;
import net.jcm.vsch.util.rot.RotShape;
import net.jcm.vsch.util.rot.RotShapes;


public class ThrusterBlock extends DirectionalBlock {

	public static final int Mult = 1000;
	//TODO: fix this bounding box
    private static final RotShape SHAPE = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private final DirectionalShape Thruster_SHAPE = DirectionalShape.south(SHAPE);

    /*public ThrusterBlock(Supplier<Double> mult, Supplier<Integer> maxTier) {
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(1.0f, 2.0f));
        this.mult = mult;
        this.maxTier = maxTier;

        
    }*/

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
    	return signal * Mult;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!(level instanceof ServerLevel)) return;

        int signal = level.getBestNeighborSignal(pos);
        VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);

        if (ships != null) {
            ships.addThruster(pos, new ThrusterData(
            		VectorConversionsMCKt.toJOMLD(state.getValue(FACING).getNormal()),
                    getThrottle(state, signal)
            ));
            //ships.updateThrusterV2(pos);
        }
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel)) return;

        VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);
        if (ships != null) {
            ships.removeThruster(pos);
        }

        super.onRemove(state, level, pos, newState, isMoving);
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
        System.out.println("ships");
        System.out.println(ships);
        System.out.println("Signal");
        System.out.println(signal);

        if (ships != null) {
            ThrusterData data = ships.getThrusterAtPos(pos);
            if (data != null) {
            	System.out.println("data:");
            	System.out.println(data);
                float newThrottle = getThrottle(state, signal);
                if (data.throttle != newThrottle) {
                	System.out.println("Throttle");
                	System.out.println(newThrottle);
                    data.throttle = newThrottle;
                }
                System.out.println(data);
                System.out.println(ships.getThrusterAtPos(pos));
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
}