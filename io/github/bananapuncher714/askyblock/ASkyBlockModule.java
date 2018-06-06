package io.github.bananapuncher714.askyblock;

import java.io.File;

import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;

public class ASkyBlockModule extends Module {

	@Override
	public void load( Minimap map, File dataFolder ) {
		map.registerShader( new ASkyBlockShader() );
	}

	@Override
	public void unload() {

	}

}
