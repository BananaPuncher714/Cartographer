package io.github.bananapuncher714.mythicmobs;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class MythicMobsAddon extends Module {
	private Type type;
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "mythicmobs-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/mythicmobs/config.yml" ) );
		loadConfig( config );
		
		map.registerCursorSelector( new MythicMobCursorSelector( type ) );
	}

	private void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		type = FailSafe.getEnum( Type.class, config.getString( "cursor-type" ) );
	}
	
	@Override
	public void unload() {

	}

}
