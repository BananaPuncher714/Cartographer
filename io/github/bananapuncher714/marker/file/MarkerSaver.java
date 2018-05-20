package io.github.bananapuncher714.marker.file;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.marker.MarkerGroup;

public class MarkerSaver {
	
	public static void saveMarkerGroup( File saveDirectory, MarkerGroup group ) {
		saveDirectory.mkdirs();
		File save = new File( saveDirectory + "/" + group.getId() );
		if ( save.exists() ) {
			save.delete();
		}
		try {
			save.createNewFile();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration( save );
		
		Map< String, RealWorldCursor > cursors = group.getRawCursors();
		for ( String name : cursors.keySet() ) {
			RealWorldCursor cursor = cursors.get( name );
			config.set( name + ".location", getStringFromLocation( cursor.getLocation() ) );
			config.set( name + ".type", cursor.getType().name() );
			config.set( name + ".highlighted", !cursor.hideWhenOOB() );
		}
		
		try {
			config.save( save );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public static MarkerGroup getMarkerGroup( File saveDirectory, String id ) {
		if ( !saveDirectory.exists() ) {
			return null;
		}
		File save = new File( saveDirectory + "/" + id );
		if ( !save.exists() ) {
			return null;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration( save );
		MarkerGroup group = new MarkerGroup( id );
		for ( String key : config.getKeys( false ) ) {
			Location cursorLoc = getLocationFromString( config.getString( key + ".location" ) );
			Type type = FailSafe.getEnum( Type.class, config.getString( key + ".type" ) );
			boolean hwoob = config.getBoolean( key + ".highlighted" );
			RealWorldCursor cursor = new RealWorldCursor( cursorLoc, type, !hwoob );
			group.addMarker( key, cursor );
		}
		return group;
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
	
}
