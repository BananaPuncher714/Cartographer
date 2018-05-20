package io.github.bananapuncher714.cartographer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.events.PlayerChangeMapEvent;
import io.github.bananapuncher714.cartographer.api.map.Minimap;

/**
 * Handle switching maps automatically when a player moves;
 * Keep the current map only if:
 * 1. They're still on it
 * 2. It's in the same world as them and no other maps match
 * 
 * @author BananaPuncher714 
 */
public class PlayerChangeMapListener implements Listener {

	@EventHandler
	public void onPlayerMoveEvent( PlayerMoveEvent event ) {
		Player player = event.getPlayer();
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map != null && map.getProvider().onMap( player.getLocation() ) && map.getHeight() < 0 ) {
			return;
		}
		Location pLoc = player.getLocation();
		Minimap[] maps = new Minimap[ MapManager.getInstance().getMinimaps().size() ];
		int index = 0;
		for ( Minimap nMap : MapManager.getInstance().getMinimaps().values() ) {
			if ( nMap.getProvider().onMap( pLoc ) ) {
				if ( nMap.getMapHeight() < 0 ) {
					PlayerChangeMapEvent chMapEvent = new PlayerChangeMapEvent( player, map, nMap );
					Bukkit.getPluginManager().callEvent( chMapEvent );
					MapManager.getInstance().setPlayerMap( player, nMap );
					return;
				}

				maps[ index++ ] = nMap;
			}
		}
		Minimap selected = null;
		double currDistance = 10000;
		double y = pLoc.getY();
		for ( Minimap depthMap : maps ) {
			if ( depthMap == null ) {
				continue;
			}
			if ( selected == null || ( depthMap.getMapHeight() - y < currDistance && depthMap.getMapHeight() - y >= 0 ) ) {
				if ( depthMap.getMapHeight() - y >= 0 ) {
					currDistance = depthMap.getMapHeight() - y;
				}
				selected = depthMap;
			}
		}
		if ( selected != null && ( map == null || !selected.getUID().equals( map.getUID() ) ) ) {
			PlayerChangeMapEvent chMapEvent = new PlayerChangeMapEvent( player, map, selected );
			Bukkit.getPluginManager().callEvent( chMapEvent );
			MapManager.getInstance().setPlayerMap( player, selected );
			return;
		}
		if ( map != null && !map.getWorld().getUID().equals( player.getWorld().getUID() ) ) {
			MapManager.getInstance().setPlayerMap( player, null );
		}
	}
}
