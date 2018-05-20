package io.github.bananapuncher714.griefprevention;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class GriefPreventionAddon extends Module {
	int shade = 50;
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "griefprevention-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/griefprevention/config.yml" ), true );
		loadConfig( config );
	}

	private void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		shade = config.getInt( "shade-percent" );
	}
	
	@Override
	public void unload() {
	}
	
	protected int getShadePercent() {
		return shade;
	}

}
