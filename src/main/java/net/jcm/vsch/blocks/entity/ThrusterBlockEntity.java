package net.jcm.vsch.blocks.entity;

import java.text.NumberFormat;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ThrusterBlockEntity extends BlockEntity {

	public ThrusterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.THRUSTER_BLOCK_ENTITY.get(), pos, state);
	}

	public void tick(Level level, BlockPos pos, BlockState state, ThrusterBlockEntity be) {
		System.out.println("tick");

		Ship ship = VSGameUtilsKt.getShipManagingPos(level,pos);
		if (ship == null){
			System.out.println("no ship");
			return;};
			//var rp = ship.getTransform().getShipToWorld().transformPosition(VectorConversionsMCKt.toJOMLD(pos));
			Direction dir = state.getValue(DirectionalBlock.FACING);
			/*double vel = 1;
		int particleCount = 20;

		var x = rp.x + (0.5 * (dir.getStepX() + 1));
		var y = rp.y + (0.5 * (dir.getStepY() + 1));
		var z = rp.z + (0.5 * (dir.getStepZ() + 1));
		var speed = ship.getTransform().getShipToWorldRotation().getEulerAnglesXYZ(new Vector3d(0.0,0.0,0.0)).add(VectorConversionsMCKt.toJOMLD(dir.getNormal())).mul(new Vector3d(vel,vel,vel));
		for (int i = 0; i < particleCount; i++) {

		}*/

			/*Vector3d realPos = VSGameUtilsKt.toWorldCoordinates(ship, pos);
		System.out.println("Realpos:");
		System.out.println(realPos.toString(NumberFormat.getIntegerInstance()));

		// Offset to get center of block, then offset by the blocks rotation (devided by 2 because its -1->1 and we need -0.5->0.5)
		realPos = realPos.add(new Vector3d(0.5-(dir.getStepX()/2), 0.5-(dir.getStepY()/2), 0.5-(dir.getStepZ()/2)));
		System.out.println(realPos.toString(NumberFormat.getIntegerInstance()));

		double d0 = realPos.x() - 0.7D * dir.getStepX();
		double d1 = realPos.y() - 0.7D * dir.getStepY();
		double d2 = realPos.z() - 0.7D * dir.getStepZ();

		Vector3d newPos = new Vector3d(d0, d1, d2);
		Vector3d newPosCopy = new Vector3d(newPos.x, newPos.y, newPos.z);

		System.out.println("Newpos: "+newPos);
		System.out.println("Newpos2: "+newPosCopy);

		Vector3d direction = newPos.sub(realPos);

		System.out.println("Newpos: "+newPos); 
		System.out.println("Newpos2: "+newPosCopy);

		System.out.println("Direction: "+direction);
		//XYZ, YXZ
		Vector3d transformedDirection = direction.add(ship.getTransform().getShipToWorldRotation().getEulerAnglesZYX(new Vector3d(0.0,0.0,0.0)));

		System.out.println("Trans direction: "+transformedDirection);

		newPosCopy = newPosCopy.add(transformedDirection);*/

			Vec3 center = pos.getCenter();
			Vector3d rp = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(center.x, center.y, center.z));

			// Get velocity
			int signal = level.getBestNeighborSignal(pos);

			double vel = signal / 15;

			if (vel == 0.0) {
				return;
			}

			vel = vel * 0.7;

			double x = rp.x + (0.5 * (dir.getStepX() + 1));
			double y = rp.y + (0.5 * (dir.getStepY() + 1));
			double z = rp.z + (0.5 * (dir.getStepZ() + 1));
			double speedX = dir.getStepX() * -vel;
			double speedY = dir.getStepY() * -vel;
			double speedZ = dir.getStepZ() * -vel;

			Vector3d speeds = new Vector3d(speedX, speedY, speedZ);
			speeds = ship.getTransform().getShipToWorldRotation().transform(speeds, new Vector3d(0, 0, 0));
			//speeds.add(ship.getTransform().getShipToWorldRotation().getEulerAnglesZYX(new Vector3d(0.0,0.0,0.0)));
			//speeds = ship.getTransform().getShipToWorldRotation().getEulerAnglesZYX(new Vector3d(0.0,0.0,0.0));
			System.out.println(speeds.toString(NumberFormat.getIntegerInstance()));


			level.addParticle(
					CosmosModParticleTypes.THRUSTED.get(),
					rp.x, rp.y, rp.z,
					speeds.x, speeds.y, speeds.z
					);

			return;
	}

}
