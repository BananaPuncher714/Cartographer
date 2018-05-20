package io.github.bananapuncher714.factionsuuid;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.factionsuuid.listeners.LandClaimListener;
import io.github.bananapuncher714.factionsuuid.listeners.LandUnclaimListener;

public class FactionsUUIDAddon extends Module {
	private File dataFolder;
	
	public static int PERCENT;
	public static boolean OOB, HOOB;
	public static String home, admin, moderator, normal;
	
	static {
		Bukkit.getPluginManager().registerEvents( new LandClaimListener(), Cartographer.getMain() );
		Bukkit.getPluginManager().registerEvents( new LandUnclaimListener(), Cartographer.getMain() );
	}
	
	@Override
	public void load( Minimap map, File dataFolder) {
		this.dataFolder = dataFolder;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "factionsuuid-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/factionsuuid/config.yml" ), true );
		map.registerShader( new FactionTerritoryShader() );
		map.registerCursorSelector( new FactionCursorSelector() );

		loadConfig( config );
	}

	@Override
	public void unload() {
		
	}
	
	private void loadConfig( File file ) {
		FileConfiguration c = YamlConfiguration.loadConfiguration( file );
		PERCENT = c.getInt( "shade-percent" );
		OOB = c.getBoolean( "members-visible-when-not-on-map" );
		HOOB = c.getBoolean( "house-visible-when-not-on-map" );
		home = c.getString( "home-cursor-type" ).toUpperCase();
		if ( Type.valueOf( home ) == null ) home = "WHITE_CROSS";
		admin = c.getString( "admin-cursor-type" ).toUpperCase();
		if ( Type.valueOf( admin ) == null ) admin = "RED_POINTER";
		moderator = c.getString( "moderator-cursor-type" ).toUpperCase();
		if ( Type.valueOf( moderator ) == null ) moderator = "BLUE_POINTER";
		normal = c.getString( "normal-cursor-type" ).toUpperCase();
		if ( Type.valueOf( normal ) == null ) normal = "GREEN_POINTER";
	}

}
