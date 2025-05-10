package net.jcm.vsch.blocks.entity;

import dan200.computercraft.shared.Capabilities;

import net.jcm.vsch.blocks.entity.template.ParticleBlockEntity;
import net.jcm.vsch.compat.CompatMods;
import net.jcm.vsch.ship.gyro.GyroData;
import net.jcm.vsch.ship.VSCHForceInducedShips;

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

public class GyroBlockEntity extends BlockEntity implements ParticleBlockEntity {
	private final GyroData data;
	private volatile boolean isPeripheralMode = false;
	private boolean wasPeripheralMode = true;
	private LazyOptional<Object> lazyPeripheral = LazyOptional.empty();

	public GyroBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.GYRO_BLOCK_ENTITY.get(), pos, state);
		this.data = new GyroData(0, 0, 0);
	}

	public double getTorqueX() {
		return this.data.x;
	}

	public void setTorqueX(double x) {
		if (this.data.x == x) {
			return;
		}
		this.data.x = x;
		this.setChanged();
	}

	public double getTorqueY() {
		return this.data.y;
	}

	public void setTorqueY(double y) {
		if (this.data.y == y) {
			return;
		}
		this.data.y = y;
		this.setChanged();
	}

	public double getTorqueZ() {
		return this.data.z;
	}

	public void setTorqueZ(double z) {
		if (this.data.z == z) {
			return;
		}
		this.data.z = z;
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
		this.data.x = data.getDouble("TorqueX");
		this.data.y = data.getDouble("TorqueY");
		this.data.z = data.getDouble("TorqueZ");
		this.isPeripheralMode = CompatMods.COMPUTERCRAFT.isLoaded() && data.getBoolean("PeripheralMode");
		super.load(data);
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);
		data.putDouble("TorqueX", this.data.x);
		data.putDouble("TorqueY", this.data.y);
		data.putDouble("TorqueZ", this.data.z);
		data.putBoolean("PeripheralMode", this.getPeripheralMode());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag data = super.getUpdateTag();
		data.putDouble("TorqueX", this.data.x);
		data.putDouble("TorqueY", this.data.y);
		data.putDouble("TorqueZ", this.data.z);
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
				// lazyPeripheral = LazyOptional.of(() -> new GyroPeripheral(this));
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
		final double MAX_TORQUE = 100;
		final Level level = this.getLevel();
		final BlockPos pos = this.getBlockPos();
		this.data.x = MAX_TORQUE * (level.getSignal(pos, Direction.EAST) - level.getSignal(pos, Direction.WEST)) / 15.0;
		this.data.y = MAX_TORQUE * (level.getSignal(pos, Direction.UP) - level.getSignal(pos, Direction.DOWN)) / 15.0;
		this.data.z = MAX_TORQUE * (level.getSignal(pos, Direction.SOUTH) - level.getSignal(pos, Direction.NORTH)) / 15.0;
	}

}
