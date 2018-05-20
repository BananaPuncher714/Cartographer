package io.github.bananapuncher714.cartographer.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import io.github.bananapuncher714.cartographer.api.map.Minimap;

public class PlayerChangeMapEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Minimap originalMap, newMap;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public Minimap getOriginalMap() {
		return originalMap;
	}

	public Minimap getNewMap() {
		return newMap;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public PlayerChangeMapEvent( Player player, Minimap original, Minimap newMap ) {
		super( player );
		originalMap = original;
		this.newMap = newMap;
	}
}
