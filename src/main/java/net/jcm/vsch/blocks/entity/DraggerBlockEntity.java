package net.jcm.vsch.blocks.entity;

import org.joml.Vector3d;
import org.joml.Vector4d;
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

public class DraggerBlockEntity extends BlockEntity {

	public DraggerBlockEntity(BlockPos pos, BlockState state) {
		super(VSCHBlockEntities.DRAGGER_BLOCK_ENTITY.get(), pos, state);
	}

	public void tick(Level level, BlockPos pos, BlockState state, DraggerBlockEntity be) {
		//TODO: add particles depending on where we're thrusting? Might need to go in force inducers
	}

}
