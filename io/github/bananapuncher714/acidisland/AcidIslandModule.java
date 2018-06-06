package io.github.bananapuncher714.acidisland;

import java.io.File;

import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;

public class AcidIslandModule extends Module {

	@Override
	public void load( Minimap map, File dataFolder ) {
		map.registerShader( new AcidIslandShader() );
	}

	@Override
	public void unload() {

	}

}
