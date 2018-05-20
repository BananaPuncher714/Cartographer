package io.github.bananapuncher714.waypoints.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.waypoints.Waypoint;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.waypoints.WaypointViewer;

public class PlayerMoveListener implements Listener {
	Map< UUID, Location > playerLoc = new HashMap< UUID, Location >();
	
	@EventHandler
	public void onPlayerMoveEvent( PlayerMoveEvent event ) {
		Player player = event.getPlayer();
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
			return;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			return;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		if ( !playerLoc.containsKey( player.getUniqueId() ) ) playerLoc.put( player.getUniqueId(), player.getLocation() );
		Location location = playerLoc.get( player.getUniqueId() );
		Location curLoc = player.getLocation();
		if ( !curLoc.getWorld().getUID().equals( map.getWorld().getUID() ) ) {
			return;
		}
		if ( !( location.getX() == curLoc.getX() || location.getZ() != curLoc.getZ() || location.getY() != curLoc.getZ() ) ) {
			return;
		}
		WaypointViewer viewer = addon.getManager().getViewer( player );
		for ( Iterator< UUID > it = addon.getManager().getDiscoverable().iterator(); it.hasNext(); ) {
			Waypoint waypoint = addon.getManager().getWaypoint( it.next() );
			if ( waypoint == null ) {
				it.remove();
				continue;
			}
			if ( viewer.getWaypoints().containsKey( waypoint ) ) {
				continue;
			}
			if ( viewer.getPublic().containsKey( waypoint.getId() ) ) {
				continue;
			}
			double range = waypoint.getRange();
			if ( range <= 0 ) {
				continue;
			}
			if ( curLoc.distanceSquared( waypoint.getLocation() ) <= range * range ) {
				viewer.getPublic().put( waypoint.getId(), addon.getDefaultDiscoverDisplay() );
				if ( addon.showDiscoverMessage() ) CLogger.msg( player, "header", CLogger.parse( player, "waypoints.name" ), CLogger.parse( player, "waypoints.notification.discovered-waypoint", waypoint.getName() ) );
			}
		}
	}
	
}
