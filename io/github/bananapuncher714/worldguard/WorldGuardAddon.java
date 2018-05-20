package io.github.bananapuncher714.worldguard;

import java.awt.Color;
import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class WorldGuardAddon extends Module {
	private Color regionColor;

	@Override
	public void load( Minimap map, File dataFolder) {
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "worldguard-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/worldguard/config.yml" ), true );
		loadConfig( config );
		
		map.registerShader( new WorldGuardLandShader( regionColor ) );
	}

	@Override
	public void unload() {
	}
	
	private void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		int r = config.getInt( "color.r" );
		int g = config.getInt( "color.g" );
		int b = config.getInt( "color.b" );
		regionColor = new Color( r, g, b );
	}
}
