package net.jcm.vsch.ship;

import org.joml.Vector3d;

import net.minecraft.util.StringRepresentable;

public class ThrusterData {

	public enum ThrusterMode implements StringRepresentable  {
		POSITION,
		GLOBAL;

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}

		public ThrusterMode toggle() {
			return this == POSITION ? GLOBAL : POSITION;
		}
	}

	public final Vector3d dir;
	public float throttle;
	public ThrusterMode mode;

	public ThrusterData(Vector3d dir, float throttle, ThrusterMode mode) {
		this.dir = dir;
		this.throttle = throttle;
		this.mode = mode;
	}

	public String toString() {
		return "Direction: "+this.dir+" Throttle: "+this.throttle+" Mode: "+this.mode;
	}
}
