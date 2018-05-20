package io.github.bananapuncher714.worldborder;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.worldborder.events.WorldBorderChangeSizeAndCenterEvent;
import io.github.bananapuncher714.worldborder.events.WorldBorderChangeSizeEvent;
import io.github.bananapuncher714.worldborder.events.WorldBorderSwitchCenterEvent;
import io.github.bananapuncher714.worldborder.listeners.WorldBorderListener;

public class WorldBorderAddon extends Module {
	File dataFolder;
	private boolean showCenter, highlight, border, shade;
	private Type type, rimType;
	private Color borderColor;
	private double tint, distance;
	
	static {
		Bukkit.getPluginManager().registerEvents( new WorldBorderListener(), Cartographer.getMain() );
		Bukkit.getScheduler().scheduleSyncRepeatingTask( Cartographer.getMain(), new Runnable() {
			Map< UUID, Double > sizes = new HashMap< UUID, Double >();
			Map< UUID, Location > locations = new HashMap< UUID, Location >();
			
			@Override
			public void run() {
				for ( World world : Bukkit.getWorlds() ) {
					WorldBorder border = world.getWorldBorder();
					if ( border == null ) return;
					Location center = border.getCenter();
					double length = border.getSize();
					UUID uid = world.getUID();

					if ( !locations.containsKey( uid ) ) {
						locations.put( uid, center );
					}
					if ( !sizes.containsKey( uid ) ) {
						sizes.put( uid, length );
					}
					double size = sizes.get( uid );
					Location loc = locations.get( uid );
					if ( size != length && ( center.getBlockX() != loc.getBlockX() || center.getBlockY() != loc.getBlockY() || center.getBlockZ() != loc.getBlockZ() ) ) {
						Bukkit.getPluginManager().callEvent( new WorldBorderChangeSizeAndCenterEvent( world, border, length, size, center, loc ) );
						locations.put( uid, center );
						sizes.put( uid, length );
					} else if ( center.getBlockX() != loc.getBlockX() || center.getBlockY() != loc.getBlockY() || center.getBlockZ() != loc.getBlockZ() ) {
						Bukkit.getPluginManager().callEvent( new WorldBorderSwitchCenterEvent( world, border, center, loc ) );
						locations.put( uid, center );
					} else if ( length != size ) {
						Bukkit.getPluginManager().callEvent( new WorldBorderChangeSizeEvent( world, border, length, size ) );
						sizes.put( uid, length );
					}
				}
			}
		}, 0, 5 );
	}
	
	@Override
	public void load( Minimap map, File dataFolder) {
		this.dataFolder = dataFolder;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "worldborder-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/worldborder/config.yml" ), true );
		loadConfig( config );
		
		if ( shade ) {
			map.registerShader( new WorldBorderShader( this ) );
		}
		
		map.registerCursorSelector( new WorldBorderCursorSelector( this, type, rimType, distance ) );
	}

	@Override
	public void unload() {
	}

	private void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		String[] s = config.getString( "shade.color" ).split( "\\D+" );
		borderColor = new Color( Integer.parseInt( s[ 0 ] ), Integer.parseInt( s[ 1 ] ), Integer.parseInt( s[ 2 ] ) );
		tint = config.getDouble( "shade.tint-percent" );
		shade = config.getBoolean( "shade.enable" );
		
		type = FailSafe.getEnum( Type.class, config.getString( "center.type" ) );
		showCenter = config.getBoolean( "center.enable" );
		highlight = config.getBoolean( "center.highlight" );
		
		rimType = FailSafe.getEnum( Type.class, config.getString( "cursor.type" ) ) ;
		border = config.getBoolean( "cursor.enable" );
		distance = config.getDouble( "cursor.distance" );
	}

	public boolean isBorder() {
		return border;
	}

	public boolean isShowCenter() {
		return showCenter;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public double getTint() {
		return tint;
	}
	
	public boolean isHighlight() {
		return highlight;
	}
}
