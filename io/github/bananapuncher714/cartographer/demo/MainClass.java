package io.github.bananapuncher714.cartographer.demo;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;

public class MainClass {
	
	public void registerShaderWithEveryMap( MapShader shader ) {
		for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
			// Check if you really want to add a territory shader to this map
			map.registerShader( shader );
			map.registerCursorSelector( new TeamCursorSelector() );
			map.registerPixelShader( new ExamplePixelShader() );
		}
	}
}
