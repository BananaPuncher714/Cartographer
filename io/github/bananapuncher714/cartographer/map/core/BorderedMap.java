package io.github.bananapuncher714.cartographer.map.core;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursor.Type;

import com.google.common.io.Files;

import io.github.bananapuncher714.beacon.BeaconAddon;
import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.DependencyManager;
import io.github.bananapuncher714.cartographer.api.map.MapProvider;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.map.addon.MapCursorSelector;
import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.map.addon.PixelShader;
import io.github.bananapuncher714.cartographer.api.map.addon.TextSelector;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.map.interactive.MapViewerAddon;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.citizens.CitizensAddon;
import io.github.bananapuncher714.cursor.CursorAddon;
import io.github.bananapuncher714.display.TextDisplay;
import io.github.bananapuncher714.factionsuuid.FactionsUUIDAddon;
import io.github.bananapuncher714.gangsplus.GangsPlusAddon;
import io.github.bananapuncher714.griefprevention.GriefPreventionAddon;
import io.github.bananapuncher714.imager.ImagerAddon;
import io.github.bananapuncher714.kingdoms.KingdomsAddon;
import io.github.bananapuncher714.marker.MarkerAddon;
import io.github.bananapuncher714.mythicmobs.MythicMobsAddon;
import io.github.bananapuncher714.quests.QuestsAddon;
import io.github.bananapuncher714.radar.RadarAddon;
import io.github.bananapuncher714.shader.ShaderAddon;
import io.github.bananapuncher714.towny.TownyAddon;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.worldborder.WorldBorderAddon;
import io.github.bananapuncher714.worldguard.WorldGuardAddon;

public class BorderedMap implements Minimap {
	Map< Material, Map< Byte, Color > > mapColors = new HashMap< Material, Map< Byte, Color > >();
	private final Set< CursorSelector > cursors = new HashSet< CursorSelector >();
	private final Set< TextSelector > text = new HashSet< TextSelector >();
	private final List< PixelShader > pixels = new ArrayList< PixelShader >();
	private final Set< MapCursorSelector > mapCursors = new HashSet< MapCursorSelector >();
	
	private final Map< String, Module > modules = new HashMap< String, Module >();
	Set< MapShader > shaders = new HashSet< MapShader >();
	List< String > colorfiles = new ArrayList< String >();
	Set< Material > skipBlocks = new HashSet< Material >();
	List< String > lore;
	private boolean zoomBlacklist;
	Set< ZoomScale > zooms = new HashSet< ZoomScale >();
	private byte[][] overlay = new byte[ 128 ][ 128 ];
	
	// Map settings
	private int x, y, width, height;
	private int discoverRad = 0;
	private int mapHeight, waters;
	private World mainWorld;
	private double curRad = 3;
	private boolean centerChunk = true;
	private boolean showPlayer = true;
	private boolean updates = true;
	private boolean renderOnChunkLoad = false;
	private boolean cursorEnabled = true;
	private boolean forceOverlay = false;
	private boolean hinh = false;
	private boolean sh = true;
	private boolean staticColors = false;
	private double loadRadius;
	private boolean square = false;
	private Type defPointer = MapCursor.Type.WHITE_POINTER;
	private Type cursorType = Type.WHITE_CROSS;
	private Color defc = new Color( 255, 0, 255 );
	private File dataFolder;
	
	private MapProvider provider;
	
	private UUID specialId;
	private String name = "UNSET";
	private String id;
	
	private Cartographer main = Cartographer.getMain();
	
	public BorderedMap( File dataFolder, String name ) {
		this( dataFolder );
		this.name = name;
	}
	
	public BorderedMap( File dataFolder ) {
		this.dataFolder = dataFolder;
		id = dataFolder.getName();
		dataFolder.mkdirs();
		
		info( "Loading map " + id );
		try {
			CLogger.debug( "Saving config if not already saved" );
			File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "master-config.yml" );
			File localConfig = new File( dataFolder + "/" + "config.yml" );
			if ( !localConfig.exists() ) {
				Files.copy( masterConfig, localConfig );
			}
			FileUtil.updateConfigFromFile( localConfig, main.getResource( "data/map/config.yml" ) );
			
			FileUtil.saveToFile( main.getResource( "data/map/README.txt" ), new File( dataFolder + "/" + "README.txt" ), true );
			
			info( "Loading config..." );
			loadConfig();
			info( "Config loaded successfully!" );

			info( "Loading map provider..." );
			provider = new UniversalMapProvider( this, mapColors, skipBlocks );
			info( "Done loading map provider!" );
			
			info( "Done loading map " + id );
		} catch ( Exception exception ) {
			exception.printStackTrace();
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG START =====" );
			CLogger.severe( ChatColor.RED + "Uh-oh! Looks like Cartographer had an error while loading!" );
			CLogger.severe( ChatColor.RED + "In order to prevent file corruption or deletion, Cartographer is now disabled!" );
			CLogger.severe( ChatColor.RED + "Please report the following error to BananaPuncher714" );
			exception.printStackTrace();
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG FINISH =====" );
			Bukkit.getPluginManager().disablePlugin( main );
		}
	}
	
	public void disable() {
		try {
			provider.disable();
			saveModules();
			saveData();
		} catch ( Exception exception ) {
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG START" );
			CLogger.severe( ChatColor.RED + "Oh no! It looks like Cartographer is broken! Please contact BananaPuncher714 ASAP!" );
			CLogger.severe( ChatColor.RED + "If you are seeing this message it means Cartographer's contingency plan has failed!" );
			CLogger.severe(ChatColor.RED +  "Please shut down your server to prevent data loss and report the following error:" );
			exception.printStackTrace();
			CLogger.severe( ChatColor.RED + "===== CARTOGRAPHER ERROR LOG FINISH =====" );
		}
	}
	
	private FileConfiguration getConfig() {
		return YamlConfiguration.loadConfiguration( new File( dataFolder + "/" + "config.yml" ) );
	}
	
	private void loadConfig() {
		try {
			CLogger.debug( "Loading the config..." );
			FileConfiguration c = getConfig();
			name = c.getString( "name" ).replace( "&", "\u00a7" );
			lore = c.getStringList( "lore" );
			centerChunk = c.getBoolean( "map.center-chunk" );
			skipBlocks.add( Material.AIR );
			updates = c.getBoolean( "map.updates" );
			renderOnChunkLoad = c.getBoolean( "map.render-on-chunk-load" );
			forceOverlay = c.getBoolean( "misc.force-overlay" );
			cursorType = FailSafe.getEnum( Type.class, c.getString( "cursor.type" ) );
			cursorEnabled = c.getBoolean( "cursor.enabled" );
			curRad = c.getDouble( "cursor.radius" );
			loadRadius = c.getDouble( "map.player-load-radius" );
			discoverRad = c.getInt( "misc.discover-range" );
			square = c.getBoolean( "map.square-load" );
			staticColors = c.getBoolean( "misc.static-colors" );
			String uuid = c.getString( "uid" );
			if ( uuid != null ) {
				specialId = UUID.fromString( uuid );
			} else {
				specialId = UUID.randomUUID();
			}
			info( "Loading the presets..." );
			loadPresetList();
			info( "Loading the map settings..." );
			loadMapSettings();
			info( "Loading all colors..." );
			loadAllColors( true );
			info( "Loading all transparent blocks..." );
			loadAllTransparentBlocks( true );
			info( "Loading map data..." );
			loadData();
			info( "Loading the modules..." );
			loadModules();
			info( "Done loading the config!" );
		} catch ( Exception exception ) {
			CLogger.debug( "Error thrown in 'loadConfig()'!" );
			throw exception;
		}
	}
	
	public void reload() {
		try {
			CLogger.debug( "Reloading the map " + id );
			CLogger.info( "Saving map data..." );
			saveData();
			CLogger.debug( "Saving the modules..." );
			saveModules();

			CLogger.debug( "Clearing the shaders..." );
			shaders.clear();
			CLogger.debug( "Clearing the modules..." );
			modules.clear();
			text.clear();
			cursors.clear();

			CLogger.debug( "Reloading the config" );
			loadConfig();
			CLogger.debug( "Done reloading map " + id );
		} catch ( Exception exception ) {
			CLogger.debug( "Error thrown in 'reload()'!" );
			throw exception;
		}
	}
	
	private void loadMapSettings() {
		try {
			CLogger.debug( "Loading the map settings..." );
			String[] s = getConfig().getString( "shading.default-color" ).split( "\\D+" );
			defc = new Color( Integer.parseInt( s[ 0 ] ), Integer.parseInt( s[ 1 ] ), Integer.parseInt( s[ 2 ] ) );
			waters = getConfig().getInt( "shading.water-shading" );
			mapHeight = getConfig().getInt( "shading.shade-height" );
			showPlayer = getConfig().getBoolean( "misc.show-player" );
			height = getConfig().getInt( "map.height" );
			width = getConfig().getInt( "map.width" );
			for ( String zoom : getConfig().getStringList( "map.zooms.list-of-zooms" ) ) {
				zooms.add( FailSafe.getEnum( ZoomScale.class, zoom ) );
			}
			zoomBlacklist = getConfig().getBoolean( "map.zooms.blacklist" );
			hinh = getConfig().getBoolean( "shading.hide-if-hidden" );
			sh = getConfig().getBoolean( "shading.show-highest" );
			CLogger.debug( "Done loading the map settings" );
		} catch ( Exception exception ) {
			CLogger.debug( "Error thrown in 'loadMapSettings()'!" );
			throw exception;
		}
	}

	public void loadPresetList() {
		colorfiles = getConfig().getStringList( "presets" );
	}
	
	public void loadAllTransparentBlocks( boolean clear ) {
		CLogger.debug( "Loading the transparent blocks..." );
		new File( dataFolder + File.separator + "presets" + File.separator ).mkdirs();
		for ( String name : colorfiles ) {
			File colorConfig = new File( dataFolder + File.separator + "presets" + File.separator + name );
			if ( !colorConfig.exists() ) {
				colorConfig = new File( main.getDataFolder() + "/" + "presets" + "/" + name );
			}
			if ( !colorConfig.exists() ) {
				CLogger.warning( "preset '" + name + "' does not exist! Some blocks may not appear properly!" );
				continue;
			}
			FileConfiguration cc = YamlConfiguration.loadConfiguration( colorConfig );
			if ( loadTransparentBlocks( cc, false ) ) {
				info( "Loaded the transparent blocks of preset '" + name + "' successfully!" );
			}
		}
		CLogger.debug( "Loaded the transparent blocks successfully!" );
	}
	
	@Override
	public boolean loadTransparentBlocks( FileConfiguration c, boolean clear ) {
		if ( clear ) skipBlocks.clear();
		if ( !c.contains( "transparent-blocks" ) ) return false;
		for ( String s : c.getStringList( "transparent-blocks" ) ) {
			String sf = s;
			boolean negate = false;
			if ( s.startsWith( "-" ) ) {
				sf = s.substring( 1 );
				negate = true;
			}
			Material mat;
			try {
				mat = Material.getMaterial( Integer.parseInt( sf ) );
			} catch ( Exception e ) {
				try {
					mat = Material.getMaterial( sf );
				} catch ( Exception e2 ) {
					CLogger.warning( sf + " does not exist! Older version of Minecraft, perhaps?" );
					continue;
				}
			}
			if ( negate ) {
				if ( skipBlocks.contains( mat ) ) skipBlocks.remove( mat );
			} else {
				skipBlocks.add( mat );
			}
		}
		return true;
	}
	
	public void loadAllColors( boolean clear ) {
		new File( dataFolder + File.separator + "presets" + File.separator ).mkdirs();
		for ( String name : colorfiles ) {
			File colorConfig = new File( dataFolder + File.separator + "presets", name );
			if ( !colorConfig.exists() ) {
				colorConfig = new File( main.getDataFolder() + "/" + "presets" + "/" + name );
			}
			if ( !colorConfig.exists() ) {
				CLogger.warning( "preset '" + name + "' does not exist! Some colors may be missing!" );
				continue;
			}
			FileConfiguration cc = YamlConfiguration.loadConfiguration( colorConfig );
			if ( loadColors( cc, false ) ) {
				info( "Loaded the colors of preset '" + name + "' successfully!" );
			}
		}
	}
	
	@Override
	public boolean loadColors( FileConfiguration c, boolean clear ) {
		if ( clear ) mapColors.clear();
		if ( c.getConfigurationSection( "map-colors" ) == null ) return false;
		for ( String s : c.getConfigurationSection( "map-colors" ).getKeys( false ) ) {
			String[] split = c.getString( "map-colors." + s ).split( "\\D+" );
			String[] matSplit = s.split( "," );
			loadColor( matSplit, split );
		}
		return true;
	}
	
	@Override
	public void loadColor( Material m, byte data, Color c ) {
		Material mat = m;
		HashMap< Byte, Color > tempMap = mapColors.containsKey( mat ) ? ( HashMap< Byte, Color > ) mapColors.get( mat ) : new HashMap< Byte, Color >();
		tempMap.put( data , c );
		mapColors.put( mat, tempMap );
		return;
	}
	
	@Override
	public void loadColor( Material m, byte data, int r, int g, int b ) {
		loadColor( m, data, new Color( r, g, b ) );
	}
	
	private boolean loadColor( String[] md, String[] rgb ) {
		Material mat = Material.AIR;
		boolean found = false;
		try {
			mat = Material.getMaterial( Integer.parseInt( md[ 0 ] ) );
			found = true;
		} catch ( Exception e ) {
		}
		if ( !found ) {
			try {
				mat = Material.getMaterial( md[ 0 ] );
			} catch ( Exception e2 ) {
			}
		}
		
		if ( mat == null ) {
			CLogger.warning( md[ 0 ] + " does not exist! Older version of Minecraft, perhaps?" );
			return false;
		}
		
		loadColor( mat, Byte.parseByte( md[ 1 ] ), new Color( Integer.parseInt( rgb[ 0 ] ), Integer.parseInt( rgb[ 1 ] ), Integer.parseInt( rgb[ 2 ] ) ) );
		return true;
	}

	private void loadData() {
		try {
			CLogger.debug( "Loading all map data..." );
			FileUtil.saveToFile( main.getResource( "data/map/data.yml" ), new File( dataFolder + "/" + "data.yml" ), false );
			File data = new File( this.dataFolder + File.separator + "data.yml" );
			FileConfiguration datac = YamlConfiguration.loadConfiguration( data );
			x = datac.getInt( "x" );
			y = datac.getInt( "y" );
			mainWorld = Bukkit.getWorld( datac.getString( "world" ) );
			File file = new File( dataFolder, "overlay.ser" );
			if ( file.exists() ) {
				try {
					CLogger.debug( "Attempting to read the overlay file..." );
					FileInputStream fileIn = new FileInputStream( file );
					ObjectInputStream in = new ObjectInputStream( fileIn );
					overlay = ( byte[][] ) in.readObject();
					in.close();
					fileIn.close();
					info( "A overlay was found!" );
				} catch( IOException i ) {
					CLogger.warning( "There was an error while reading the overlay data on disk!" );
				} catch( ClassNotFoundException c ) {
					CLogger.severe( "'" + file.getName() + "' is not a proper map file! Reloading the map!" );
				}
			} else {
				overlay = new byte[ 128 ][ 128 ];
				info( "An overlay was not found!!" );
			}
			saveData();
		} catch ( Exception exception ) {
			CLogger.debug( "Error thrown in 'loadData()'!" );
			throw exception;
		}
	}
	
	public void saveData() {
		CLogger.debug( "Saving map data..." );
		File data = new File( this.dataFolder + File.separator + "data.yml" );
		FileConfiguration datac = YamlConfiguration.loadConfiguration( data );
		datac.set( "x", x );
		datac.set( "y", y );
		datac.set( "id", specialId.toString() );
		if ( mainWorld != null ) {
			datac.set( "world", mainWorld.getName() );
		}
		try {
			datac.save( data );
		} catch (IOException e) {
			CLogger.warning( "There was an error while trying to save the data to file!" );
		}
		CLogger.debug( "Done map data!" );
	}
	
	@Override
	public void loadModule( String id, Module module ) {
		File dataFolder = new File( getDataFolder() + "/modules/" + id );
		dataFolder.mkdirs();
		module.load( this, dataFolder );
		Module old = modules.put( id, module );
		old.unload();
	}
	
	private void loadModules() {
		try {
			CLogger.debug( "Loading modules..." );
			FileConfiguration c = getConfig();
			if ( c.getBoolean( "module.waypoints" ) ){
				WaypointAddon addon = new WaypointAddon();
				File waypointFolder = new File( getDataFolder() + "/modules/" + "waypoints/" );
				waypointFolder.mkdirs();
				addon.load( this, waypointFolder );
				modules.put( "waypoints", addon );
			}
			if ( c.getBoolean( "module.radar" ) ) {
				RadarAddon addon = new RadarAddon();
				File radarFolder = new File( getDataFolder() + "/modules/" + "radar/" );
				radarFolder.mkdirs();
				addon.load( this, radarFolder );
				modules.put( "radar", addon );
			}
			if ( c.getBoolean( "module.display" ) ) {
				TextDisplay addon = new TextDisplay();
				File displayFile = new File( getDataFolder() + "/modules/" + "display/" );
				displayFile.mkdirs();
				addon.load( this, displayFile );
				modules.put( "display", addon );
			}
			if ( c.getBoolean( "module.kingdoms" ) && DependencyManager.isKingdomsEnabled() ) {
				KingdomsAddon addon = new KingdomsAddon();
				File kingdomsFile = new File( getDataFolder() + "/modules/" + "kingdoms" );
				kingdomsFile.mkdirs();
				addon.load( this, kingdomsFile );
				modules.put( "kingdoms", addon );
			}
			if ( c.getBoolean( "module.factionsuuid" ) && DependencyManager.isFactionsUUIDEnabled() ) {
				FactionsUUIDAddon addon = new FactionsUUIDAddon();
				File factionsFile = new File( getDataFolder() + "/modules/" + "factionsuuid" );
				factionsFile.mkdirs();
				addon.load( this, factionsFile );
				modules.put( "factionsuud", addon );
			}
			if ( c.getBoolean( "module.towny" ) && DependencyManager.isTownyEnabled() ) {
				TownyAddon addon = new TownyAddon();
				File townyFile = new File( getDataFolder() + "/modules/" + "towny" );
				townyFile.mkdirs();
				addon.load( this, townyFile );
				modules.put( "towny", addon );
			}
			if ( c.getBoolean( "module.worldguard" ) && DependencyManager.isWorldGuardEnabled() ) {
				WorldGuardAddon addon = new WorldGuardAddon();
				File wgFile = new File( getDataFolder() + "/modules/" + "worldguard" );
				wgFile.mkdirs();
				addon.load( this, wgFile );
				modules.put( "worldguard", addon );
			}
			if ( c.getBoolean( "module.citizens" ) && DependencyManager.isCitizensEnabled() ) {
				CitizensAddon addon = new CitizensAddon();
				File cFile = new File( getDataFolder() + "/modules/" + "citizens" );
				cFile.mkdirs();
				addon.load( this, cFile );
				modules.put( "citizens", addon );
			}
			if ( c.getBoolean( "module.quests" ) && DependencyManager.isQuestsEnabled() ) {
				QuestsAddon addon = new QuestsAddon();
				File qFile = new File( getDataFolder() + "/modules/" + "quests" );
				qFile.mkdirs();
				addon.load( this, qFile );
				modules.put( "quests", addon );
			}
			if ( c.getBoolean( "module.worldborder" ) ) {
				WorldBorderAddon addon = new WorldBorderAddon();
				File wFile = new File( getDataFolder() + "/modules/" + "worldborders" );
				wFile.mkdirs();
				addon.load( this, wFile );
				modules.put( "worldborder", addon );
			}
			if ( c.getBoolean( "module.playerheads" ) ) {
				ImagerAddon addon = new ImagerAddon();
				File iFile = new File( getDataFolder() + "/modules/" + "playerheads" );
				iFile.mkdirs();
				addon.load( this, iFile );
				modules.put( "playerheads", addon );
			}
			if ( c.getBoolean( "module.cursor" ) ) {
				CursorAddon addon = new CursorAddon();
				File cFile = new File( getDataFolder() + "/modules/" + "cursor" );
				cFile.mkdirs();
				addon.load( this, cFile );
				modules.put( "cursor", addon );
			}
			if ( c.getBoolean( "module.beacon" ) ) {
				BeaconAddon addon = new BeaconAddon();
				File bFile = new File( getDataFolder() + "/modules/" + "beacon" );
				bFile.mkdirs();
				addon.load( this, bFile );
				modules.put( "beacon", addon );
			}
			if ( DependencyManager.isGangsPlusEnabled() && c.getBoolean( "module.gangsplus" ) ) {
				GangsPlusAddon addon = new GangsPlusAddon();
				File gFile = new File( getDataFolder() + "/modules/" + "gangsplus" );
				gFile.mkdirs();
				addon.load( this, gFile );
				modules.put( "gangsplus", addon );
			}
			if ( c.getBoolean( "module.markers" ) ) {
				MarkerAddon addon = new MarkerAddon();
				File mFile = new File( getDataFolder() + "/modules/" + "markers" );
				mFile.mkdirs();
				addon.load( this, mFile );
				modules.put( "markers", addon );
			}
			if ( c.getBoolean( "module.griefprevention" ) && DependencyManager.isGriefPreventionEnabled() ) {
				GriefPreventionAddon addon = new GriefPreventionAddon();
				File gFile = new File( getDataFolder() + "/modules/" + "griefprevention" );
				gFile.mkdirs();
				addon.load( this, gFile );
				modules.put( "griefprevention", addon );
			}
			if ( c.getBoolean( "module.shaders" ) ) {
				ShaderAddon addon = new ShaderAddon();
				File sFile = new File( getDataFolder() + "/modules/" + "shaders" );
				sFile.mkdirs();
				addon.load( this, sFile );
				modules.put( "shaders", addon );
			}
			if ( c.getBoolean( "module.mythicmobs" ) && DependencyManager.isMythicMobsEnabled() ) {
				MythicMobsAddon addon = new MythicMobsAddon();
				File mFile = new File( getDataFolder() + "/modules/" + "mythicmobs" ) ;
				mFile.mkdirs();
				addon.load( this, mFile );
				modules.put( "mythicmobs", addon );
			}
			{
				MapViewerAddon addon = new MapViewerAddon();
				File mFile = new File( getDataFolder() + "/modules/mapviewer" );
				mFile.mkdirs();
				addon.load( this, mFile );
				modules.put( "mapviewer", addon );
			}
			CLogger.debug( "Modules loaded successfully!" );
			for ( String string : modules.keySet() ) {
				info( "The '" + string + "' module has been loaded!" );
			}
		} catch ( Exception exception ) {
			CLogger.debug( "Error thrown in 'loadModules()'!" );
			throw exception;
		}
	}
	
	private void saveModules() {
		for ( Module module : modules.values() ) {
			module.unload();
		}
	}
	
	@Override
	public Map< String, Module > getModules() {
		return modules;
	}
	
	@Override
	public void registerShader( MapShader shader ) {
		shaders.add( shader );
	}
	
	@Override
	public UUID getUID() {
		return specialId;
	}

	@Override
	public File getDataFolder() {
		return dataFolder;
	}
	
	@Override
	public void registerCursorSelector( CursorSelector selector ) {
		cursors.add( selector );
	}
	
	@Override
	public Set< CursorSelector > getCursorSelectors() {
		return cursors;
	}
	
	@Override
	public void registerMapCursorSelector( MapCursorSelector selector ) {
		mapCursors.add( selector );
	}
	
	@Override
	public Set< MapCursorSelector > getMapCursorSelectors() {
		return mapCursors;
	}
	
	@Override
	public void registerTextSelector( TextSelector selector ) {
		text.add( selector );
	}
	
	@Override
	public Set< TextSelector > getTextSelectors() {
		return text;
	}
	
	@Override
	public void registerPixelShader( PixelShader shader ) {
		for ( int index = 0; index < pixels.size(); index++ ) {
			PixelShader otherShader = pixels.get( index );
			if ( otherShader.getPriority() < shader.getPriority() ) {
				pixels.add( index + 1, shader );
				return;
			}
		}
		pixels.add( shader );
	}
	
	@Override
	public List< PixelShader > getPixelShaders() {
		return pixels;
	}
	
	@Override
	public Set< MapShader > getShaders() {
		return shaders;
	}
	
	@Override
	public byte[][] getOverlay() {
		if ( overlay == null ) {
			return null;
		}
		return overlay.clone();
	}
	
	@Override
	public void setOverlay( byte[][] overlay ) {
		if ( overlay != null ) this.overlay = overlay;
		saveOverlay();
	}
	
	private void saveOverlay() {
		if ( FileUtil.saveByteArray( overlay, new File( dataFolder, "overlay.ser" ) ) ) {
			info( "Saved overlay successfully to '" + "overlay.ser" + "'" );
		} else {
			CLogger.warning( "There was an error while saving the overlay to disk!" );
		}
	}
	
	@Override
	public void setOverlay( File file ) {
		overlay = FileUtil.loadFile( file );
		saveOverlay();
	}
	
	@Override
	public boolean showPlayer() {
		return showPlayer;
	}
	
	@Override
	public void showPlayer( boolean showPlayer ) {
		this.showPlayer = showPlayer;
	}
	
	@Override
	public boolean removeTransparentBlock( Material material ) {
		if ( skipBlocks.contains( material ) ) {
			skipBlocks.remove( material );
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addTransparetMaterial( Material material ) {
		if ( !skipBlocks.contains( material ) ) {
			skipBlocks.add( material );
			return true;
		}
		return false;
	}
	
	@Override
	public Set< Material > getTransparentBlocks() {
		return new HashSet< Material >( skipBlocks );
	}
	
	@Override
	public MapCursor.Type getDefPointer() {
		return defPointer;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public List< String > getLore() {
		return lore;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Set< ZoomScale > getZoomList() {
		return zooms;
	}
	
	@Override
	public boolean isZoomScaleBlacklist() {
		return zoomBlacklist;
	}
	
	@Override
	public boolean isZoomAllowed( ZoomScale scale ) {
		return zooms.contains( scale ) ^ zoomBlacklist;
	}
	
	@Override
	public MapCursor.Type setDefPointer( MapCursor.Type defPointer ) {
		MapCursor.Type old = defPointer;
		this.defPointer = defPointer;
		return old;
	}
	
	@Override
	public Type getCursorType() {
		return cursorType;
	}
	
	@Override
	public double getCursorRadius() {
		return curRad;
	}
	
	@Override
	public boolean isCursorEnabled() {
		return cursorEnabled;
	}
	
	@Override
	public boolean isUpdateEnabled() {
		return updates;
	}
	
	@Override
	public boolean isCenterChunk() {
		return centerChunk;
	}
	
	@Override
	public boolean isRenderOnChunkLoad() {
		return renderOnChunkLoad;
	}
	
	@Override
	public boolean isSquareLoad() {
		return square;
	}
	
	@Override
	public boolean isForceOverlay() {
		return forceOverlay;
	}
	
	@Override
	public boolean isStaticColors() {
		return staticColors;
	}
	
	public MapProvider getProvider() {
		return provider;
	}
	
	@Override
	public int getMapHeight() {
		return mapHeight;
	}
	
	@Override
	public int getWaterShading() {
		return waters;
	}
	
	@Override
	public boolean isHighestIfNotHidden() {
		return hinh;
	}
	
	@Override
	public boolean isShowHidden() {
		return sh;
	}
	
	@Override
	public Color getDefaultColor() {
		return defc;
	}
	
	@Override
	public double getLoadRadius() {
		return loadRadius;
	}
	
	@Override
	public int getDiscoverRadius() {
		return discoverRad * discoverRad;
	}
	
	@Override
	public void updateLocation( Location loc ) {
		provider.updateLocation( loc );
	}
	
	@Override
	public void recolorChunk( Location location ) {
		int xbound = location.getChunk().getX();
		int ybound = location.getChunk().getZ();
		recolorChunk( location.getWorld(), xbound, ybound );
	}
	
	@Override
	public void recolorChunk( World world, int x, int z ) {
		provider.updateChunk( new ChunkLocation( world, x, z ) );
	}
	
	@Override
	public void refreshMap( boolean reload ) {
		CLogger.debug( "Refreshing map..." );
		if ( reload ) {
			loadMapSettings();
		}
		loadPresetList();
		loadAllColors( reload );
		loadAllTransparentBlocks( reload );
		CLogger.debug( "Done refreshing map!" );
	}
	
	@Override
	public void relocateMap( int centerX, int centerY, int width, int height, World world ) {
		x = centerX;
		y = centerY;
		this.width = width;
		this.height = height;
		mainWorld = world;
		return;
	}
	
	public Color shadeLocation( Location location, Color color ) {
		Color c = color;
		for ( MapShader shader : shaders ) {
			c = shader.shadeLocation( location, c );
		}
		return c;
	}
	
	@Override
	public World getWorld() {
		return mainWorld;
	}
	
	@Override
	public int getCenterX() {
		return x;
	}
	
	@Override
	public int getCenterY() {
		return y;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public boolean isInfinite() {
		return width <= 0 || height <= 0;
	}

	protected Set< Material > getSkipBlocks() {
		return skipBlocks;
	}
	
	protected Map< Material, Map< Byte, Color > > getMapColors() {
		return mapColors;
	}
	private void info( String message ) {
		CLogger.info( "[" + id + "] " + message );
	}
}
