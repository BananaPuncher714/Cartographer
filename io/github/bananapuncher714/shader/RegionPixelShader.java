package io.github.bananapuncher714.shader;

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

public class RegionPixelShader implements PixelShader {

	@Override
	public List< MapPixel > getPixels( Player player, double centerX, double centerY, ZoomScale scale ) {
		List< MapPixel > pixels = new ArrayList< MapPixel >();

		for ( MapRegion region : RegionManager.getInstance().getRegions() ) {
			if ( !region.isViewer( player ) ) {
				continue;
			}
			// Make sure you can see the region first
			Location center = region.getLower();
			center.setY( player.getLocation().getY() );
			if ( center.getWorld() != player.getWorld() ) {
				continue;
			}
			MapCursorLocation location = MapUtil.findRelCursorPosition( center, centerX, centerY, scale );
			int pixelX = ( int ) ( ( location.getRawX() + 128 ) / 2.0 + 1 );
			int pixelY = ( int ) ( ( location.getRawY() + 128 ) / 2.0 + 1 );
			Color regionColor = region.getRegionColor();
			
			for ( int x = 0; x < region.getX() / scale.getBlocksPerPixel(); x++ ) {
				for ( int y = 0; y < region.getY() / scale.getBlocksPerPixel(); y++ ) {
					int px = pixelX + x;
					int py = pixelY + y;
					if ( px < 128 && py < 128 && px > -1 && py > -1 ) {
						pixels.add( new MapPixel( px, py, regionColor ) );
					}
				}
			}
		}
		return pixels;
	}

	@Override
	public int getPriority() {
		return 100;
	}
}
