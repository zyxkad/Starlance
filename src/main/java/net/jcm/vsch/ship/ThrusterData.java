package net.jcm.vsch.ship;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class ThrusterData {
	
	public enum ThrusterMode implements StringRepresentable {
		POSITION("POSITION"),
		GLOBAL("GLOBAL");

        public final String name;

        ThrusterMode(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
        public @NotNull String toString() {
            return this.name;
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
