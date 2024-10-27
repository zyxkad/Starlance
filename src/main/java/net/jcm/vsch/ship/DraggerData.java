package net.jcm.vsch.ship;

import org.joml.Vector3d;

import net.jcm.vsch.ship.ThrusterData.ThrusterMode;

public class DraggerData {

	public boolean on;
	public ThrusterMode mode;

	public DraggerData(boolean on, ThrusterMode mode) {
		this.on = on;
		this.mode = mode;
	}

	public String toString() {
		return " State: "+this.on+" Mode: "+this.mode;
	}
}
