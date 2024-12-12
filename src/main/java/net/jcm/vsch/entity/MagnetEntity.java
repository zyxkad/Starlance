package net.jcm.vsch.entity;

import net.jcm.vsch.blocks.VSCHBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class MagnetEntity extends Entity {
    public MagnetEntity(EntityType<? extends MagnetEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true; // Prevents collision with blocks
        this.setInvisible(true);
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return false;
    }

    @Override
    public void tick() {
        Level lv = level();
        if (lv instanceof ServerLevel) {
            System.out.println(this.position());
            BlockPos pos = BlockPos.containing(this.position().x, this.position().y, this.position().z);

            // Idk the difference but ig ill do both
            Ship ship = VSGameUtilsKt.getShipManagingPos(lv, pos);
            Ship ship2 = VSGameUtilsKt.getShipObjectManagingPos(lv, pos);
            if (ship != null) {
                Vector3d new_pos = ship.getTransform().getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(this.position()));
                pos = BlockPos.containing(new_pos.x, new_pos.y, new_pos.z);

            } else if (ship2 != null) {
                Vector3d new_pos = ship.getTransform().getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(this.position()));
                pos = BlockPos.containing(new_pos.x, new_pos.y, new_pos.z);
            }


            BlockState block = lv.getBlockState(pos);

            System.out.println(block);
            if (!block.is(VSCHBlocks.MAGNET_BLOCK.get())) {
                this.kill();
            }
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {}
}
