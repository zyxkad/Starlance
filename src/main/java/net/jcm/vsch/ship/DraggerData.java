package net.jcm.vsch.ship;

import org.joml.Vector3d;

public class DraggerData {

	public boolean on;

	public DraggerData(boolean on) {
		this.on = on;
	}

	public String toString() {
		return " State: "+this.on;
	}
}
