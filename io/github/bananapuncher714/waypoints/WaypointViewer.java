package io.github.bananapuncher714.waypoints;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.waypoints.file.WaypointFileManager;

public class WaypointViewer {
	private WaypointAddon addon;
	
	public enum DisplayType {
		HIDDEN( "Hidden" ), VISIBLE( "Visible" ), HIGHLIGHTED( "Highlighted");
		
		String name;
		
		DisplayType( String name ) {
			this.name = name;
		}
		
		public String getDisplayName() {
			return name;
		}
	}
	
	UUID uuid;
	String name;
	HashMap< Waypoint, DisplayType > waypoints = new HashMap< Waypoint, DisplayType >();
	HashMap< Waypoint, DisplayType > shared = new HashMap< Waypoint, DisplayType >();
	HashMap< UUID, DisplayType > pWaypoints = new HashMap< UUID, DisplayType >();
	
	protected WaypointViewer( WaypointAddon addon, Player player ) {
		this.addon = addon;
		uuid = player.getUniqueId();
		name = player.getName();
		WaypointFileManager.saveViewer( addon, this );
	}
	
	protected WaypointViewer( WaypointAddon addon, FileConfiguration config ) {
		this.addon = addon;
		uuid = UUID.fromString( config.getString( "UUID" ) );
		name = config.getString( "name" );
		if ( config.contains( "waypoints.private" ) ) {
			for ( String key : config.getConfigurationSection( "waypoints.private" ).getKeys( false ) ) {
				Waypoint waypoint = addon.getManager().getWaypoint( UUID.fromString( key ) );
				if ( waypoint == null ) continue;
				String dispT = config.getString( "waypoints.private." + key );
				if ( dispT.equalsIgnoreCase( "OBSCURED" ) ) {
					dispT = "VISIBLE";
				}
				waypoints.put( waypoint, DisplayType.valueOf( dispT ) );
			}
		}
		
		if ( config.contains( "waypoints.shared" ) ) {
			for ( String key : config.getConfigurationSection( "waypoints.shared" ).getKeys( false ) ) {
				Waypoint waypoint = addon.getManager().getWaypoint( UUID.fromString( key ) );
				if ( waypoint == null ) continue;
				String dispT = config.getString( "waypoints.shared." + key );
				if ( dispT.equalsIgnoreCase( "OBSCURED" ) ) {
					dispT = "VISIBLE";
				}
				shared.put( waypoint, DisplayType.valueOf( dispT ) );
			}
		}
		
		if ( config.contains( "waypoints.public" ) ) {
			for ( String key : config.getConfigurationSection( "waypoints.public" ).getKeys( false ) ) {
				if ( addon.getManager().getWaypoint( UUID.fromString( key ) ) == null ) {
					continue;
				}
				String dispT = config.getString( "waypoints.public." + key );
				if ( dispT.equalsIgnoreCase( "OBSCURED" ) ) {
					dispT = "VISIBLE";
				}
				pWaypoints.put( UUID.fromString( key ), DisplayType.valueOf( dispT ) );
			}
		}
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public Map< Waypoint, DisplayType > getWaypoints() {
		return waypoints;
	}
	
	public Map< Waypoint, DisplayType > getShared() {
		return shared;
	}
	
	public Map< UUID, DisplayType > getPublic() {
		return pWaypoints;
	}
	
	public void refreshPublic() {
		for ( Waypoint waypoint : addon.getManager().getPublicWaypoints().keySet() ) {
			if ( !pWaypoints.containsKey( waypoint.getId() ) ) {
				pWaypoints.put( waypoint.getId(), DisplayType.HIGHLIGHTED );
			}
		}
	}
}
