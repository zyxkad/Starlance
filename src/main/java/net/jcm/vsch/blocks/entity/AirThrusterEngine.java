package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.thruster.ThrusterEngine;
import net.jcm.vsch.blocks.thruster.ThrusterEngineContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class AirThrusterEngine extends ThrusterEngine {
    private final int maxWaterConsumeRate;

    public AirThrusterEngine(int energyConsumeRate, float maxThrottle, int maxWaterConsumeRate) {
        super(1, energyConsumeRate, maxThrottle);
        this.maxWaterConsumeRate = maxWaterConsumeRate;
    }

    @Override
    public boolean isValidFuel(int tank, Fluid fluid) {
        return fluid == Fluids.WATER;
    }

    @Override
    public void tick(ThrusterEngineContext context) {
        super.tick(context);
        if (this.maxWaterConsumeRate == 0) {
            return;
        }
        double power = context.getPower();
        if (power == 0) {
            return;
        }
        double density = getLevelAirDensity(context.getLevel());
        if (density >= 1) {
            return;
        }
        int amount = context.getAmount();

        double waterConsumeRate = this.maxWaterConsumeRate * (1 - density);
        int needsWater = (int) (Math.ceil(waterConsumeRate * power * amount));
        int avaliableWater = context.getFluidHandler().drain(new FluidStack(Fluids.WATER, needsWater), IFluidHandler.FluidAction.SIMULATE).getAmount();
        context.setPower(avaliableWater / (waterConsumeRate * amount));
        context.addConsumer((ctx) -> {
            int water = (int) (Math.ceil(this.maxWaterConsumeRate * (1 - density) * ctx.getPower() * ctx.getAmount()));
            ctx.getFluidHandler().drain(new FluidStack(Fluids.WATER, water), IFluidHandler.FluidAction.EXECUTE);
        });
    }

    @Override
    public void tickBurningObjects(final ThrusterEngineContext context, final List<BlockPos> thrusters, final Direction direction) {
        //
    }

    /**
     * @return the air density relative to {@code minecraft:overworld}
     * @todo use API from somewhere instead
     */
    private static double getLevelAirDensity(ServerLevel level) {
        if (level.dimension() == Level.OVERWORLD) {
            return 1.0;
        }
        if (level.dimension() == Level.NETHER) {
            return 1.2;
        }
        if (level.dimension() == Level.END) {
            return 0.0;
        }
        return 0.0;
    }
}
