package io.github.bananapuncher714.cartographer.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.map.MapView.Scale;

public class CartographerInitializeMapEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	Scale scale;

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public CartographerInitializeMapEvent( Scale scale ) {
		this.scale = scale;
	}
	
	public void setScale( Scale scale ) {
		this.scale = scale;
	}
	
	public Scale getScale() {
		return scale;
	}

}
