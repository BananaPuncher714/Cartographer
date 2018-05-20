/**
 * Edits simple colors.
 * 
 * @author BananaPuncher714
 */
package io.github.bananapuncher714.cartographer.api.util;

import java.awt.Color;

public class ColorMixer {
	
	/**
	 * Tint types for tinting a color.
	 * 
	 * @author BananaPuncher714
	 */
	public enum Tint {
		RED, GREEN, BLUE, YELLOW;
	}
	
	/**
	 * Brightens a given color for a percent; Negative values darken the color.
	 * 
	 * @param c
	 * The color to brighten.
	 * @param percent
	 * The percentage to brighten; Must not exceed 100 percent.
	 * @return
	 * The new brightened color.
	 */
	public static Color brightenColor( Color c, int percent ) {
		if ( percent == 0 ) return c;
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		if ( percent > 0 ) {
			int newr = r + percent * ( 255 - r ) / 100;
			int newg = g + percent * ( 255 - g ) / 100;
			int newb = b + percent * ( 255 - b ) / 100;
			return new Color( newr, newg, newb );
		}
		int newr = r + percent * r / 100;
		int newg = g + percent * g / 100;
		int newb = b + percent * b / 100;
		return new Color( newr, newg, newb );
	}

	/**
	 * Tints a given color with a certain color and percent.
	 * 
	 * @param type
	 * The tint that will be applied.
	 * @param c
	 * The color to tint.
	 * @param percent
	 * The percentage to tint the given color; Must not exceed 100 percent.
	 * @return
	 * The newly tinted color.
	 */
	public static Color tintColor( Tint type, Color c, int percent) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		int newr = r, newg = g, newb = b;
		if ( type == Tint.YELLOW || type == Tint.RED ) newr = r + percent * ( 255 - r ) / 100;
		if ( type == Tint.YELLOW || type == Tint.GREEN ) newg = g + percent * ( 255 - g ) / 100;
		if ( type == Tint.BLUE ) newb = b + percent * ( 255 - b ) / 100;
		return new Color( newr, newg, newb );
	}
	
	/**
	 * Blends 2 colors together
	 * 
	 * @param c0
	 * The base color
	 * @param c1
	 * The color to shade
	 * @param percent
	 * The percent you want to shade c0 with c1
	 * @return
	 * The new shaded color
	 */
	public static Color blend( Color c0, Color c1, double percent ) {
		int r = c0.getRed();
		int g = c0.getGreen();
		int b = c0.getBlue();

		int newr = r, newg = g, newb = b;
		newr = ( int ) ( r + percent * ( c1.getRed() - r ) / 100 );
		newg = ( int ) ( g + percent * ( c1.getGreen() - g ) / 100 );
		newb = ( int ) ( b + percent * ( c1.getBlue() - b ) / 100 );
		
		return new Color( newr, newg, newb );
	}
	
	public static Color blendARGB( Color c1, Color c2 ){
	    int r1 = c1.getRed();
	    int g1 = c1.getGreen();
	    int b1 = c1.getBlue();

	    int a2 = c2.getAlpha();
	    int r2 = c2.getRed();
	    int g2 = c2.getGreen();
	    int b2 = c2.getBlue();

	    double percent = a2 / 255.0;
	    
	    int r = ( int ) ( ( r1 + ( r2 * percent ) ) / 2);
	    int g = ( int ) ( ( g1 + ( g2 * percent ) ) / 2);
	    int b = ( int ) ( ( b1 + ( b2 * percent ) ) / 2);
	    
	    return new Color( r, g, b );
	}
}
