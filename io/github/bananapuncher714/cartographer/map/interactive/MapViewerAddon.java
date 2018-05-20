package io.github.bananapuncher714.cartographer.map.interactive;

import java.io.File;

import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.map.interactive.file.MapViewerFileManager;

public class MapViewerAddon extends Module {
	File dataFolder;

	@Override
	public void load( Minimap map, File dataFolder ) {
		MapViewerFileManager.loadMapViewers( dataFolder );
		this.dataFolder = dataFolder;
	}

	@Override
	public void unload() {
		for ( MapViewer viewer : MapViewer.getViewers() ) {
			MapViewerFileManager.saveMapViewer( dataFolder, viewer );
		}
	}

}
