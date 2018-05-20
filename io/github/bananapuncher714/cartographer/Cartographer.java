/**
 * The base of Cartographer
 * 
 * @author BananaPuncher714
 */

package io.github.bananapuncher714.cartographer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bananapuncher714.cartographer.api.CartographerPlugin;
import io.github.bananapuncher714.cartographer.api.objects.ZoomAction;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.api.util.ImageUtil;
import io.github.bananapuncher714.cartographer.commands.CartographerExecutor;
import io.github.bananapuncher714.cartographer.commands.CartographerTabCompleter;
import io.github.bananapuncher714.cartographer.commands.CursorExecutor;
import io.github.bananapuncher714.cartographer.commands.CursorTabCompleter;
import io.github.bananapuncher714.cartographer.commands.MapReloadExecutor;
import io.github.bananapuncher714.cartographer.commands.SetMapCenterExecutor;
import io.github.bananapuncher714.cartographer.commands.SetMapCenterTabCompleter;
import io.github.bananapuncher714.cartographer.inv.CartographerBananaManager;
import io.github.bananapuncher714.cartographer.listeners.BlockUpdateListener;
import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;
import io.github.bananapuncher714.cartographer.listeners.MapInteractListener;
import io.github.bananapuncher714.cartographer.listeners.PlayerChangeMapListener;
import io.github.bananapuncher714.cartographer.map.BananaFont;
import io.github.bananapuncher714.cartographer.map.core.BorderedMap;
import io.github.bananapuncher714.cartographer.map.interactive.CursorMoveListener;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.cartographer.util.FontLoader;
import io.github.bananapuncher714.locale.LocaleAddon;

public class Cartographer extends JavaPlugin implements Listener, CartographerPlugin {
	public static final String MAP_SAVE_FOLDER = "saves";
	
	String welcome = "Thank you, " + "%%__USER__%%" + ", for buying Cartographer!";
	
	Map< String, BananaFont > fonts = new HashMap< String, BananaFont >();
	LocaleAddon locales;
	private int selectorLoadDelay;
	private int mapLoadSpeed = 5;
	private int maxCursors = 500;
	private byte[][] missingMap = new byte[ 128 ][ 128 ];
	private Set< String > blacklist = new HashSet< String >();
	private boolean worldWhitelist = false;
	private boolean circularZooming = false;
	private ZoomAction left, right;
	private double tpsThreshold;
	
	private static Cartographer main;
	public static boolean DEBUG = false;
	
	@Override
	public void onEnable() {
		try {
			main = this;
			
			DEBUG = getConfig().getBoolean( "debug" );

			update();
			
			CLogger.debug( "Saving config if not already saved" );
			saveDefaultConfig();
			FileUtil.updateConfigFromFile( new File( getDataFolder() + "/" + "config.yml" ), getResource( "config.yml" ), true );
			FileUtil.saveToFile( getResource( "data/help/README.txt" ), new File( getDataFolder() + "/" + "README.txt" ), true );
			FileUtil.saveToFile( getResource( "data/help/permissions.txt" ), new File( getDataFolder() + "/" + "PERMISSIONS.txt" ), true );
			FileUtil.saveToFile( getResource( "data/help/commands.txt" ), new File( getDataFolder() + "/" + "COMMANDS.txt" ), true );
			
			CLogger.debug( "Saving presets if not already saved" );
			FileUtil.saveToFile( getResource( "presets/x-ray.yml" ), new File( getDataFolder() + "/presets/", "x-ray.yml" ), false );
			FileUtil.saveToFile( getResource( "presets/1.11.2.yml" ), new File( getDataFolder() + "/presets/", "1.11.2.yml" ), false );
			FileUtil.saveToFile( getResource( "data/font/font_template.yml" ), new File( getDataFolder() + "/" + "fonts",  "font_template.yml"), true );
			FileUtil.saveToFile( getResource( "data/font/fancy_font.yml" ), new File( getDataFolder() + "/" + "fonts",  "fancy.yml"), false );
			FileUtil.saveToFile( getResource( "data/map/missing-map.ser" ), new File( getDataFolder() + "/" + "missing-map.ser" ), false );

			saveConfigs();
			
			CLogger.debug( "Presets saved successfully!" );
			
			CLogger.info( "Dumping colors in 'map-colors.yml'" );
			ImageUtil.dumpColors( new File( getDataFolder(), "map-colors.yml" ) );
			CLogger.info( "Loading commands..." );
			loadCommands();
			CLogger.info( "Registering listeners..." );
			registerListeners();
			CLogger.info( "Loading Cartographer config..." );
			loadConfig();
			CLogger.info( "Config loaded successfully!" );
			MapManager.getInstance();
		} catch ( Exception exception ) {
			exception.printStackTrace();
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG START =====" );
			CLogger.severe( ChatColor.RED + "Uh-oh! Looks like Cartographer had an error while loading!" );
			CLogger.severe( ChatColor.RED + "In order to prevent file corruption or deletion, Cartographer is now disabled!" );
			CLogger.severe( ChatColor.RED + "Please report the following error to BananaPuncher714" );
			exception.printStackTrace();
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG FINISH =====" );
			Bukkit.getPluginManager().disablePlugin( this );
		}
	}
	
	@Override
	public void onDisable() {
		try {
			locales.unload();
			MapManager.getInstance().disable();
		} catch ( Exception exception ) {
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG START" );
			CLogger.severe( ChatColor.RED + "Oh no! It looks like Cartographer is broken! Please contact BananaPuncher714 ASAP!" );
			CLogger.severe( ChatColor.RED + "If you are seeing this message it means Cartographer's contingency plan has failed!" );
			CLogger.severe(ChatColor.RED +  "Please shut down your server to prevent data loss and report the following error:" );
			exception.printStackTrace();
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG FINISH =====" );
		}
	}

	private void saveConfigs() {
		FileUtil.saveToFile( getResource( "data/map/config.yml" ), new File( getDataFolder() + "/configs/" + "master-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "master-config.yml" ), getResource( "data/map/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/citizens/config.yml" ), new File( getDataFolder() + "/configs/" + "citizens-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "citizens-config.yml" ), getResource( "data/citizens/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/factionsuuid/config.yml" ), new File( getDataFolder() + "/configs/" + "factionsuuid-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "factionsuuid-config.yml" ), getResource( "data/factionsuuid/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/kingdoms/config.yml" ), new File( getDataFolder() + "/configs/" + "kingdoms-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "kingdoms-config.yml" ), getResource( "data/kingdoms/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/quests/config.yml" ), new File( getDataFolder() + "/configs/" + "quests-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "quests-config.yml" ), getResource( "data/quests/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/radar/config.yml" ), new File( getDataFolder() + "/configs/" + "radar-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "radar-config.yml" ), getResource( "data/radar/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/waypoints/config.yml" ), new File( getDataFolder() + "/configs/" + "waypoints-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "waypoints-config.yml" ), getResource( "data/waypoints/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/worldborder/config.yml" ), new File( getDataFolder() + "/configs/" + "worldborder-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "worldborder-config.yml" ), getResource( "data/worldborder/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/worldguard/config.yml" ), new File( getDataFolder() + "/configs/" + "worldguard-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "worldguard-config.yml" ), getResource( "data/worldguard/config.yml" ), true );
		
		FileUtil.saveToFile( getResource( "data/display/config.yml" ), new File( getDataFolder() + "/configs/" + "display-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "display-config.yml" ), getResource( "data/display/config.yml" ) );
		
		FileUtil.saveToFile( getResource( "data/gangsplus/config.yml" ), new File( getDataFolder() + "/configs/" + "gangsplus-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "gangsplus-config.yml" ), getResource( "data/gangsplus/config.yml" ) );
		
		FileUtil.saveToFile( getResource( "data/playerheads/config.yml" ), new File( getDataFolder() + "/configs/" + "playerheads-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "playerheads-config.yml" ), getResource( "data/playerheads/config.yml" ) );
		
		FileUtil.saveToFile( getResource( "data/griefprevention/config.yml" ), new File( getDataFolder() + "/configs/" + "griefprevention-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "griefprevention-config.yml" ), getResource( "data/griefprevention/config.yml" ) );

		FileUtil.saveToFile( getResource( "data/mythicmobs/config.yml" ), new File( getDataFolder() + "/configs/" + "mythicmobs-config.yml" ), false );
		FileUtil.updateConfigFromFile( new File( getDataFolder() + "/configs/" + "mythicmobs-config.yml" ), getResource( "data/mythicmobs/config.yml" ) );
	}
	
	@Override
	public FileConfiguration getConfig() {
		File config = new File( getDataFolder() + "/" + "config.yml" );
		if ( !config.exists() ) {
			return super.getConfig();
		} else {
			return YamlConfiguration.loadConfiguration( config );
		}
	}
	
	private void registerListeners() {
		CLogger.debug( "Registering BlockUpdateListener()" );
		Bukkit.getPluginManager().registerEvents( new BlockUpdateListener(), this );
		CLogger.debug( "Registering ChunkLoadListener()" );
		Bukkit.getPluginManager().registerEvents( new ChunkLoadListener(), this );
		CLogger.debug( "Registering the Map Manager" );
		Bukkit.getPluginManager().registerEvents( new MapInteractListener(), this );
		CLogger.debug( "Registering the inventory manager" );
		Bukkit.getPluginManager().registerEvents( new CartographerBananaManager(), this );
		CLogger.debug( "Registering the cursor tracker" );
		Bukkit.getPluginManager().registerEvents( new CursorMoveListener(), this );
		CLogger.debug( "Registering the player movement tracker" );
		Bukkit.getPluginManager().registerEvents( new PlayerChangeMapListener(), this );
	}

	private void loadCommands() {
		CLogger.debug( "Loading waypoints command..." );
		getCommand( "waypoints" ).setExecutor( new BlankExecutor() );
		CLogger.debug( "Loading cartographer command..." );
		getCommand( "cartographer" ).setExecutor( new CartographerExecutor() );
		CLogger.debug( "Loading cartographer tab completer..." );
		getCommand( "cartographer" ).setTabCompleter( new CartographerTabCompleter() );
		CLogger.debug( "Loading setmapcenter command..." );
		getCommand( "setmapcenter" ).setExecutor( new SetMapCenterExecutor( this ) );
		CLogger.debug( "Loading setmapcenter tab completer..." );
		getCommand( "setmapcenter" ).setTabCompleter( new SetMapCenterTabCompleter() );
		CLogger.debug( "Loading mapreload command..." );
		getCommand( "mapreload" ).setExecutor( new MapReloadExecutor( this ) );
		CLogger.debug( "Loading mapreload tab completer..." );
		getCommand( "mapreload" ).setTabCompleter( new SetMapCenterTabCompleter() );
		CLogger.debug( "Loading cursor command" );
		getCommand( "cursor" ).setExecutor( new CursorExecutor() );
		CLogger.debug( "Loading cursor tab completer" );
		getCommand( "cursor" ).setTabCompleter( new CursorTabCompleter() );
		CLogger.debug( "Loading beacon command" );
		getCommand( "beacon" ).setExecutor( new BlankExecutor() );
	}

	// Here begins a LOT of methods to load and save data from files...
	
	public void reload() {
		CLogger.debug( "Reloading the config" );
		loadConfig();
		CLogger.debug( "Done reloading Cartographer!" );
	}
	
	private void loadConfig() {
		CLogger.debug( "Loading the config..." );
		FileConfiguration c = getConfig();
		mapLoadSpeed = c.getInt( "map-load-speed" );
		selectorLoadDelay = c.getInt( "selector-delay" );
		maxCursors = c.getInt( "max-cursors" );
		List< Short > mapIds = c.getShortList( "map-ids" );
		for ( String name : c.getStringList( "blacklisted-worlds" ) ) {
			blacklist.add( name );
		}
		worldWhitelist = c.getBoolean( "world-whitelist" );
		circularZooming = c.getBoolean( "circular-zooming" );
		left = FailSafe.getEnum( ZoomAction.class, c.getString( "zoom-actions.left" ) );
		right = FailSafe.getEnum( ZoomAction.class, c.getString( "zoom-actions.right" ) );
		tpsThreshold = c.getDouble( "tps-threshold" );
		if ( mapIds != null ) {
			for ( short id : mapIds ) {
				try {
					MapView view = Bukkit.getMap( id );
					if ( view == null ) continue;
					MapManager.getInstance().getMapView( null, view, false, true );
				} catch ( Exception exception ) {
					continue;
				}
				MapInteractListener.addMapId( id );
			}
		}
		CLogger.info( "Loading the fonts..." );
		for ( BananaFont font : FontLoader.loadFonts( new File( getDataFolder() + "/" + "fonts" ) ) ){
			fonts.put( font.getId(), font );
			CLogger.info( "Loaded font '" + font.getId() + "'!" );
		}
		
		missingMap = FileUtil.loadFile( new File( getDataFolder() + "/" + "missing-map.ser" ) );
		
		CLogger.debug( "Loading locales..." );
		locales = new LocaleAddon();
		File localeFile = new File( getDataFolder() + "/" + "locale" );
		localeFile.mkdirs();
		locales.load( localeFile );
		CLogger.debug( "Locale loaded successfully!" );
		CLogger.info( "Done loading the config!" );
	}
	
	public void saveConfig() {
		CLogger.debug( "Saving config..." );
		FileConfiguration datac = getConfig();
		List< Short > mapIds = new ArrayList< Short >( MapInteractListener.getMapIds() );
		datac.set( "map-ids", mapIds );
		try {
			datac.save( new File( getDataFolder() + "/" + "config.yml" ) );
		} catch (IOException e) {
			getLogger().warning( "There was an error while trying to save the data to file!" );
		}
		CLogger.debug( "Done saving config!" );
	}

	public static Cartographer getMain() {
		return main;
	}
	
	@Override
	public int getMapLoadSpeed() {
		return mapLoadSpeed;
	}
	
	@Override
	public int getSelectorLoadDelay() {
		return selectorLoadDelay;
	}
	
	@Override
	public int getMaxCursors() {
		return maxCursors;
	}
	
	@Override
	public MapFont getFont( String id ) {
		return fonts.containsKey( id ) ? fonts.get( id ) : MinecraftFont.Font;
	}
	
	public LocaleAddon getLocaleAddon() {
		return locales;
	}
	
	@Override
	public byte[][] getMissingMap() {
		return missingMap;
	}
	
	@Override
	public double getTpsThreshold() {
		return tpsThreshold;
	}
	
	@Override
	public boolean isBlacklisted( String world ) {
		return blacklist.contains( world ) ^ worldWhitelist;
	}
	
	@Override
	public boolean isCircularZooming() {
		return circularZooming;
	}
	
	@Override
	public ZoomAction getZoomAction( boolean leftClick ) {
		return leftClick ? left : right;
	}
	
	@Override
	public BorderedMap createNewMap( boolean register, String id ) {
		return createNewMap( register, id, null );
	}
	
	public BorderedMap createNewMap( boolean register, String id, String name ) {
		BorderedMap map;
		if ( name != null ) {
			map = new BorderedMap( new File( getDataFolder() + "/" + "saves/" + id ), name );
		} else {
			map = new BorderedMap( new File( getDataFolder() + "/" + "saves/" + id ) );
		}
		if ( register ) {
			MapManager.getInstance().registerMinimap( map );
		}
		return map;
	}
	
	private void update() {
		File configFile = new File( getDataFolder() + "/" + "config.yml" );
		if ( !configFile.exists() ) {
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration( configFile );
		if ( config.getDouble( "config-version" ) == 1.0 ) {
			return;
		}
		CLogger.info( "Legacy Cartographer detected! Updating files..." );
		File root = getDataFolder();
		File defaultMap = new File( root + "/saves/" + "legacy" );
		defaultMap.mkdirs();
		for ( File file : root.listFiles() ) {
			String name = file.getName();
			switch( name ) {
			case "config.yml": {
				FileUtil.move( file, new File( defaultMap + "/" + "config.yml" ), false );
				break;
			}
			case "data.yml": {
				FileUtil.move( file, new File( defaultMap + "/" + "data.yml" ), false );
				break;
			}
			case "README.txt": {
				FileUtil.move( file, new File( defaultMap + "/" + "README.txt" ), false );
				break;
			}
			case "map.ser": {
				FileUtil.move( file, new File( defaultMap + "/" + "map.ser" ), false );
				break;
			}
			case "overlay.ser": {
				FileUtil.move( file, new File( defaultMap + "/" + "overlay.ser" ), false );
				break;
			}
			case "radar": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "radar" ), false );
				break;
			}
			case "quests": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "quests" ), false );
				break;
			}
			case "towny": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "towny" ), false );
				break;
			}
			case "worldborders": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "worldborders" ), false );
				break;
			}
			case "worldguard": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "worldguard" ), false );
				break;
			}
			case "kingdoms": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "kingdoms" ), false );
				break;
			}
			case "citizens": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "citizens" ), false );
				break;
			}
			case "factionsuuid": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "factionsuuid" ), false );
				break;
			}
			case "display": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "display" ), false );
				break;
			}
			case "waypoints": {
				FileUtil.move( file, new File( defaultMap + "/modules/" + "waypoints" ), false );
				break;
			}
			}
		}
		CLogger.info( "Updating completed!" );
	}
}
