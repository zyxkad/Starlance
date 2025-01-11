package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.VSCHBlocks;
import net.jcm.vsch.blocks.custom.template.AbstractThrusterBlock;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.ship.ThrusterData.ThrusterMode;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractThrusterBlockEntity extends BlockEntity implements ParticleBlockEntity {
	private final ThrusterData thrusterData;
	private float power = 0;

	protected AbstractThrusterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		this.thrusterData = new ThrusterData(
			VectorConversionsMCKt.toJOMLD(state.getValue(DirectionalBlock.FACING).getNormal()),
			0,
			state.getValue(AbstractThrusterBlock.MODE));
	}

	public abstract float getMaxThrottle();

	public float getThrottle() {
		//return state.getValue(TournamentProperties.TIER) * signal * mult.get().floatValue();
		return getPower() * getMaxThrottle();
	}

	/**
	 * @return thruster power between 0.0~1.0
	 */
	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		setPower(power, true);
	}

	protected void setPower(float power, boolean update) {
		float newPower = Math.min(Math.max(power, 0), 1);
		if (this.power == newPower) {
			return;
		}
		this.power = newPower;
		if (update) {
			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 11);
		}
		this.thrusterData.throttle = getThrottle();
	}

	private static float getPowerByRedstone(Level level, BlockPos pos) {
		return (float)(level.getBestNeighborSignal(pos)) / 15;
	}

	@Override
	public void load(CompoundTag data) {
		this.setPower(data.getFloat("Power"), false);
		super.load(data);
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);
		data.putFloat("Power", this.getPower());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag data = super.getUpdateTag();
		data.putFloat("Power", this.getPower());
		return data;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void tickForce(Level level, BlockPos pos, BlockState state) {
		VSCHForceInducedShips ships = VSCHForceInducedShips.get(level, pos);
		if (ships == null) {
			return;
		}
		float newPower = getPowerByRedstone(level, pos);
		setPower(newPower);
		boolean isLit = state.getValue(AbstractThrusterBlock.LIT);
		if (getPower() > 0) {
			if (!isLit) { //If we aren't lit
				level.setBlockAndUpdate(pos, state.setValue(AbstractThrusterBlock.LIT, true));
			}
		} else {
			if (isLit) { //If we ARE lit
				level.setBlockAndUpdate(pos, state.setValue(AbstractThrusterBlock.LIT, false));
			}
		}
		if (ships.getThrusterAtPos(pos) == null) { 
			ships.addThruster(pos, thrusterData);
		}
	}

	protected ParticleOptions getThrusterParticleType() {
		return CosmosModParticleTypes.THRUSTED.get();
	}

	protected ParticleOptions getThrusterSmokeParticleType() {
		return CosmosModParticleTypes.THRUST_SMOKE.get();
	}

	@Override
	public void tickParticles(Level level, BlockPos pos, BlockState state) {
		Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
		// If we aren't on a ship, then we skip
		if (ship == null) {
			return;
		}

		// If we are unpowered, do no particles
		if (getPower() == 0.0) {
			return;
		}

		// BlockPos is always at the corner, getCenter gives us a Vec3 thats centered YAY
		Vec3 center = pos.getCenter();
		// Transform that shipyard pos into a world pos
		Vector3d worldPos = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(center.x, center.y, center.z));

		// Get blockstate direction, NORTH, SOUTH, UP, DOWN, etc
		Direction dir = state.getValue(DirectionalBlock.FACING);
		Vector3d direction = ship.getTransform().getShipToWorldRotation().transform(new Vector3d(dir.getStepX(), dir.getStepY(), dir.getStepZ()));

		spawnParticles(worldPos, direction);
	}

	protected void spawnParticles(Vector3d pos, Vector3d direction) {
		// Offset the XYZ by a little bit so its at the end of the thruster block
		double x = pos.x - direction.x;
		double y = pos.y - direction.y;
		double z = pos.z - direction.z;

		Vector3d speed = new Vector3d(direction).mul(-getPower());

		speed.mul(0.6);

		// All that for one particle per tick...
		level.addParticle(
				getThrusterParticleType(),
				x, y, z,
				speed.x, speed.y, speed.z
				);

		speed.mul(1.06);

		// Ok ok, two particles per tick
		level.addParticle(
				getThrusterSmokeParticleType(),
				x, y, z,
				speed.x, speed.y, speed.z
				);
	}
}
