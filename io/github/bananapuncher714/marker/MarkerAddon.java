package io.github.bananapuncher714.marker;

import java.io.File;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.marker.command.MarkerExecutor;
import io.github.bananapuncher714.marker.command.MarkerTabCompleter;
import io.github.bananapuncher714.marker.file.MarkerSaver;

public class MarkerAddon extends Module {
	private Minimap map;
	
	static {
		Cartographer.getMain().getCommand( "markers" ).setExecutor( new MarkerExecutor() );
		Cartographer.getMain().getCommand( "markers" ).setTabCompleter( new MarkerTabCompleter() );
	}
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		this.map = map;
		map.registerCursorSelector( new MarkerCursorSelector() );
	}

	@Override
	public void unload() {
		for ( MarkerGroup group : UniversalMarkerManager.getUMM().getGroups() ) {
			MarkerSaver.saveMarkerGroup( new File( Cartographer.getMain().getDataFolder() + "/modules/" + "markers" ), group );
		}
	}

}
