package io.github.bananapuncher714.worldborder.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBorderSwitchCenterEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private WorldBorder border;
	private World world;
	private Location nl, ol;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public WorldBorderSwitchCenterEvent( World world, WorldBorder border, Location newCenter, Location oldCenter ) {
		this.border = border;
		this.world = world;
		nl = newCenter;
		ol = oldCenter;
	}
	
	public WorldBorder getBorder() {
		return border;
	}
	
	public Location getNewLocation() {
		return nl;
	}
	
	public Location getOldLocation() {
		return ol;
	}
	
	public World getWorld() {
		return world;
	}
}
