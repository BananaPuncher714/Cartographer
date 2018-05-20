package io.github.bananapuncher714.citizens;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.citizens.inv.CitizensBananaManager;

public class CitizensAddon extends Module {
	private File datafolder;
	private boolean hidden, visible;
	private double range;
	private Type type;
	private CursorManager manager;
	private CitizensCursorSelector selector = new CitizensCursorSelector();
	private MapFont font;
	
	static {
		Bukkit.getPluginManager().registerEvents( new CitizensBananaManager(), Cartographer.getMain() );
	}
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		datafolder = dataFolder;
		dataFolder.mkdirs();
		File masterConfig = new File( Cartographer.getMain().getDataFolder() + "/configs/" + "citizens-config.yml" );
		File config = new File( dataFolder + "/" + "config.yml" );
		if ( !config.exists() ) {
			try {
			Files.copy( masterConfig, config );
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileUtil.updateConfigFromFile( config, Cartographer.getMain().getResource( "data/citizens/config.yml" ), true );
		loadConfig( config );
		manager = new CursorManager( this );
		
		map.registerCursorSelector( selector );
		map.registerTextSelector( new CitizensTextSelector() );
	}

	public File getDatafolder() {
		return datafolder;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isVisible() {
		return visible;
	}

	public double getRange() {
		return range;
	}

	public Type getType() {
		return type;
	}
	
	public CursorManager getManager() {
		return manager;
	}
	
	public MapFont getFont() {
		return font;
	}

	@Override
	public void unload() {
		selector.stop();
		manager.saveCursors();
	}
	
	private void loadConfig( File file ) {
		FileConfiguration save = YamlConfiguration.loadConfiguration( file );
		type = FailSafe.getEnum( Type.class, save.getString( "default.icon" ) );
		hidden = !save.getBoolean( "default.highlighted" );
		visible = save.getBoolean( "default.visible" );
		range = save.getDouble( "default.range" );
		String font = save.getString( "hover-font" );
		if ( Cartographer.getMain().getFont( font ) == null ) {
			this.font = MinecraftFont.Font;
		} else {
			this.font = Cartographer.getMain().getFont( font );
		}
	}
}
