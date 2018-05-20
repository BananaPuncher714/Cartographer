package io.github.bananapuncher714.display;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.DependencyManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.map.addon.TextSelector;
import io.github.bananapuncher714.cartographer.api.objects.MapText;

public class TextDisplay extends Module {
	private HashMap< String, TextSelector > selectors = new HashMap< String, TextSelector >();
	private File dataFolder;
	private boolean unloaded = false;
	private Minimap map;
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		this.dataFolder = dataFolder;
		this.map = map;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "display-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
				Files.copy( masterConfig, config );
				} catch ( Exception exception ) {
					exception.printStackTrace();
				}
		}
		loadConfig( config );
	}

	@Override
	public void unload() {
		unloaded = true;
	}

	public void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		for ( String id : config.getConfigurationSection( "displays" ).getKeys( false ) ) {
			String permission = config.getString( "displays." + id + ".permission" );
			String message = config.getString( "displays." + id + ".message" );
//			Pattern pattern = Pattern.compile( "\u00a7[0-9]+?[^;]" );
			MapFont mapFont = MinecraftFont.Font;
			String font = config.getString( "displays." + id + ".font" );
			if ( font != null ) {
				mapFont = Cartographer.getMain().getFont( font );
			}
			int x = config.getInt( "displays." + id + ".x" );
			int y = config.getInt( "displays." + id + ".y" );
			final MapFont finalFont = mapFont;
			TextSelector selector = new TextSelector() {
				@Override
				public List< MapText > getText( Player player ) {
					List< MapText > texts = new ArrayList< MapText >();
					if ( unloaded ) return texts;
					texts.add( new MapText( DependencyManager.parse( player, message ), x, y, finalFont ) );
					return texts;
				}
			};
			selectors.put( id, selector );
			map.registerTextSelector( selector );
		}
	}
}