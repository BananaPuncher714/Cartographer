package io.github.bananapuncher714.citizens;

import java.io.File;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor.Type;

public class CitizensFileManager {
	
	public static void saveCursor( File dataFolder, UUID uuid, CitizenCursor cursor ) {
		File file = new File( dataFolder + "/" + "saves", uuid.toString() );
		file.getParentFile().mkdirs();
		if ( !file.exists() ) {
			try {
				file.createNewFile();
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileConfiguration save = YamlConfiguration.loadConfiguration( file );
		save.set( "type", cursor.getType().name() );
		save.set( "hidden", cursor.isHidden() );
		save.set( "visible", cursor.isVisible() );
		save.set( "range", cursor.getRange() );
		try {
			save.save( file );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	public static CitizenCursor loadCursor( File dataFolder, UUID uuid ) {
		File file = new File( dataFolder + "/" + "saves", uuid.toString() );
		if ( !file.exists() ) return null;
		FileConfiguration save = YamlConfiguration.loadConfiguration( file );
		Type type;
		if ( save.getString( "type" ) != null ) {
			type = Type.valueOf( save.getString( "type" ) );
		} else {
			type = Type.BLUE_POINTER;
		}
		
		if ( type == null ) type = Type.BLUE_POINTER;
		boolean hidden = save.getBoolean( "hidden" );
		boolean visible = save.getBoolean( "visible" );
		double range = save.getDouble( "range" );
		return new CitizenCursor( type, visible, hidden, range );
	}
}
