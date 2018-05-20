package io.github.bananapuncher714.worldborder.events;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBorderChangeSizeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private WorldBorder border;
	private World world;
	private double d0, d1;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public WorldBorderChangeSizeEvent( World world, WorldBorder border, double newSize, double oldSize ) {
		this.border = border;
		this.world = world;
		d0 = oldSize;
		d1 = newSize;
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
	
	public World getWorld() {
		return world;
	}
}
