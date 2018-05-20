package io.github.bananapuncher714.beacon;

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
import io.github.bananapuncher714.cartographer.util.RivenMath;

public class BeaconPixelShader implements PixelShader {
	BeaconAddon addon;
	
	public BeaconPixelShader( BeaconAddon addon ) {
		this.addon = addon;
	}
	
	@Override
	public List< MapPixel > getPixels( Player player, double centerX, double centerY, ZoomScale scale ) {
		List< MapPixel > pixels = new ArrayList< MapPixel >();
		
		for ( Beacon beacon : addon.getBeacons() ) {
			if ( !beacon.isVisible() && !beacon.isViewer( player ) ) {
				continue;
			}
			// Make sure you can see the beacon first
			Location center = beacon.getCenter();
			center.setY( player.getLocation().getY() );
			if ( center.getWorld() != player.getWorld() ) {
				continue;
			}
			if ( center.distance( player.getLocation() ) - beacon.getRadius() > scale.getBlocksPerPixel() * 64 ) {
				continue;
			}
			MapCursorLocation location = MapUtil.findRelCursorPosition( beacon.getCenter(), centerX, centerY, scale );
			double pixelX = ( location.getX() + 128.5 ) / 2;
			double pixelY = ( location.getY() + 128.5 ) / 2;
			Color beaconColor = beacon.getColor();
			for ( int i = ( int ) pixelX - 1; i < pixelX + 1; i++ ) {
				for ( int j = ( int ) pixelY - 1; j < pixelY + 1; j++ ) {
					pixels.add( new MapPixel( i, j, ( ( i + j ) % 2 == 0 ? beaconColor : getDarker( beaconColor ) ) ) );
				}
			}

			double range = beacon.getRange();
			for ( int i = 0; i < 360; i = i + 5 ) {
				float distanceX = RivenMath.cos( ( float ) Math.toRadians( i ) );
				float distanceZ = RivenMath.sin( ( float ) Math.toRadians( i ) );
				int px = ( int ) ( ( ( range ) * distanceX ) + .5 );
				int pz = ( int ) ( ( ( range ) * distanceZ ) + .5 );
				pixels.add( new MapPixel( ( int ) ( px + pixelX - 1 ), ( int ) ( pz + pixelY - 1 ), beaconColor ) );

				int px2 = ( int ) ( ( ( range - 1 ) * distanceX ) + .5 );
				int pz2 = ( int ) ( ( ( range - 1 ) * distanceZ ) + .5 );
				pixels.add( new MapPixel( ( int ) ( px2 + pixelX - 1 ), ( int ) ( pz2 + pixelY - 1 ), getDarker( beaconColor ) ) );

				int px3 = ( int ) ( ( ( range - 2 ) * distanceX ) + .5 );
				int pz3 = ( int ) ( ( ( range - 2 ) * distanceZ ) + .5 );
				pixels.add( new MapPixel( ( int ) ( px3 + pixelX - 1 ), ( int ) ( pz3 + pixelY - 1 ), new Color( beaconColor.getAlpha() / 2, beaconColor.getGreen() / 2, beaconColor.getBlue() / 2, 127 ) ) );
			}
		}
		return pixels;
	}
	
	private Color getDarker( Color color ) {
		return new Color( color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2, color.getAlpha() );
	}

	@Override
	public int getPriority() {
		return 50;
	}
}
