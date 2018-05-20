package io.github.bananapuncher714.cartographer.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.map.MapFont;

public class BananaFont extends MapFont {
	private String id;

	public BananaFont( FileConfiguration config ) {
		id = config.getString( "id" );
		if ( !config.contains( "characters" ) ) return;
		for ( String characterKey : config.getConfigurationSection( "characters" ).getKeys( false ) ) {
			switch ( characterKey ) {
			case "DOUBLE_QUOTE": characterKey = "\""; break;
			case "PERIOD": characterKey = "."; break;
			}
			int width = 0;
			boolean skip = false;
			List< Boolean > preprocess = new ArrayList< Boolean >();
			int height = 0;
			for ( String line : config.getStringList( "characters." + characterKey ) ) {
				if ( width == 0 ) {
					width = line.length();
				} else if ( width != line.length() ) {
					skip = true;
					break;
				}
				for ( int index = 0; index < line.length(); index++ ) {
					preprocess.add( line.charAt( index ) != ' ' );
				}
				height++;
			}
			if ( skip ) {
				continue;
			}
			boolean[] charMap = new boolean[ height * width ];
			for ( int idx = 0; idx < preprocess.size(); idx++ ) {
				charMap[ idx ] = preprocess.get( idx );
			}
			setChar( characterKey.charAt( 0 ), new CharacterSprite( width, height, charMap ) );
		}
		malleable = true;
	}
	
	public String getId() {
		return id;
	}
}
