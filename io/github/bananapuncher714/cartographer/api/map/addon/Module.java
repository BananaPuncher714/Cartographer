package io.github.bananapuncher714.cartographer.api.map.addon;

import java.io.File;

import io.github.bananapuncher714.cartographer.api.map.Minimap;

public abstract class Module {
	public abstract void load( Minimap map, File dataFolder );
	public abstract void unload();
}
