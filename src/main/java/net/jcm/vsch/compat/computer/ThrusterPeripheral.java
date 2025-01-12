package net.jcm.vsch.compat.computer;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import net.jcm.vsch.blocks.entity.AbstractThrusterBlockEntity;

public class ThrusterPeripheral implements IPeripheral {
	private final AbstractThrusterBlockEntity entity;

	public ThrusterPeripheral(AbstractThrusterBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public Object getTarget() {
		return this.entity;
	}

	@Override
	public String getType() {
		return "thruster";
	}

	@LuaFunction
	public String getThrusterType() {
		return this.entity.getTypeString();
	}

	@LuaFunction
	public boolean getComputerMode() {
		return this.entity.getComputerMode();
	}

	@LuaFunction
	public void setComputerMode(boolean mode) {
		this.entity.setComputerMode(mode);
	}

	@LuaFunction
	public float getPower() {
		return this.entity.getPower();
	}

	@LuaFunction
	public String setPower(double power) {
		if (!this.entity.getComputerMode()) {
			return "Computer mode is off";
		}
		this.entity.setPower((float) power);
		return null;
	}

	@LuaFunction
	public float getMaxThrottle() {
		return this.entity.getMaxThrottle();
	}

	@LuaFunction
	public float getThrottle() {
		return this.entity.getThrottle();
	}

	@Override
	public boolean equals(IPeripheral other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ThrusterPeripheral otherThruster) {
			return this.entity == otherThruster.entity;
		}
		return false;
	}
}
