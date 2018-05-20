package io.github.bananapuncher714.cartographer.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.addon.PixelShader;
import io.github.bananapuncher714.cartographer.api.objects.MapCursorLocation;
import io.github.bananapuncher714.cartographer.api.objects.MapDirection;
import io.github.bananapuncher714.cartographer.api.objects.MapPixel;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.api.util.MapUtil;

/**
 * This shows different pixels per player; May not necessarily have to do with a real Location
 * 
 * @author BananaPuncher714
 */
public class ExamplePixelShader implements PixelShader {
	
	@Override
	public List< MapPixel > getPixels( Player player, double centerX, double centerY, ZoomScale scale ) {
		List< MapPixel > pixels = new ArrayList< MapPixel >();
		
		// This gives you the coords on the map in -128 to 126
		MapCursorLocation coords = MapUtil.findRelCursorPosition( player.getLocation(), centerX, centerY, scale );
		// We must convert it to 0 to 127
		int pixelX = ( int ) ( coords.getX() + 128.5 ) / 2;
		int pixelY = ( int ) ( coords.getY() + 128.5 ) / 2;
		
		if ( coords.getDirection() != MapDirection.CENTER )  {
			// The pixel is off the map, so you may not want to show it
		}
		Color color = null;
		
		MapPixel pixel = new MapPixel( pixelX, pixelY, color );
		pixels.add( pixel );
		
		return pixels;
	}

	@Override
	public int getPriority() {
		return 50;
	}
}
