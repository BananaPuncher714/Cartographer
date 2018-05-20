package io.github.bananapuncher714.cartographer.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.map.MapView.Scale;

public class MapZoomEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	Scale scale;
	boolean cancelled = false, zoom;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public MapZoomEvent( Scale scale, boolean zoom ) {
		this.scale = scale;
		this.zoom = zoom;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean arg0 ) {
		cancelled = arg0;
	}

	public Scale getScale() {
		return scale;
	}
	
	public boolean isZooming() {
		return zoom;
	}
	
}
