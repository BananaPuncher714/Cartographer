package io.github.bananapuncher714.locale;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.cartographer.message.CLogger;

public final class LocaleLoader {
	
	/**
	 * Loads all the locale files from the directory and returns a set of them
	 * 
	 * @param directory
	 * The directory to load them from
	 */
	public static Map< String, Locale > loadLocales( File directory ) {
		Map< String, Locale > locales = new HashMap< String, Locale >();
		if ( !directory.exists() || !directory.isDirectory() ) return locales;
		for ( File file : directory.listFiles() ) {
			Locale locale = loadLocale( file );
			if ( locale != null ) {
				locales.put( locale.getId(), locale );
				CLogger.info( "Loaded " + locale.getName() + "(" + locale.getId() + ") successfully!" );
			}
		}
		return locales;
	}
	
	public static Locale loadLocale( File localeFile ) {
		if ( !localeFile.exists() || !localeFile.isFile() ) return null;
		FileConfiguration locale = YamlConfiguration.loadConfiguration( localeFile );
		return new Locale( locale );
	}
	
	public static void savePreference( UUID uuid, Locale locale, File file ) {
		file.getParentFile().mkdirs();
		if ( !file.exists() ) {
			try {
				file.createNewFile();
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		config.set( uuid.toString(), locale.getId() );
		try {
			config.save( file );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public static String loadPreference( UUID uuid, File file ) {
		if ( !file.exists() ) return null;
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		return config.getString( uuid.toString() );
	}
}
