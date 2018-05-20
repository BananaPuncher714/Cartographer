package io.github.bananapuncher714.cursor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.MapCursorSelector;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class CursorAddon extends Module {
	Minimap map;

	@Override
	public void load( Minimap map, File dataFolder ) {
		this.map = map;
		FileUtil.saveToFile( Cartographer.getMain().getResource( "data/cursor/config.yml" ), new File( dataFolder + "/" + "config.yml" ), false );
		
		loadConfig( new File( dataFolder + "/" + "config.yml" ) );
		
	}

	private void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		for ( String key : config.getConfigurationSection( "display" ).getKeys( false ) ) {
			int x = config.getInt( "display." + key + ".x" );
			int z = config.getInt( "display." + key + ".z" );
			int orientation = config.getInt( "display." + key + ".direction" ) % 15;
			Type type = FailSafe.getEnum( Type.class, config.getString( "display." + key + ".type" ) );
			String permission = config.getString( "display." + key + ".permission" );
			map.registerMapCursorSelector( new MapCursorSelector() {
				@Override
				public List< MapCursor > getCursors( Player player ) {
					List< MapCursor > cursors = new ArrayList< MapCursor >();
					MapCursor cursor = new MapCursor( ( byte ) x, ( byte ) z, ( byte ) orientation, ( byte ) 0, true );
					cursor.setType( type );
					cursors.add( cursor );
					return cursors;
				}
			} );
		}
	}
	
	@Override
	public void unload() {
		
	}
	
	public Minimap getMap() {
		return map;
	}

}
