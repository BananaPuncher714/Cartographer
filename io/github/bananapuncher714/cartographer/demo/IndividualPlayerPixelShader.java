package io.github.bananapuncher714.cartographer.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.addon.PixelShader;
import io.github.bananapuncher714.cartographer.api.objects.MapCursorLocation;
import io.github.bananapuncher714.cartographer.api.objects.MapPixel;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.api.util.MapUtil;

public class IndividualPlayerPixelShader implements PixelShader {

	@Override
	public List< MapPixel > getPixels(Player player, double centerX, double centerY, ZoomScale scale) {
		Location center = null;
		Color regionColor = null;
		int width = 16;
		int height = 16;
		
		List< MapPixel > pixels = new ArrayList< MapPixel >();
		center.setY( player.getLocation().getY() );
		if ( center.getWorld() != player.getWorld() ) {
			return pixels;
		}
		MapCursorLocation location = MapUtil.findRelCursorPosition( center, centerX, centerY, scale );
		int pixelX = ( int ) ( location.getRawX() + 128.5 ) / 2;
		int pixelY = ( int ) ( location.getRawY() + 128.5 ) / 2;
		
		for ( int x = 0; x < width / scale.getBlocksPerPixel(); x++ ) {
			for ( int y = 0; y < height / scale.getBlocksPerPixel(); y++ ) {
				int px = pixelX + x;
				int py = pixelY + y;
				if ( px < 128 && py < 128 && px > -1 && py > -1 ) {
					pixels.add( new MapPixel( pixelX + x, pixelY + y, regionColor ) );
				}
			}
		}
		return pixels;
	}

	@Override
	public int getPriority() {
		return 50;
	}
}