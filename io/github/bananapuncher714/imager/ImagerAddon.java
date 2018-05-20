package io.github.bananapuncher714.imager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class ImagerAddon extends Module {
	Minimap map;
	File dataFolder;
	private final Set< UUID > enabled = new HashSet< UUID >();
	private boolean defOn = false;
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		this.map = map;
		this.dataFolder = dataFolder;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "playerheads-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/playerheads/config.yml" ), true );
		loadConfig( config );
		map.registerPixelShader( new ImagePixelShader( this ) );
	}

	@Override
	public void unload() {
		
	}
	
	public void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		defOn = config.getBoolean( "default-on" );
	}
	
	public Minimap getMap() {
		return map;
	}
	
	public File getDataFolder() {
		return dataFolder;
	}
	
	public boolean isOnByDefault() {
		return defOn;
	}
	
	public boolean isEnabledFor( Player player ) {
		return enabled.contains( player.getUniqueId() );
	}
	
	public void toggle( Player player ) {
		if ( enabled.contains( player.getUniqueId() ) ) {
			enabled.remove( player.getUniqueId() );
		} else {
			enabled.add( player.getUniqueId() );
		}
	}
}
