package net.jcm.vsch.ship;

import org.joml.Vector3d;

public class ThrusterData {
    public final Vector3d dir;
    public float throttle;

    public ThrusterData(Vector3d dir, float throttle) {
        this.dir = dir;
        this.throttle = throttle;
    }
    
    public String toString() {
    	return "Direction: "+this.dir+" Throttle: "+throttle;
    }
}
