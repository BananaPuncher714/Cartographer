/**
 * Handles simple tests like getting the highest distance at a location and getting the depth of water.
 * 
 * @author BananaPuncher714
 */
package io.github.bananapuncher714.cartographer.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public final class BlockUtil {
	
	/**
	 * Gets how deep the water is in blocks from a certain location.
	 * 
	 * @param l
	 * The location to test at
	 * @return
	 * The amount of water blocks below the location including the location itself
	 */
	public static int getWaterDepth( Location l ) {
		Block b = l.getBlock();
		int depth = 1;
		if ( b.getType() != Material.WATER && b.getType() != Material.STATIONARY_WATER ) return -1;
		Block lower = b.getRelative( BlockFace.DOWN );
		while ( lower.getLocation().getBlockY() > 1 && ( lower.getType() == Material.WATER || lower.getType() == Material.STATIONARY_WATER ) ) {
			depth++;
			lower = lower.getRelative( BlockFace.DOWN );
		}
		return depth;
	}
	
	/**
	 * Gets the highest block at a certain location
	 * 
	 * @param l
	 * The location to get a block
	 * @return
	 * The highest block including transparent objects.
	 */
	public static Block getHighestBlockAt( Location l ) {
		return getHighestBlockAt( l, new HashSet< Material >(), -1 );
	}
	
	/**
	 * Gets the highest block at a certain location while skipping certain materials.
	 * 
	 * @param loc
	 * The location to get a block
	 * @param skip
	 * The set of materials to skip
	 * @param height
	 * The height at which to start looking for blocks
	 * @return
	 * The highest block at a certain location without being in the set of transparent blocks.
	 */
	public static Block getHighestBlockAt( Location loc, Set< Material > skip, int height ) {
		skip.add( Material.AIR );
		Location l = loc.clone();
		if ( height > 0 ) {
			l.setY( height );
		} else {
			l.setY( loc.getWorld().getMaxHeight() );
		}
		Block b = l.getBlock();
		if ( l.getBlockY() <= 1 ) {
			return b;
		}
		Block upper = b.getRelative( BlockFace.DOWN );
		while ( skip.contains( upper.getType() ) && upper.getLocation().getBlockY() > 1 ) {
			upper = upper.getRelative( BlockFace.DOWN );
		}
		return upper;
	}
	
	public static Block getNextHighestBlockAt( Location location, Set< Material > skip, int height ) {
		skip.add( Material.AIR );
		Location loc = location.clone();
		if ( height > 0 && height <= loc.getWorld().getMaxHeight() ) {
			loc.setY( height );
		} else {
			loc.setY( 1 );
		}
		Block b = loc.getBlock();
		Block upper = b.getRelative( BlockFace.UP );
		while ( skip.contains( upper.getType() ) && upper.getLocation().getY() < loc.getWorld().getMaxHeight() ) {
			upper = upper.getRelative( BlockFace.UP );
		}
		return upper;
	}
}
