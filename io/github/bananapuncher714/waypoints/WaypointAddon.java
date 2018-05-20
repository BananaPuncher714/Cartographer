package io.github.bananapuncher714.waypoints;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.inventory.util.CustomHolder;
import io.github.bananapuncher714.waypoints.WaypointViewer.DisplayType;
import io.github.bananapuncher714.waypoints.command.WaypointExecutor;
import io.github.bananapuncher714.waypoints.command.WaypointTabCompleter;
import io.github.bananapuncher714.waypoints.file.WaypointFileManager;
import io.github.bananapuncher714.waypoints.inv.WaypointBananaManager;
import io.github.bananapuncher714.waypoints.listeners.JoinListener;
import io.github.bananapuncher714.waypoints.listeners.PlayerMoveListener;

public class WaypointAddon extends Module {
	File dataFolder;
	private int maxWaypoints;
	private double range;
	private boolean showDiscover;
	private Type defaultType;
	private boolean locked = false;
	private DisplayType visibility;
	private DisplayType discoverDisplay;
	private WaypointManager manager;
	private WaypointTextSelector selector;
	private MapFont font;
	
	static {
		Cartographer.getMain().getCommand( "waypoints" ).setExecutor( new WaypointExecutor() );
		Cartographer.getMain().getCommand( "waypoints" ).setTabCompleter( new WaypointTabCompleter() );
		
		Bukkit.getPluginManager().registerEvents( new WaypointBananaManager(), Cartographer.getMain() );
		
		Bukkit.getPluginManager().registerEvents( new JoinListener(), Cartographer.getMain() );
		Bukkit.getPluginManager().registerEvents( new PlayerMoveListener(), Cartographer.getMain() );
	}
	
	@Override
	public void load( Minimap map, File data ) {
		dataFolder = data;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "waypoints-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/waypoints/config.yml" ), true );
		loadConfig( config );
		manager = new WaypointManager( this );
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			WaypointFileManager.loadViewer( this, player ).refreshPublic();
		}
		
		map.registerCursorSelector( new WaypointSelector( this ) );
		
		selector = new WaypointTextSelector( this );
		map.registerTextSelector( selector );
	}
	
	@Override
	public void unload() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			if ( player.getOpenInventory().getTopInventory().getHolder() instanceof CustomHolder ) player.closeInventory();
		}
		manager.saveViewers();
		manager.saveWaypoints();
	}
	
	private void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		maxWaypoints = config.getInt( "maximum-waypoints" );
		range = config.getDouble( "default.discover-range" );
		showDiscover = config.getBoolean( "show-discover-message" );
		discoverDisplay = FailSafe.getEnum( DisplayType.class, config.getString( "default.discover-visibility" ) );
		defaultType = FailSafe.getEnum( Type.class, config.getString( "default.icon" ) );
		visibility = FailSafe.getEnum( DisplayType.class, "default.visibility" );
		locked = config.getBoolean( "default.locked" );
		String font = config.getString( "hover-font" );
		if ( Cartographer.getMain().getFont( font ) == null ) {
			this.font = MinecraftFont.Font;
		} else {
			this.font = Cartographer.getMain().getFont( font );
		}
	}
	
	public int maxWaypoints() {
		return maxWaypoints;
	}
	
	public double getDefaultRange() {
		return range;
	}
	
	public boolean showDiscoverMessage() {
		return showDiscover;
	}
	
	public DisplayType getDefaultDiscoverDisplay() {
		return discoverDisplay;
	}
	
	public DisplayType getDefaultDisplay() {
		return visibility;
	}
	
	public boolean isDefaultLocked() {
		return locked;
	}
	
	public Type getDefaultType() {
		return defaultType;
	}
	
	public MapFont getFont() {
		return font;
	}
	
	public File getDataFolder() {
		return dataFolder;
	}
	
	public WaypointManager getManager() {
		return manager;
	}
	
	public WaypointTextSelector getTextSelector() {
		return selector;
	}
}
