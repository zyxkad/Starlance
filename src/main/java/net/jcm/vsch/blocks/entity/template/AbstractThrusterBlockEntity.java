package net.jcm.vsch.blocks.entity.template;

import dan200.computercraft.shared.Capabilities;

import net.jcm.vsch.blocks.custom.template.AbstractThrusterBlock;
import net.jcm.vsch.compat.CompatMods;
import net.jcm.vsch.compat.cc.ThrusterPeripheral;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.lointain.cosmos.init.CosmosModParticleTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractThrusterBlockEntity extends BlockEntity implements ParticleBlockEntity {
	private final String typeString;
	private final ThrusterData thrusterData;
	private final ThrusterEngine engine;
	private volatile float power = 0;
	private volatile float currentPower = 0;
	private volatile boolean powerChanged = false;

	// Peripheral mode determines if the throttle is controlled by redstone, or by CC computers
	private volatile boolean isPeripheralMode = false;
	private boolean wasPeripheralMode = true;
	private LazyOptional<Object> lazyPeripheral = LazyOptional.empty();

	private final ThrusterEnergyStorage energyStorage;
	private final ThrusterFluidStorage fluidStorage;

	protected AbstractThrusterBlockEntity(String typeStr, BlockEntityType<?> type, BlockPos pos, BlockState state, ThrusterEngine engine) {
		super(type, pos, state);

		this.typeString = typeStr;
		this.thrusterData = new ThrusterData(
			VectorConversionsMCKt.toJOMLD(state.getValue(DirectionalBlock.FACING).getNormal()),
			0,
			state.getValue(AbstractThrusterBlock.MODE));
		this.engine = engine;
		this.energyStorage = new ThrusterEnergyStorage(this.engine);
		this.fluidStorage = new ThrusterFluidStorage(this.engine);
	}

	public String getTypeString() {
		return this.typeString;
	}

	public ThrusterEngine getEngine() {
		return this.engine;
	}

	public abstract float getMaxThrottle();

	public float getCurrentPower() {
		return this.currentPower;
	}

	protected void setCurrentPower(float power) {
		if (this.currentPower == power) {
			return;
		}
		this.currentPower = power;
		this.markPowerChanged();
	}

	public float getCurrentThrottle() {
		return this.getCurrentPower() * this.getMaxThrottle();
	}

	/**
	 * @return thruster power between 0.0~1.0
	 */
	public float getPower() {
		return this.power;
	}

	public void setPower(float power) {
		float newPower = Math.min(Math.max(power, 0), 1);
		this.power = newPower;
	}

	public boolean getPeripheralMode() {
		return this.isPeripheralMode;
	}

	public void setPeripheralMode(boolean on) {
		if (this.isPeripheralMode != on) {
			this.isPeripheralMode = on;
			this.setChanged();
		}
	}

	protected void markPowerChanged() {
		this.powerChanged = true;
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 11);
	}

	public ThrusterData.ThrusterMode getThrusterMode() {
		return this.getBlockState().getValue(AbstractThrusterBlock.MODE);
	}

	public void setThrusterMode(ThrusterData.ThrusterMode mode) {
		this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(AbstractThrusterBlock.MODE, mode));
		this.thrusterData.mode = mode;
	}

	public ThrusterData getThrusterData() {
		return this.thrusterData;
	}

	@Override
	public void load(CompoundTag data) {
		this.setPower(data.getFloat("Power"));
		this.currentPower = data.getFloat("CurrentPower");
		this.isPeripheralMode = CompatMods.COMPUTERCRAFT.isLoaded() && data.getBoolean("PeripheralMode");
		this.thrusterData.throttle = this.getCurrentThrottle();
		super.load(data);
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);
		data.putFloat("Power", this.getPower());
		data.putFloat("CurrentPower", this.getCurrentPower());
		data.putBoolean("PeripheralMode", this.getPeripheralMode());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag data = super.getUpdateTag();
		data.putFloat("CurrentPower", this.getCurrentPower());
		return data;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction direction) {
		if (cap == ForgeCapabilities.ENERGY) {
			return LazyOptional.of(() -> this.energyStorage).cast();
		}
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.of(() -> this.fluidStorage).cast();
		}
		if (CompatMods.COMPUTERCRAFT.isLoaded() && cap == Capabilities.CAPABILITY_PERIPHERAL) {
			if (!lazyPeripheral.isPresent()) {
				lazyPeripheral = LazyOptional.of(() -> new ThrusterPeripheral(this));
			}
			return lazyPeripheral.cast();
		}
		return super.getCapability(cap, direction);
	}

	public void neighborChanged(Block block, BlockPos pos, boolean moving) {
		if (!this.isPeripheralMode) {
			this.updatePowerByRedstone();
		}
	}

	@Override
	public void tickForce(ServerLevel level, BlockPos pos, BlockState state) {
		// If we have changed peripheral mode, and we aren't peripheral mode
		if (this.wasPeripheralMode != this.isPeripheralMode && !this.isPeripheralMode) {
			this.updatePowerByRedstone();
		}
		this.wasPeripheralMode = this.isPeripheralMode;

		ThrusterEngineContext context = new ThrusterEngineContext((ServerLevel)(this.getLevel()), this.energyStorage.getExtractOnly(), this.fluidStorage.getDrainOnly(), this.getPower());
		this.engine.tick(context);
		if (context.isRejected()) {
			this.setCurrentPower(0);
		} else {
			this.setCurrentPower((float)(context.getPower()));
			context.consume();
		}
		if (this.powerChanged) {
			this.powerChanged = false;
			this.thrusterData.throttle = this.getCurrentThrottle();
		}

		boolean isLit = state.getValue(AbstractThrusterBlock.LIT);
		boolean powered = getPower() > 0;
		if (powered != isLit) {
			level.setBlockAndUpdate(pos, state.setValue(AbstractThrusterBlock.LIT, powered));
		}

		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);
		if (ships == null) {
			return;
		}

		if (ships.getThrusterAtPos(pos) == null) {
			ships.addThruster(pos, this.thrusterData);
		}
	}

	private void updatePowerByRedstone() {
		float newPower = getPowerByRedstone(this.getLevel(), this.getBlockPos());
		this.setPower(newPower);
	}

	protected ParticleOptions getThrusterParticleType() {
		return CosmosModParticleTypes.THRUSTED.get();
	}

	protected ParticleOptions getThrusterSmokeParticleType() {
		return CosmosModParticleTypes.THRUST_SMOKE.get();
	}

	@Override
	public void tickParticles(Level level, BlockPos pos, BlockState state) {
		Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
		// If we aren't on a ship, then we skip
		if (ship == null) {
			return;
		}

		// If we are unpowered, do no particles
		if (this.getCurrentPower() == 0.0) {
			return;
		}

		// BlockPos is always at the corner, getCenter gives us a Vec3 thats centered YAY
		Vec3 center = pos.getCenter();
		// Transform that shipyard pos into a world pos
		Vector3d worldPos = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(center.x, center.y, center.z));

		// Get blockstate direction, NORTH, SOUTH, UP, DOWN, etc
		Direction dir = state.getValue(DirectionalBlock.FACING);
		Vector3d direction = ship.getTransform().getShipToWorldRotation().transform(new Vector3d(dir.getStepX(), dir.getStepY(), dir.getStepZ()));

		spawnParticles(worldPos, direction);
	}

	protected void spawnParticles(Vector3d pos, Vector3d direction) {
		// Offset the XYZ by a little bit so its at the end of the thruster block
		double x = pos.x - direction.x;
		double y = pos.y - direction.y;
		double z = pos.z - direction.z;

		Vector3d speed = new Vector3d(direction).mul(-this.getCurrentPower());

		speed.mul(0.6);

		// All that for one particle per tick...
		level.addParticle(
				this.getThrusterParticleType(),
				x, y, z,
				speed.x, speed.y, speed.z
				);

		speed.mul(1.06);

		// Ok ok, two particles per tick
		level.addParticle(
				this.getThrusterSmokeParticleType(),
				x, y, z,
				speed.x, speed.y, speed.z
				);
	}

	private static float getPowerByRedstone(Level level, BlockPos pos) {
		return (float)(level.getBestNeighborSignal(pos)) / 15;
	}

	private static final class ThrusterEnergyStorage implements IEnergyStorage {
		private final int maxEnergy;
		private int stored;
		private final IEnergyStorage extractOnly = this.new ExtractOnly();

		ThrusterEnergyStorage(ThrusterEngine engine) {
			this.maxEnergy = engine.getEnergyConsumeRate();
		}

		public IEnergyStorage getExtractOnly() {
			return this.extractOnly;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			int needs = this.maxEnergy - this.stored;
			if (needs < maxReceive) {
				maxReceive = needs;
			}
			if (!simulate) {
				this.stored += maxReceive;
			}
			return maxReceive;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return this.stored;
		}

		@Override
		public int getMaxEnergyStored() {
			return this.maxEnergy;
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return true;
		}

		final class ExtractOnly implements IEnergyStorage {
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				return 0;
			}

			@Override
			public int extractEnergy(int maxExtract, boolean simulate) {
				if (maxExtract > ThrusterEnergyStorage.this.stored) {
					maxExtract = ThrusterEnergyStorage.this.stored;
				}
				if (!simulate) {
					ThrusterEnergyStorage.this.stored -= maxExtract;
				}
				return maxExtract;
			}

			@Override
			public int getEnergyStored() {
				return ThrusterEnergyStorage.this.stored;
			}

			@Override
			public int getMaxEnergyStored() {
				return ThrusterEnergyStorage.this.maxEnergy;
			}

			@Override
			public boolean canExtract() {
				return true;
			}

			@Override
			public boolean canReceive() {
				return false;
			}
		}
	}

	private static final class ThrusterFluidStorage implements IFluidHandler {
		private final ThrusterEngine engine;
		private final FluidTank[] tanks;
		private final IFluidHandler drainOnly = this.new DrainOnly();

		ThrusterFluidStorage(ThrusterEngine engine) {
			this.engine = engine;
			this.tanks = new FluidTank[this.engine.getTanks()];
			for (int i = 0; i < this.tanks.length; i++) {
				this.tanks[i] = new FluidTank(10000);
			}
		}

		@Override
		public int getTanks() {
			return this.tanks.length;
		}

		public IFluidHandler getDrainOnly() {
			return this.drainOnly;
		}

		@Override
		public FluidStack getFluidInTank(int tank) {
			return this.tanks[tank].getFluid();
		}

		@Override
		public int getTankCapacity(int tank) {
			return this.tanks[tank].getCapacity();
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack) {
			return this.engine.isValidFuel(tank, stack.getFluid());
		}

		private FluidTank getFillable(FluidStack resource) {
			Fluid fluid = resource.getFluid();
			for (int i = 0; i < this.tanks.length; i++) {
				if (this.engine.isValidFuel(i, fluid)) {
					FluidTank tank = this.tanks[i];
					FluidStack stack = tank.getFluid();
					if (stack.getFluid() != fluid) {
						if (!stack.isEmpty()) {
							return null;
						}
						stack = new FluidStack(fluid, 0);
						tank.setFluid(stack);
					}
					return tank;
				}
			}
			return null;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			FluidTank tank = this.getFillable(resource);
			return tank == null ? 0 : tank.fill(resource, action);
		}

		private FluidTank getDrainable(FluidStack resource) {
			Fluid fluid = resource.getFluid();
			for (FluidTank tank : this.tanks) {
				if (tank.getFluid().getFluid() != fluid) {
					return null;
				}
				return tank;
			}
			return null;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			return FluidStack.EMPTY;
		}

		final class DrainOnly implements IFluidHandler {
			@Override
			public int getTanks() {
				return ThrusterFluidStorage.this.getTanks();
			}

			public IFluidHandler getDrainOnly() {
				return ThrusterFluidStorage.this.getDrainOnly();
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				return ThrusterFluidStorage.this.getFluidInTank(tank);
			}

			@Override
			public int getTankCapacity(int tank) {
				return ThrusterFluidStorage.this.getTankCapacity(tank);
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				return ThrusterFluidStorage.this.isFluidValid(tank, stack);
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				return 0;
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				FluidTank tank = ThrusterFluidStorage.this.getDrainable(resource);
				return tank == null ? FluidStack.EMPTY : tank.drain(resource, action);
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				for (FluidTank tank : ThrusterFluidStorage.this.tanks) {
					FluidStack stack = tank.drain(maxDrain, action);
					if (!stack.isEmpty()) {
						return stack;
					}
				}
				return FluidStack.EMPTY;
			}
		}
	}

	public static class ThrusterEngine {
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

	public static class ThrusterEngineContext {
		@FunctionalInterface
		public interface EngineConsumeAction {
			void consume(ThrusterEngineContext ctx);
		}

		private final ServerLevel level;
		private final IEnergyStorage energy;
		private final IFluidHandler tanks;
		private final List<EngineConsumeAction> consumers = new ArrayList<>(2);
		private double power;
		private boolean rejected = false;

		/**
		 * @param level  The level the thruster is in
		 * @param energy The engine's energy storage, extract only
		 * @param tanks  The engine's fluid tanks, drain only
		 * @param power  The maximum power (in range of [0.0, 1.0]) the engine should maximum run with
		 */
		public ThrusterEngineContext(ServerLevel level, IEnergyStorage energy, IFluidHandler tanks, double power) {
			this.level = level;
			this.energy = energy;
			this.tanks = tanks;
			this.power = power;
		}

		public void reject() {
			this.rejected = true;
		}

		public boolean isRejected() {
			return this.rejected;
		}

		public ServerLevel getLevel() {
			return this.level;
		}

		public IEnergyStorage getEnergyStorage() {
			return this.energy;
		}

		public IFluidHandler getFluidHandler() {
			return this.tanks;
		}

		public void addConsumer(EngineConsumeAction consumer) {
			this.consumers.add(consumer);
		}

		public double getPower() {
			return this.power;
		}

		public void setPower(double power) {
			this.power = power;
		}

		void consume() {
			for (EngineConsumeAction consumer : this.consumers) {
				consumer.consume(this);
			}
		}
	}
}
