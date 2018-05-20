package io.github.bananapuncher714.quests;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class QuestsAddon extends Module {
	private Type loc, interact, kill;
	private boolean highlight;
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "quests-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/quests/config.yml" ), true );
		loadConfig( config );
		
		map.registerCursorSelector( new QuesterSelector( this ) );
	}

	@Override
	public void unload() {

	}

	public void loadConfig( File file ) {
		FileConfiguration c = YamlConfiguration.loadConfiguration( file );
		loc = FailSafe.getEnum( Type.class, c.getString( "location-type" ) );
		interact = FailSafe.getEnum( Type.class, c.getString( "npc-interact-type" ) );
		kill = FailSafe.getEnum( Type.class, c.getString( "npc-kill-type" ) );
		highlight = c.getBoolean( "highlight-objectives" );
	}

	public Type getLoc() {
		return loc;
	}

	public Type getInteract() {
		return interact;
	}

	public Type getKill() {
		return kill;
	}

	public boolean isHighlight() {
		return highlight;
	}
}
