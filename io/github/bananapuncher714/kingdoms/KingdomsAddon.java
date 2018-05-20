package io.github.bananapuncher714.kingdoms;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.kingdoms.listeners.LandClaimListener;
import io.github.bananapuncher714.kingdoms.listeners.LandUnclaimListener;

public class KingdomsAddon extends Module {
	private boolean unload = false;
	private Type player, home, king, mod;
	private double percent;
	private Minimap map;
	
	static {
		Bukkit.getPluginManager().registerEvents( new LandClaimListener(), Cartographer.getMain() );
		Bukkit.getPluginManager().registerEvents( new LandUnclaimListener(), Cartographer.getMain() );
	}
	
	@Override
	public void load( Minimap map, File dataFolder) {
		this.map = map;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "kingdoms-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/kingdoms/config.yml" ) );
		loadConfig( config );
		
		map.registerShader( new KingdomLandShader( this ) );
		map.registerCursorSelector( new KingdomCursorSelector( this ) );
	}

	@Override
	public void unload() {
		unload = true;
	}
	
	public boolean unloaded() {
		return unload;
	}
	
	public Type getMemberCursorType() {
		return player;
	}
	
	public Type getKingCursorType() {
		return king;
	}
	
	public Type getModCursorType() {
		return mod;
	}
	
	public Type getHomeCursorType() {
		return home;
	}
	
	public double getShadingPercent() {
		return percent;
	}
	
	public Minimap getMap() {
		return map;
	}
	
	private void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		player = FailSafe.getEnum( Type.class, config.getString( "member-cursor" ) );
		if ( player == null ) player = Type.GREEN_POINTER;
		king = FailSafe.getEnum( Type.class, config.getString( "king-cursor" ) );
		if ( king == null ) king = Type.RED_POINTER;
		mod = FailSafe.getEnum( Type.class, config.getString( "mod-cursor" ) );
		if ( mod == null ) mod = Type.BLUE_POINTER;
		home = FailSafe.getEnum( Type.class, config.getString( "kingdom-home-cursor" ) );
		if ( home == null ) home = Type.WHITE_CROSS;
		percent = config.getDouble( "shading-percent" );
	}

}
