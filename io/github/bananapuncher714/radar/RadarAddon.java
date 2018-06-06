package io.github.bananapuncher714.radar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class RadarAddon extends Module {
	private File datafolder;
	boolean monster, animal, players, hwoob;
	double radius, yrad;
	private static RadarConfig configuration;
	private Minimap map;
	
	@Override
	public void load( Minimap map, File dataFolder) {
		datafolder = dataFolder;
		this.map = map;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "radar-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/radar/config.yml" ) );
		loadConfig( config );
		map.registerCursorSelector( new CursorSelector() {
			@Override
			public List< RealWorldCursor > getCursors( Player player ) {
				ArrayList< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
				for ( Entity ent : player.getNearbyEntities( radius, yrad, radius ) ) {
					if ( ent == player ) {
						continue;
					}
					if ( monster && ent instanceof Monster ) {
						cursors.add( new RealWorldCursor( ent.getLocation(), MapCursor.Type.RED_POINTER, hwoob ) );
					} else if ( animal && ent instanceof Animals ) {
						cursors.add( new RealWorldCursor( ent.getLocation(), MapCursor.Type.GREEN_POINTER, hwoob ) );
					} else if ( players && ent instanceof Player ) {
						if ( ( ( Player ) ent ).isSneaking() ) {
							continue;
						}
						if ( player != ent && ent.hasPermission( "cartographer.radar.vanish" ) ) {
							continue;
						}
						if ( ( ( Player ) ent ).hasPotionEffect( PotionEffectType.INVISIBILITY ) ) {
							continue;
						}
						cursors.add( new RealWorldCursor( ent.getLocation(), MapCursor.Type.BLUE_POINTER, hwoob ) );
					}
				}
				return cursors;
			}
		} );
	}

	@Override
	public void unload() {
		
	}
	
	public File getDataFolder() {
		return datafolder;
	}
	
	private void loadConfig( File file ) {
		FileConfiguration c = YamlConfiguration.loadConfiguration( file );
		radius = c.getDouble( "radius" );
		yrad = c.getDouble( "y-radius" );
		monster = c.getBoolean( "monster" );
		animal = c.getBoolean( "animal" );
		players = c.getBoolean( "player" );
		hwoob = c.getBoolean( "hide-when-not-on-map" );
//		configuration = new RadarConfig( c );
	}
	
	public static RadarConfig getConfig() {
		return configuration;
	}
}
