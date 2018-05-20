package io.github.bananapuncher714.cartographer.dependencies;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;

import com.wimbli.WorldBorder.WorldFileData;

public class WorldBorderAPI {
	private static Map< UUID, WorldFileData > data = new HashMap< UUID, WorldFileData >();
	
	public static boolean isLoaded( World world, int x, int z ) {
		WorldFileData wdf;
		if ( data.containsKey( world.getUID() ) ) {
			wdf = data.get( world.getUID() );
		} else {
			wdf = WorldFileData.create( world, null );
			data.put( world.getUID(), wdf );
		}
		
		return wdf.doesChunkExist( x, z );
	}
}
