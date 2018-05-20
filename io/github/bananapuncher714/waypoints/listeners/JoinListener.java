package io.github.bananapuncher714.waypoints.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.waypoints.file.WaypointFileManager;

public class JoinListener implements Listener {
	
	@EventHandler
	public void onPlayerJoinEvent( PlayerJoinEvent event ) {
		for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
			if ( map.getModules().containsKey( "waypoints" ) ) {
				WaypointFileManager.loadViewer( ( WaypointAddon ) map.getModules().get( "waypoints" ), event.getPlayer() ).refreshPublic();
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent( PlayerQuitEvent event ) {
		for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
			if ( map.getModules().containsKey( "waypoints" ) ) {
				WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
				WaypointFileManager.saveViewer( addon, addon.getManager().getViewer( event.getPlayer() ) );
			}
		}
	}
}
