package io.github.bananapuncher714.cartographer.api.util;

import org.bukkit.Location;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import io.github.bananapuncher714.cartographer.api.objects.MapCursorLocation;
import io.github.bananapuncher714.cartographer.api.objects.MapDirection;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.map.CartographerRenderer;

public final class MapUtil {
	
	public static MapCursorLocation findRelCursorPosition( Location playerLoc, double mapX, double mapZ, ZoomScale scale ) {
		double fx = playerLoc.getX() - mapX;
		double fb = playerLoc.getZ() - mapZ;
		double size = scale.getBlocksPerPixel();
		double finalx = 128.0 * ( fx / ( size * 64.0 ) );
		double finaly = 128.0 * ( fb / ( size * 64.0 ) );
		
		MapDirection direction = MapDirection.CENTER;
		if ( finalx > 126 ) {
			direction = MapDirection.EAST;
		}
		else if ( finalx < -128 ) {
			direction = MapDirection.WEST;
		}
		if ( finaly > 126 ) {
			if ( direction == MapDirection.EAST ) {
				direction = MapDirection.SOUTH_EAST;
			} else if ( direction == MapDirection.WEST ) {
				direction = MapDirection.SOUTH_WEST;
			} else {
				direction = MapDirection.SOUTH;
			}
		}
		else if ( finaly < -128 ) {
			if ( direction == MapDirection.EAST ) {
				direction = MapDirection.NORTH_EAST;
			} else if ( direction == MapDirection.WEST ) {
				direction = MapDirection.NORTH_WEST;
			} else {
				direction = MapDirection.NORTH;
			}
		}
		return new MapCursorLocation( finalx, finaly, direction );
	}
	
	public static int getScaleSize( Scale scale ) {
		if ( scale.equals( Scale.CLOSEST ) ) return 1;
		else if ( scale.equals( Scale.CLOSE ) ) return 2;
		else if ( scale.equals( Scale.NORMAL ) ) return 4;
		else if ( scale.equals( Scale.FAR ) ) return 8;
		else if ( scale.equals( Scale.FARTHEST ) ) return 16;
		return 0;
	}
	
	public static int getDirection( double degree ) {
		return ( int ) Math.min( 15, Math.max( 0, ( ( ( degree + 371.25 ) % 360 ) / 22.5 ) ) );
	}
	
	public static boolean isDefaultMap( MapView view ) {
		for ( MapRenderer renderer : view.getRenderers() ) {
			if ( !renderer.getClass().getSimpleName().equalsIgnoreCase( "CraftMapRenderer" ) ) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isCartographerMap( MapView view ) {
		for ( MapRenderer renderer : view.getRenderers() ) {
			if ( renderer.getClass().getName().equalsIgnoreCase( CartographerRenderer.class.getName() ) ) {
				return true;
			}
		}
		return false;
	}
}
