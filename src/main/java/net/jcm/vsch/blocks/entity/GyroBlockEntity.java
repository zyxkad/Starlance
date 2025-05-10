package net.jcm.vsch.blocks.entity;

import dan200.computercraft.shared.Capabilities;

import net.jcm.vsch.blocks.entity.template.ParticleBlockEntity;
import net.jcm.vsch.compat.CompatMods;
import net.jcm.vsch.compat.cc.peripherals.GyroPeripheral;
import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.ship.gyro.GyroData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import org.joml.Vector3d;

public class GyroBlockEntity extends BlockEntity implements ParticleBlockEntity {
	private final GyroData data;
	private volatile double torqueX = 0;
	private volatile double torqueY = 0;
	private volatile double torqueZ = 0;
	private volatile boolean isPeripheralMode = false;
	private boolean wasPeripheralMode = true;
	private LazyOptional<Object> lazyPeripheral = LazyOptional.empty();

	public GyroBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.GYRO_BLOCK_ENTITY.get(), pos, state);
		this.data = new GyroData(new Vector3d());
	}

	public double getTorqueForce() {
		return VSCHConfig.GYRO_STRENGTH.get().doubleValue();
	}

	public double getTorqueX() {
		return this.torqueX;
	}

	public void setTorqueX(double x) {
		x = Math.min(Math.max(x, -1), 1);
		if (this.torqueX == x) {
			return;
		}
		this.torqueX = x;
		this.setChanged();
	}

	public double getTorqueY() {
		return this.torqueY;
	}

	public void setTorqueY(double y) {
		y = Math.min(Math.max(y, -1), 1);
		if (this.torqueY == y) {
			return;
		}
		this.torqueY = y;
		this.setChanged();
	}

	public double getTorqueZ() {
		return this.torqueZ;
	}

	public void setTorqueZ(double z) {
		z = Math.min(Math.max(z, -1), 1);
		if (this.torqueZ == z) {
			return;
		}
		this.torqueZ = z;
		this.setChanged();
	}

	public void resetTorque() {
		this.torqueX = 0;
		this.torqueY = 0;
		this.torqueZ = 0;
		this.setChanged();
	}

	public double[] getTorque() {
		return new double[]{this.torqueX, this.torqueY, this.torqueZ};
	}

	public void setTorque(double x, double y, double z) {
		this.torqueX = Math.min(Math.max(x, -1), 1);
		this.torqueY = Math.min(Math.max(y, -1), 1);
		this.torqueZ = Math.min(Math.max(z, -1), 1);
		this.setChanged();
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

	@Override
	public void tickForce(ServerLevel level, BlockPos pos, BlockState state) {
		if (this.wasPeripheralMode != this.isPeripheralMode && !this.isPeripheralMode) {
			this.updatePowerByRedstone();
		}
		this.wasPeripheralMode = this.isPeripheralMode;

		final double force = this.getTorqueForce();
		this.data.torque.set(this.torqueX * force, this.torqueY * force, this.torqueZ * force);

		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);
		if (ships == null) {
			return;
		}
		if (ships.getGyroAtPos(pos) == null) {
			ships.addGyro(pos, this.data);
		}
	}

	@Override
	public void tickParticles(Level level, BlockPos pos, BlockState state) {
		//
	}

	@Override
	public void load(CompoundTag data) {
		this.torqueX = data.getDouble("TorqueX");
		this.torqueY = data.getDouble("TorqueY");
		this.torqueZ = data.getDouble("TorqueZ");
		this.isPeripheralMode = CompatMods.COMPUTERCRAFT.isLoaded() && data.getBoolean("PeripheralMode");
		super.load(data);
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);
		data.putDouble("TorqueX", this.torqueX);
		data.putDouble("TorqueY", this.torqueY);
		data.putDouble("TorqueZ", this.torqueZ);
		data.putBoolean("PeripheralMode", this.getPeripheralMode());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag data = super.getUpdateTag();
		data.putDouble("TorqueX", this.torqueX);
		data.putDouble("TorqueY", this.torqueY);
		data.putDouble("TorqueZ", this.torqueZ);
		return data;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction direction) {
		if (CompatMods.COMPUTERCRAFT.isLoaded() && cap == Capabilities.CAPABILITY_PERIPHERAL) {
			if (!lazyPeripheral.isPresent()) {
				lazyPeripheral = LazyOptional.of(() -> new GyroPeripheral(this));
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

	private void updatePowerByRedstone() {
		final Level level = this.getLevel();
		final BlockPos pos = this.getBlockPos();
		final double x = (level.getSignal(pos.east(), Direction.EAST) - level.getSignal(pos.west(), Direction.WEST)) / 15.0;
		final double y = (level.getSignal(pos.above(), Direction.UP) - level.getSignal(pos.below(), Direction.DOWN)) / 15.0;
		final double z = (level.getSignal(pos.south(), Direction.SOUTH) - level.getSignal(pos.north(), Direction.NORTH)) / 15.0;
		this.setTorque(x, y, z);
	}
}
