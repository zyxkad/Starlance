package net.jcm.vsch.blocks.thruster;

import com.mojang.logging.LogUtils;
import net.jcm.vsch.blocks.custom.template.AbstractThrusterBlock;
import net.jcm.vsch.blocks.custom.template.WrenchableBlock;
import net.jcm.vsch.blocks.entity.NormalThrusterEngine;
import net.jcm.vsch.blocks.entity.VSCHBlockEntities;
import net.jcm.vsch.blocks.entity.template.ParticleBlockEntity;
import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.ship.thruster.ThrusterData;
import net.jcm.vsch.util.NoSourceClipContext;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.Map;

public class AirThrusterBlockEntity extends GenericThrusterBlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();

	// Just called for BE registration
	public AirThrusterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);

	}

	public AirThrusterBlockEntity(String peripheralType, BlockEntityType<?> type, BlockPos pos, BlockState state, ThrusterEngine engine) {
		super(peripheralType, type, pos, state, engine);
	}

	@Override
	protected void spawnParticles(Vector3d pos, Vector3d direction) {
		final Vector3d speed = new Vector3d(direction).mul(this.getCurrentPower());

		speed.mul(0.118);

		int amount = 100;
		for (int i = 0; i < amount; i++) {
			level.addParticle(
					this.getThrusterParticleType(),
					pos.x, pos.y, pos.z,
					speed.x, speed.y, speed.z
			);
		}
	}

}
