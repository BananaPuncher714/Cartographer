package io.github.bananapuncher714.waypoints;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.waypoints.file.WaypointFileManager;

public class WaypointManager {
	private Map< UUID, Waypoint > waypoints = new HashMap< UUID, Waypoint >();
	private HashMap< UUID, WaypointViewer > viewers = new HashMap< UUID, WaypointViewer >();
	Map< Waypoint, Boolean > pWaypoints = new HashMap< Waypoint, Boolean >();
	Set< UUID > discoverable = new HashSet< UUID >();
	WaypointAddon addon;
	
	public WaypointManager( WaypointAddon addon ) {
		this.addon = addon;
	}
	
	public WaypointAddon getAddon() {
		return addon;
	}
	
	public void addPublicWaypoint( Waypoint waypoint ) {
		pWaypoints.put( waypoint, false );
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			getViewer( player ).refreshPublic();
		}
	}
	
	public void removePublicWaypoint( Waypoint waypoint ) {
		if ( pWaypoints.containsKey( waypoint ) ) pWaypoints.remove( waypoint );
		if ( discoverable.contains( waypoint.getId() ) ) discoverable.remove( waypoint.getId() );
	}
	
	public boolean isPublic( Waypoint waypoint ) {
		return pWaypoints.containsKey( waypoint );
	}
	
	public void setStaff( Waypoint waypoint, boolean staff ) {
		pWaypoints.put( waypoint, staff );
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			getViewer( player ).refreshPublic();
		}
	}
	
	public boolean isStaff( Waypoint waypoint ) {
		if ( pWaypoints.containsKey( waypoint ) ) {
			return pWaypoints.get( waypoint );
		}
		return false;
	}
	
	public Map< Waypoint, Boolean > getPublicWaypoints() {
		return pWaypoints;
	}
	
	public Set< UUID > getDiscoverable() {
		return discoverable;
	}
	
	public boolean isDiscoverable( Waypoint waypoint ) {
		return discoverable.contains( waypoint.getId() );
	}
	
	public Waypoint getWaypoint( UUID uuid ) {
		if ( waypoints.containsKey( uuid ) ) return waypoints.get( uuid );
		Waypoint waypoint = WaypointFileManager.loadWaypoint( addon, uuid );
		return waypoint;
	}
	
	public Waypoint getFromConfig( FileConfiguration config ) {
		Waypoint waypoint = new Waypoint( this, config );
		waypoints.put( waypoint.getId(), waypoint );
		return waypoint;
	}
	
	public void saveWaypoints() {
		for ( Waypoint waypoint : waypoints.values() ) {
			WaypointFileManager.saveWaypoint( addon, waypoint );
		}
	}
	
	public void removeWaypoint( Waypoint waypoint ) {
		removePublicWaypoint( waypoint );
		waypoints.remove( waypoint );
	}
	
	public void addWaypoint( Waypoint waypoint ) {
		waypoints.put( waypoint.getId(), waypoint );
	}
	
	public Collection< WaypointViewer > getViewers() {
		return viewers.values();
	}
	
	public WaypointViewer getViewer( Player player ) {
		if ( viewers.containsKey( player.getUniqueId() ) ) return viewers.get( player.getUniqueId() );
		WaypointViewer viewer = new WaypointViewer( addon, player );
		viewers.put( player.getUniqueId(), viewer );
		return viewer;
	}
	
	public WaypointViewer getViewer( UUID uuid ) {
		if ( viewers.containsKey( uuid ) ) {
			return viewers.get( uuid );
		}
		WaypointViewer viewer = WaypointFileManager.loadViewer( addon, uuid );
		return viewer;
	}
	
	public WaypointViewer getViewerFromConfig( FileConfiguration config ) {
		WaypointViewer viewer = new WaypointViewer( addon, config );
		viewers.put( viewer.getUUID(), viewer );
		return viewer;
	}
	
	public void saveViewers() {
		for ( WaypointViewer viewer : viewers.values() ) {
			WaypointFileManager.saveViewer( addon, viewer );
		}
	}
}