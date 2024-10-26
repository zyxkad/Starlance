package net.jcm.vsch.ship;

import org.joml.Vector3d;

public class ThrusterData {
	
	public enum ThrusterMode {
		POSITION,
		GLOBAL
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
