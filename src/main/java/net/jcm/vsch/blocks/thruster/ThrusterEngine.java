package net.jcm.vsch.blocks.thruster;

import net.minecraft.world.level.material.Fluid;

public class ThrusterEngine {
	private final int tanks;
	private final int energyConsumeRate;

	public ThrusterEngine(int tanks, int energyConsumeRate) {
		this.tanks = tanks;
		this.energyConsumeRate = energyConsumeRate;
	}

	public int getTanks() {
		return this.tanks;
	}

	public int getEnergyConsumeRate() {
		return this.energyConsumeRate;
	}

	/**
	 * isValidFuel checks if the fluid can be uses as fuel.
	 * same fluid must <b>NOT</b> be able to fill in two different tanks.
	 *
	 * @param tank  The tank the fuel is going to transfer in
	 * @param fluid The fuel's fluid stack
	 * @return {@code true} if the fluid is consumable, {@code false} otherwise
	 */
	public boolean isValidFuel(int tank, Fluid fluid) {
		return false;
	}

	/**
	 * tick ticks the engine with given power, which consumes energy and fuels,
	 * and update the actual achieved power based on avaliable energy and fuels
	 *
	 * @param context The {@link ThrusterEngineContext}
	 * 
	 * @see ThrusterEngineContext
	 */
	public void tick(ThrusterEngineContext context) {
		if (this.energyConsumeRate == 0) {
			return;
		}
		double power = context.getPower();
		if (power == 0) {
			return;
		}
		int needs = (int)(Math.ceil(this.energyConsumeRate * power));
		int extracted = context.getEnergyStorage().extractEnergy(needs, true);
		context.setPower((double)(extracted) / this.energyConsumeRate);
		context.addConsumer((ctx) -> {
			ctx.getEnergyStorage().extractEnergy((int)(Math.ceil(this.energyConsumeRate * ctx.getPower())), false);
		});
	}
}
