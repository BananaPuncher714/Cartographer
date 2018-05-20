package io.github.bananapuncher714.shader;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.shader.file.RegionFileManager;

public class RegionManager {
	private static RegionManager instance;
	private Map< String, MapRegion > regions = new HashMap< String, MapRegion >();

	private RegionManager() {
		for ( MapRegion region : RegionFileManager.loadRegions( new File( Cartographer.getMain().getDataFolder() + "/modules/" + "shaders" ) ) ) {
			addRegion( region );
		}
	}
	
	public MapRegion getRegion( String name ) {
		return regions.get( name );
	}
	
	public void addRegion( MapRegion region ) {
		regions.put( region.getName(), region );
	}
	
	public MapRegion removeRegion( String name ) {
		return regions.remove( name );
	}
	
	public Collection< MapRegion > getRegions() {
		return regions.values();
	}
	
	public Collection< String > getNames() {
		return regions.keySet();
	}
	
	public static RegionManager getInstance() {
		if ( instance == null ) {
			instance = new RegionManager();
		}
		return instance;
	}
}
