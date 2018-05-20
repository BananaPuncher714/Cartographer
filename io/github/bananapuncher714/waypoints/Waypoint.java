package io.github.bananapuncher714.waypoints;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.waypoints.file.WaypointFileManager;

public class Waypoint {
	String name;
	String creatorName;
	UUID waypointUUID;
	UUID creator;
	Type type;
	Location location;
	boolean teleport;
	double discoverRange = 0;
	boolean unToggleable =false;
	Set< UUID > shared = new HashSet< UUID >();
	
	WaypointManager manager;
	
	public Waypoint( WaypointManager manager, Player player, String name, Location location, boolean teleport ) {
		this.creator = player.getUniqueId();
		creatorName = player.getName();
		this.name = name;
		waypointUUID = UUID.randomUUID();
		this.location = location;
		type = manager.getAddon().getDefaultType();
		this.teleport = teleport;
		this.manager = manager;
		unToggleable = manager.getAddon().isDefaultLocked();
		
		manager.addWaypoint( this );
		WaypointFileManager.saveWaypoint( manager.getAddon(), this );
	}
	
	protected Waypoint( WaypointManager manager, FileConfiguration config ) {
		this.manager = manager;
		name = config.getString( "name" );
		creatorName = config.getString( "creator" );
		creator = UUID.fromString( config.getString( "creatorUUID" ) );
		waypointUUID = UUID.fromString( config.getString( "UUID" ) );
		location = WaypointFileManager.getLocationFromString( config.getString( "location" ) );
		if ( config.getString( "type" ) != null ) {
		type = FailSafe.getEnum( Type.class, config.getString( "type" ) );
		if ( type == null ) type = Type.WHITE_CROSS;
		} else {
			type = Type.WHITE_CROSS;
		}
		teleport = config.getBoolean( "teleport" );
		for ( String id : config.getStringList( "shared" ) ) {
			shared.add( UUID.fromString( id ) );
		}
		discoverRange = config.getDouble( "range" );
		if ( config.getBoolean( "public" ) ) {
			manager.addPublicWaypoint( this );
			manager.setStaff( this, config.getBoolean( "staff" ) );
		}
		if ( config.getBoolean( "discoverable" ) ) {
			manager.getDiscoverable().add( waypointUUID );
		}
		unToggleable = config.getBoolean( "un-toggleable" );
	}
	
	public UUID getCreator() {
		return creator;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCreatorName() {
		return creatorName;
	}
	
	public UUID getId() {
		return waypointUUID;
	}
	
	public void setType( Type type ) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setUnToggleable( boolean unToggle ) {
		unToggleable = unToggle;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public boolean teleportable() {
		return teleport;
	}
	
	public void setDiscover( double range ) {
		discoverRange = range;
	}
	
	public double getRange() {
		return discoverRange;
	}
	
	public boolean isDiscoverable() {
		return discoverRange > 0;
	}
	
	public boolean isUnToggleable() {
		return unToggleable;
	}
	
	public void remove() {
		manager.removeWaypoint( this );
		for ( UUID uuid : shared ) {
			if ( manager.getViewer( uuid ) != null ) manager.getViewer( uuid ).getShared().remove( this );
		}
		WaypointFileManager.removeWaypoint( manager.getAddon(), this );
	}
	
	public Set< UUID > getShared() {
		return shared;
	}
	
	@Override
	public boolean equals( Object object ) {
		if ( object instanceof Waypoint ) {
			Waypoint waypoint = ( Waypoint ) object;
			if ( waypoint.getId().equals( waypointUUID ) ) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return waypointUUID.hashCode();
	}
}
