package io.github.bananapuncher714.worldborder.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBorderChangeSizeAndCenterEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private WorldBorder border;
	private World world;
	private double d0, d1;
	private Location c0, c1;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public WorldBorderChangeSizeAndCenterEvent( World world, WorldBorder border, double newSize, double oldSize, Location newC, Location oldC ) {
		this.border = border;
		this.world = world;
		d0 = oldSize;
		d1 = newSize;
		c0 = oldC;
		c1 = newC;
	}
	
	public WorldBorder getBorder() {
		return border;
	}
	
	public double getOldSize() {
		return d0;
	}
	
	public double getNewSize() {
		return d1;
	}
	
	public Location getOldLocation() {
		return c0;
	}
	
	public Location getNewLocation() {
		return c1;
	}
	
	public World getWorld() {
		return world;
	}
}
