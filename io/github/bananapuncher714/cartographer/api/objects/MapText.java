package io.github.bananapuncher714.cartographer.api.objects;

import java.awt.Color;

import org.bukkit.map.MapFont;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MinecraftFont;

import io.github.bananapuncher714.cartographer.api.util.ImageUtil;

/**
 * A wrapper to hold text at a certain position on the map per player
 * 
 * @author BananaPuncher714
 */
public class MapText {
	final MapFont font;
	final String text;
	final int x, y;
	
	public MapText( String text, int x, int y ) {
		this( text, x, y, new MinecraftFont() );
	}

	public MapText( String text, int x, int y, MapFont font ) {
		this.font = font;
		this.text = text;
		this.x = Math.min( 127, Math.max( 0, x ) );
		this.y = Math.min( 127, Math.max( 0, y ) );
	}
	
	public String getText() {
		return text;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public MapFont getFont() {
		return font;
	}
	
	public static String textColor( Color color ) {
		StringBuilder colorBuilder = new StringBuilder();
		colorBuilder.append( "\u00a7" );
		Color bestMatch = ImageUtil.getBestMapColor( color );
		colorBuilder.append( MapPalette.matchColor( bestMatch ) );
		colorBuilder.append( ";" );
		return colorBuilder.toString();
	}

}
