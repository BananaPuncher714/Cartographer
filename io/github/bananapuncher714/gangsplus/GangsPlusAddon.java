package io.github.bananapuncher714.gangsplus;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class GangsPlusAddon extends Module {
	private Type one, two, three, four, five;
	private boolean highlighted;

	@Override
	public void load( Minimap map, File dataFolder ) {
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "gangsplus-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/gangsplus/config.yml" ) );
		loadConfig( config );
		
		map.registerCursorSelector( new GangsPlusCursorSelector( this ) );
	}

	@Override
	public void unload() {

	}
	
	private void loadConfig( File file ) {
		FileConfiguration config =YamlConfiguration.loadConfiguration( file );
		one = FailSafe.getEnum( Type.class, config.getString( "rank.one" ) );
		two = FailSafe.getEnum( Type.class, config.getString( "rank.two" ));
		three = FailSafe.getEnum( Type.class, config.getString( "rank.three" ) );
		four = FailSafe.getEnum( Type.class, config.getString( "rank.four" ) );
		five = FailSafe.getEnum( Type.class, config.getString( "rank.five" ) );
		highlighted = config.getBoolean( "highlighted-cursor" );
	}

	protected RealWorldCursor getCursor( Player player, int rank ) {
		Type cursorType;
		switch ( rank ) {
		case 1: cursorType = one; break;
		case 2: cursorType = two; break;
		case 3: cursorType = three; break;
		case 4: cursorType = four; break;
		case 5: cursorType = five; break;
		default: cursorType = Type.WHITE_POINTER; break;
		}
		return new RealWorldCursor( player.getLocation(), cursorType, highlighted );
	}
}
