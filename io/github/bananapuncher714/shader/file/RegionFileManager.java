package io.github.bananapuncher714.shader.file;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.shader.MapRegion;

public class RegionFileManager {
	
	public static void saveRegions( File saveFile, Collection< MapRegion > regions ) {
		saveFile.mkdirs();
		for ( MapRegion region : regions ) {
			File save = new File( saveFile + "/" + region.getName() );
			FileConfiguration config = YamlConfiguration.loadConfiguration( save );
			config.set( "name", region.getName() );
			config.set( "width", region.getX() );
			config.set( "height", region.getY() );
			config.set( "color.r", region.getRegionColor().getRed() );
			config.set( "color.g", region.getRegionColor().getGreen() );
			config.set( "color.b", region.getRegionColor().getBlue() );
			config.set( "color.a", region.getRegionColor().getAlpha() );
			config.set( "location", getStringFromLocation( region.getLower() ) );
			List< String > viewer = new ArrayList< String >();
			for ( UUID uuid : region.getViewers() ) {
				viewer.add( uuid.toString() );
			}
			config.set( "viewers", viewer );
			try {
				config.save( save );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}
	
	public static Set< MapRegion > loadRegions( File file ) {
		Set< MapRegion > regions = new HashSet< MapRegion >();
		if ( !file.exists() ) {
			return regions;
		}
		for ( File reg : file.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( reg );
			String name = config.getString( "name" );
			int width = config.getInt( "width" );
			int height = config.getInt( "height" );
			int r = config.getInt( "color.r" );
			int g = config.getInt( "color.g" );
			int b = config.getInt( "color.b" );
			int a = config.getInt( "color.a" );
			Color color = new Color( r, g, b, a );
			Location loc = getLocationFromString( config.getString( "location" ) );
			
			MapRegion region = new MapRegion( loc, width, height, name, color );
			for ( String uuid : config.getStringList( "viewers" ) ) {
				region.addViewer( UUID.fromString( uuid ) );
			}
			regions.add( region );
			reg.delete();
		}
		return regions;
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
