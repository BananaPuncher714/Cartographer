package io.github.bananapuncher714.locale;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public class Locale {
	private static Locale defLocale;
	
	String name;
	String id;
	boolean useColor = false;
	Map< String, String > messages; 
	
	public Locale( String id, String name, boolean color, Map< String, String > messages ) {
		this.name = name;
		this.id = id;
		this.messages = messages;
		useColor = color;
	}
	
	public Locale( FileConfiguration config ) {
		name = config.getString( "name" ).replaceAll( "&", "\u00a7" );
		id = config.getString( "id" );
		useColor = config.getBoolean( "translate-color" );
		messages = new HashMap< String, String >();
		for ( String key : config.getConfigurationSection( "messages" ).getKeys( true ) ) {
			messages.put( key, config.getString( "messages." + key ) );
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean useColor() {
		return useColor;
	}
	
	public String parse( String identifier, Object... paramStrings ) {
		if ( messages.containsKey( identifier ) ) {
			String msg = messages.get( identifier );
			for ( int index = 0; index < paramStrings.length; index++ ) {
				msg = msg.replaceAll( "%" + index, paramStrings[ index ].toString() );
			}
			return msg;
		} else {
			if ( this == defLocale ) return "";
			return defLocale.parse( identifier, paramStrings );
		}
	}
	
	public static Locale getDefault() {
		return defLocale;
	}
	
	public static void setDefaultLocale( Locale locale ) {
		defLocale = locale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Locale other = (Locale) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
