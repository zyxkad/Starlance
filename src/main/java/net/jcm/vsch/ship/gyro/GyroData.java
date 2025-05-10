package net.jcm.vsch.ship.gyro;

public class GyroData {
	public volatile double x;
	public volatile double y;
	public volatile double z;

	public GyroData(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return "Torque: (" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}
