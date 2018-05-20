package io.github.bananapuncher714.locale;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.DependencyManager;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.locale.inv.LocaleBananaManager;

public class LocaleAddon {
	File dataFolder;
	boolean usePlaceholders;

	private Map< String, Locale > locales;
	private HashMap< UUID, Locale > playerLocales = new HashMap< UUID, Locale >();

	private Locale defLocale;

	public void load(File dataFolder) {
		this.dataFolder = dataFolder;
		Bukkit.getPluginManager().registerEvents( new LocaleBananaManager(), Cartographer.getMain() );

		File config = new File( dataFolder, "config.yml" );
		FileUtil.saveToFile( Cartographer.getMain().getResource( "data/locale/config.yml" ), config, false );
		setDefLocale();
		updateConfig( config );
		loadConfig( config );
	}

	public void loadConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		usePlaceholders = config.getBoolean( "use-placeholders" ) ;
		String defLang = config.getString( "default-locale" );
		if ( locales.containsKey( defLang ) ) defLocale = locales.get( defLang );
		else defLocale = Locale.getDefault();
	}

	private void updateConfig( File file ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		config.set( "replace-color", null );
		try {
			config.save( file );
		} catch ( Exception exception ) {

		}
	}

	private void setDefLocale() {
		File defLocFile = new File( dataFolder + "/locales", "default_lang.yml" );
		FileUtil.saveToFile( Cartographer.getMain().getResource( "data/locale/default_lang.yml" ), defLocFile, false );
		String version = YamlConfiguration.loadConfiguration( new InputStreamReader( Cartographer.getMain().getResource( "data/locale/default_lang.yml" ) ) ).getString( "version" );
		FileConfiguration locFileConfig = YamlConfiguration.loadConfiguration( defLocFile );
		if ( !version.equalsIgnoreCase( locFileConfig.getString( "version" ) ) ) {
			defLocFile.delete();
			FileUtil.saveToFile( Cartographer.getMain().getResource( "data/locale/default_lang.yml" ), defLocFile, false );
		}
		locales = LocaleLoader.loadLocales( new File( dataFolder + "/locales" ) );
		Locale defaultLocale = LocaleLoader.loadLocale( new File( dataFolder + "/locales", "default_lang.yml" ) );
		Locale.setDefaultLocale( defaultLocale );
		locales.put( defaultLocale.getId(), defaultLocale );
	}

	public void unload() {
		for ( UUID uuid : playerLocales.keySet() ) {
			LocaleLoader.savePreference( uuid, playerLocales.get( uuid ), new File( dataFolder, "preferences.yml" ) );
		}
	}

	public Locale getLocale( Player player ) {
		if ( playerLocales.containsKey( player.getUniqueId() ) ) {
			return playerLocales.get( player.getUniqueId() );
		} else {
			String preference = LocaleLoader.loadPreference( player.getUniqueId(), new File( dataFolder, "preferences.yml" ) );
			if ( preference == null ) return defLocale;
			Locale locale = locales.get( preference );
			if ( locale == null ) return defLocale;
			playerLocales.put( player.getUniqueId(), locale );
			return locale;
		}
	}

	public String parse( CommandSender player, String identifier, Object... paramStrings ) {
		Locale locale = player instanceof Player ? getLocale( ( Player ) player ) : defLocale;
		String message = locale.parse( identifier, paramStrings );
		if ( usePlaceholders && player instanceof Player ) message = DependencyManager.parse( ( Player ) player, message );
		if ( locale.useColor() ) message = message.replaceAll( "&", "\u00a7" );
		return message;
	}

	public void setPreference( Player player, Locale locale ) {
		playerLocales.put( player.getUniqueId(), locale );
	}

	public void setPreference( Player player, String id ) {
		playerLocales.put( player.getUniqueId(), locales.containsKey( id ) ? locales.get( id ) : defLocale );
	}

	public Map< String, Locale > getLocales() {
		return locales;
	}

}
