package io.github.bananapuncher714.cartographer.map.interactive.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.map.interactive.MapViewer;

public class MapViewerFileManager {
	
	public static void saveMapViewer( File saveDir, MapViewer viewer ) {
		UUID uuid = viewer.getUUID();
        saveDir.mkdirs();
		File save = new File( saveDir + "/" + uuid );
        save.delete();
        try {
			save.createNewFile();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
        FileConfiguration config = YamlConfiguration.loadConfiguration( save );
        List< String > saves = new ArrayList< String >();
		for ( ChunkLocation location : viewer.getDiscovered() ) {
			String strLoc = location.getWorld().getName() + "," + location.getX() + "," + location.getZ();
			saves.add( strLoc );
		}
		config.set( "saves", saves );
		try {
			config.save( save );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public static void loadMapViewers( File saveDir ) {
		if ( !saveDir.exists() ) {
			return;
		}
		for ( File file : saveDir.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file );
			MapViewer viewer = MapViewer.getMapViewer( UUID.fromString( file.getName() ) );
			for ( String serLoc : config.getStringList( "saves" ) ) {
				String[] seq = serLoc.split( "," );
				World world = Bukkit.getWorld( seq[ 0 ] );
				int x = Integer.parseInt( seq[ 1 ] );
				int z = Integer.parseInt( seq[ 2 ] );
				ChunkLocation location = new ChunkLocation( world, x, z );
				viewer.discover( location );
			}
			file.delete();
		}
	}

}
