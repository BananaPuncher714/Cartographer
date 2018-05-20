package io.github.bananapuncher714.shader;

import java.io.File;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.shader.command.ShaderExecutor;
import io.github.bananapuncher714.shader.command.ShaderTabCompleter;
import io.github.bananapuncher714.shader.file.RegionFileManager;

public class ShaderAddon extends Module {
	Minimap map;
	
	static {
		Cartographer.getMain().getCommand( "mapshaders" ).setExecutor( new ShaderExecutor() );
		Cartographer.getMain().getCommand( "mapshaders" ).setTabCompleter( new ShaderTabCompleter() );
	}
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		this.map = map;
		map.registerPixelShader( new RegionPixelShader() );
	}

	@Override
	public void unload() {
		RegionFileManager.saveRegions( new File( Cartographer.getMain().getDataFolder() + "/modules/" + "shaders" ), RegionManager.getInstance().getRegions() );
	}
}