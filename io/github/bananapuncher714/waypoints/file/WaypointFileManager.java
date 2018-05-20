package io.github.bananapuncher714.waypoints.file;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.waypoints.Waypoint;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.waypoints.WaypointManager;
import io.github.bananapuncher714.waypoints.WaypointViewer;
import io.github.bananapuncher714.waypoints.WaypointViewer.DisplayType;

public final class WaypointFileManager {

	public static void saveViewer( WaypointAddon addon, WaypointViewer viewer ) {
		File file = new File( addon.getDataFolder() + "/" + "player" + "/" + viewer.getUUID().toString().substring( 0, 2 ), viewer.getUUID().toString() );
		file.mkdirs();
		if ( file.exists() ) file.delete();
		try {
			file.createNewFile();
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		FileConfiguration save = YamlConfiguration.loadConfiguration( file );
		save.set( "UUID", viewer.getUUID().toString() );
		save.set( "name", viewer.getName() );
		for ( Waypoint waypoint : viewer.getWaypoints().keySet() ) {
			save.set( "waypoints.private." + waypoint.getId(), viewer.getWaypoints().get( waypoint ).name() );
		}
		for ( Waypoint waypoint : viewer.getShared().keySet() ) {
			save.set( "waypoints.shared." + waypoint.getId(), viewer.getShared().get( waypoint ).name() );
		}
		for ( UUID uuid : viewer.getPublic().keySet() ) {
			save.set( "waypoints.public." + uuid.toString(), viewer.getPublic().get( uuid ).name() );
		}
		try {
			save.save( file );
		} catch( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	public static WaypointViewer loadViewer( WaypointAddon addon, Player player ) {
		File file = new File( addon.getDataFolder() + "/" + "player" + "/" + player.getUniqueId().toString().substring( 0, 2 ), player.getUniqueId().toString() );
		if ( file.exists() ) return loadViewer( addon.getManager(), file );
		return addon.getManager().getViewer( player );
	}
	
	public static WaypointViewer loadViewer( WaypointAddon addon, UUID uuid ) {
		File file = new File( addon.getDataFolder() + "/" + "player" + "/" + uuid.toString().substring( 0, 2 ), uuid.toString() );
		if ( file.exists() ) return loadViewer( addon.getManager(), file );
		return addon.getManager().getViewer( uuid );
	}
	
	public static WaypointViewer loadViewer( WaypointManager manager, File file ) {
		FileConfiguration save = YamlConfiguration.loadConfiguration( file );
		return manager.getViewerFromConfig( save );
	}
	
	public static void saveWaypoint( WaypointAddon addon, Waypoint waypoint ) {
		File file = new File( addon.getDataFolder() + "/" + "waypoint" + "/" + waypoint.getId().toString().substring( 0, 2 ), waypoint.getId().toString() );
		file.mkdirs();
		if ( file.exists() ) file.delete();
		try {
			file.createNewFile();
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		FileConfiguration save = YamlConfiguration.loadConfiguration( file );
		save.set( "UUID", waypoint.getId().toString() );
		save.set( "name", waypoint.getName() );
		save.set( "creatorUUID", waypoint.getCreator().toString() );
		save.set( "creator", waypoint.getCreatorName() );
		save.set( "location", getStringFromLocation( waypoint.getLocation() ) );
		save.set( "teleport", waypoint.teleportable() );
		save.set( "type", waypoint.getType().name() );
		save.set( "public", addon.getManager().isPublic( waypoint ) );
		save.set( "staff", addon.getManager().isStaff( waypoint ) );
		save.set( "discoverable", addon.getManager().isDiscoverable( waypoint ) );
		save.set( "range", waypoint.getRange() );
		save.set( "un-toggleable", waypoint.isUnToggleable() );
		ArrayList< String > uuids = new ArrayList< String >();
		for ( UUID waypoints : waypoint.getShared() ) {
			uuids.add( waypoints.toString() );
		}
		save.set( "shared", uuids );
		
		try {
		save.save( file );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	public static Waypoint loadWaypoint( WaypointAddon addon, File file ) {
		FileConfiguration save = YamlConfiguration.loadConfiguration( file );
		return addon.getManager().getFromConfig( save );
	}
	
	public static Waypoint loadWaypoint( WaypointAddon addon, UUID uuid ) {
		File file = new File( addon.getDataFolder() + "/" + "waypoint" + "/" + uuid.toString().substring( 0, 2 ), uuid.toString() );
		if ( !file.exists() ) return null;
		return loadWaypoint( addon, file );
	}
	
	public static void removeWaypoint( WaypointAddon addon, Waypoint waypoint ) {
		File dir = new File( addon.getDataFolder() + "/" + "waypoint" + "/" + waypoint.getId().toString().substring( 0, 2 ) );
		if ( !dir.exists() ) return;
		File file = new File( dir, waypoint.getId().toString() );
		if ( file.exists() ) file.delete();
		if ( dir.list().length == 0 ) dir.delete();
	}
	
	public static Location getLocationFromString( String string ) {
		String dec = string.replaceAll( "%", "." );
		String[] arr = dec.split( "," );
		return new Location( Bukkit.getWorld( arr[ 0 ] ), Double.parseDouble( arr[ 1 ] ), Double.parseDouble( arr[ 2 ] ), Double.parseDouble( arr[ 3 ] ), Float.parseFloat( arr[ 4 ] ), Float.parseFloat( arr[ 5 ] ) );
	}
	
	public static String getStringFromLocation( Location location ) {
		String split = location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
		return split.replaceAll( "\\.", "%" );
	}
	
	public static void refresh( WaypointAddon addon ) {
		File baseDir = new File( addon.getDataFolder() + "/" + "waypoint" + "/" );
		if ( !baseDir.exists() ) return;
		for ( File file : baseDir.listFiles() ) {
			if ( file.isDirectory() ) {
				for ( File wp : file.listFiles() ) {
					if ( !wp.isDirectory() ) {
						Waypoint waypoint = loadWaypoint( addon, wp );
						UUID creator = waypoint.getCreator();
						WaypointViewer viewer = addon.getManager().getViewer( creator );
						if ( viewer == null ) continue;
						if ( !viewer.getWaypoints().containsKey( waypoint ) ) {
							viewer.getWaypoints().put( waypoint, DisplayType.VISIBLE );
						}
						for ( UUID uuid : waypoint.getShared() ) {
							WaypointViewer shared = addon.getManager().getViewer( uuid );
							if ( shared == null ) {
								continue;
							}
							if ( !shared.getShared().containsKey( waypoint ) ) {
								shared.getShared().put( waypoint, DisplayType.VISIBLE );
							}
						}
					}
				}
			}
		}
	}
}
