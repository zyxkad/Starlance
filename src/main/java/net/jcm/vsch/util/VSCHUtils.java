package net.jcm.vsch.util;

import org.joml.primitives.AABBd;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.util.AABBdUtilKt;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;


public class VSCHUtils {
	
	/**
	 * Converts a VS dimension id string of <code>'minecraft:dimension:namespace:dimension_name'</code> to a normal
	 * dimension id string of <code>'namespace:dimension_name'</code>
	 * @param dimensionString The VS format dimension id string
	 * @return The converted dimension id string
	 * @author Brickyboy
	 * @see #dimToVSDim(String)
	 */
	public static String VSDimToDim(String VSdimensionString) {
    	// Transform VS's 'minecraft:dimension:namespace:dimension_name' into 'namespace:dimension_name'
    	String[] dimSplit = VSdimensionString.split(":");
    	String dimFixed = dimSplit[2] + ":" + dimSplit[3];
    	return dimFixed;
	}
	
	/**
	 * Converts a normal dimension id string of <code>'namespace:dimension_name'</code> to a 
	 * VS dimension id string <code>'minecraft:dimension:namespace:dimension_name'</code>
	 * @param dimensionString The normal format dimension id string
	 * @return The converted VS dimension id string
	 * @author Brickyboy
	 * @see #VSDimToDim(String)
	 */
	public static String dimToVSDim(String dimensionString) {
        return "minecraft:dimension:" + dimensionString;
	}
	
	/**
	 * Takes in a {@link org.valkyrienskies.core.api.ships.properties.ShipTransform ShipTransform}
	 * and its ship {@link org.joml.primitives.AABBic AABBic} (its <b>shipyard</b> {@link org.joml.primitives.AABBic AABBic}) 
	 * and returns a world-based {@link org.joml.primitives.AABBd AABBd} using the transform
	 * <br></br>
	 * Basically the same as {@link org.valkyrienskies.core.api.ships.Ship#getWorldAABB() Ship#getWorldAABB()}
	 * but can take in a specified transform and ship AABBic
	 * @param transform The ship transform to use
	 * @param shipAABB The <b>shipyard</b> AABBic of the ship
	 * @author Brickyboy
	 * @return The world based AABBd
	 */
	public static AABBd transformToAABBd(ShipTransform transform, AABBic shipAABB) {
		// From AABBic (Int, constant) to AABBd (Double)
		AABBd shipAABBd = AABBdUtilKt.toAABBd(shipAABB, new AABBd());
		// Turn the shipyard AABBd to the world AABBd using the transform
        AABBd worldAABB = shipAABBd.transform(transform.getShipToWorld());
        return worldAABB;
	}
	
	/**
	 * Converts a VS dimension id string and returns a {@link net.minecraft.server.level.ServerLevel ServerLevel} in that dimension.
	 * @param server The {@link net.minecraft.server.MinecraftServer MinecraftServer} object used to get the level
 	 * @param dimensionId The VS dimension id string for the dimension level you want
	 * @return A {@link net.minecraft.server.level.ServerLevel ServerLevel} object in the VS dimension given
	 * @see #dimToVSDim(String)
	 * @todo Add error handling for if string isn't an existing dimension
	 */
	public static ServerLevel VSDimToLevel(MinecraftServer server, String dimensionId) {
		// Split 'minecraft:dimension:namespace:dimension_name' into [minecraft, dimension, namespace, dimension_name]
	    String[] splitText = dimensionId.split(":");
	    
	    // Make a resource location from 'namespace' and 'dimension_name'
	    ResourceLocation rl = new ResourceLocation(splitText[splitText.length - 2], splitText[splitText.length - 1]);
	    
	    // Turn that RL into a Registry Key and use that to get a Level
	    return server.getLevel(ResourceKey.create(Registries.DIMENSION, rl));
	}
}
