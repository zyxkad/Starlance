package net.jcm.vsch.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;

import java.util.Map;
import java.util.Set;

/**@deprecated sus vscore reference*/
@Deprecated
@Mixin(ShipObjectServerWorld.class)
public interface ServerShipObjectWorldAccessor {
    @Accessor("shipIdToConstraints")
    Map<Long, Set<Integer>> getShipIdToConstraints();

    @Accessor("constraints")
    Map<Integer, VSConstraint> getConstraints();
}
