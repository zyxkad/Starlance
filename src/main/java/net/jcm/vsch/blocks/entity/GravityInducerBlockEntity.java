package net.jcm.vsch.blocks.entity;

import net.jcm.vsch.blocks.VSCHBlocks;
import net.jcm.vsch.config.VSCHConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;

public class GravityInducerBlockEntity extends BlockEntity implements ParticleBlockEntity {

    public GravityInducerBlockEntity(BlockPos pos, BlockState blockState) {
        super(VSCHBlockEntities.GRAVITY_INDUCER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tickForce(Level level, BlockPos pos, BlockState state) {
        LoadedServerShip ship = (LoadedServerShip) VSGameUtilsKt.getShipObjectManagingPos(level,pos);
        if(ship == null){return;}
        List<Entity> entities = level.getEntities(null, VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()));
        for(Entity entity : entities){
            if(entity instanceof ServerPlayer player){
                if (player.noPhysics) {
                    continue;
                }

                // I don't know why there isn't a simpler check for this
                if (player.getAbilities().flying) {
                    continue;
                }
            }
            double maxDistance = VSCHConfig.MAGNET_BOOT_DISTANCE.get().doubleValue();
            Vec3 startPos = entity.position(); // Starting position (player's position)
            Vec3 endPos = startPos.add(0, -maxDistance, 0); // End position (straight down)
            HitResult hitResult = level.clip(new ClipContext(
                    startPos,
                    endPos,
                    ClipContext.Block.COLLIDER, // Raycast considers block collision shapes, maybe we don't want this?
                    ClipContext.Fluid.NONE,     // Ignore fluids
                    entity
            ));

            boolean magnetOn = true;

            if (hitResult.getType() == HitResult.Type.BLOCK) {

                double blockY = hitResult.getLocation().y;
                double distanceY = startPos.y - blockY;

                // If magnet is turned off and we are more than 0.1 distance, do nothing
                if ((!magnetOn) && (distanceY > 0.1)) {
                    return;
                }

                //mAtH
                double multiplier = 1.0 - (distanceY / maxDistance);

                double scaledForce = multiplier * -VSCHConfig.MAGNET_BOOT_MAX_FORCE.get().doubleValue() ;

                Vec3 force = new Vec3(0, scaledForce, 0);
                System.out.println("Block: "+force);


                entity.setDeltaMovement(entity.getDeltaMovement().add(force));
                entity.hurtMarked = true;


                //System.out.println("Hit block");


                //System.out.println(slotId);
                //level.addParticle(ParticleTypes.HEART, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    public void tickParticles(Level level, BlockPos pos, BlockState state) {

    }
}
