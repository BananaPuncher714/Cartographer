package io.github.bananapuncher714.cartographer.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import io.github.bananapuncher714.cartographer.map.BananaFont;

public final class FontLoader {

	public static BananaFont loadFont( File file ) {
		return new BananaFont( YamlConfiguration.loadConfiguration( file ) );
	}
	
	public static Set< BananaFont > loadFonts( File dir ) {
		Set< BananaFont > fonts = new HashSet< BananaFont >();
		if ( !dir.exists() ) return fonts;
		for ( File file : dir.listFiles() ) {
			if ( file.isFile() ) {
				fonts.add( loadFont( file ) );
			}
		}
		return fonts;
	}
}
