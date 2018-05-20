package io.github.bananapuncher714.cartographer.map;

import java.awt.Color;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.MapData;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.objects.MapPixel;
import io.github.bananapuncher714.cartographer.api.objects.MapText;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;

public class CartographerRenderer extends MapRenderer {
	ZoomScale scale;
	MapManager mapManager;
	
	public CartographerRenderer( boolean contextual, Cartographer p, ZoomScale s, int selectorDelay ) {
		scale = s;
		mapManager = MapManager.getInstance();
	}
	
	@Override
	public void render( MapView arg0, MapCanvas arg1, Player arg2 ) {
		Minimap bMap = mapManager.getPlayerMap( arg2 );

		if ( bMap == null ) {
			fillMap( arg1, Cartographer.getMain().getMissingMap() );
			int size = arg1.getCursors().size();
			for ( int index = 0; index < size; index++ ) {
				arg1.getCursors().removeCursor( arg1.getCursors().getCursor( 0 ) );
			}
			return;
		}
		MapData data = bMap.getProvider().getMap( arg2, scale );
		fillMap( arg1, data.getMap() );

		// CURSOR MANAGEMENT
		List< MapCursor > cursors = data.getMapCursors();
		MapCursorCollection mcc = arg1.getCursors();
		
		while ( mcc.size() < cursors.size() ) {
			mcc.addCursor( new MapCursor( ( byte ) 0, ( byte ) 0, ( byte ) 0, ( byte ) 0, true ) );
		}
		
		while ( mcc.size() > cursors.size() ) {
			mcc.removeCursor( mcc.getCursor( 0 ) );
		}
		
		for ( int index = 0; index < cursors.size(); index++ ) {
			MapCursor cursor = cursors.get( index );
			MapCursor currentCursor = mcc.getCursor( index );
			currentCursor.setVisible( true );
			currentCursor.setDirection( cursor.getDirection() );
			currentCursor.setType( cursor.getType() );
			currentCursor.setX( cursor.getX() );
			currentCursor.setY( cursor.getY() );
		}
		
		// TEXT DISPLAY MANAGEMENT
		for ( MapText component : data.getText() ) {
			String text = component.getText();
			text = text.replace( "%MAP_NAME%", bMap.getName() );
			text = text.replace( "%MAP_SCALE%", scale.name().toLowerCase() );
			arg1.drawText( component.getX(), component.getY(), component.getFont(), text );
		}
		
		// MAP PIXEL MANAGEMENT
		for ( MapPixel pixel : data.getMapPixels() ) {
			byte mapColor = arg1.getPixel( pixel.getX(), pixel.getZ() );
			Color color = MapPalette.getColor( mapColor );
			Color fin = ColorMixer.blend( color, pixel.getColor(), ( pixel.getColor().getAlpha() * 100 ) / 255 );
			arg1.setPixel( pixel.getX(), pixel.getZ(), MapPalette.matchColor( fin ) );
		}
	}

	private void fillMap( MapCanvas canvas, byte[][] missingMap ) {
		for ( int x = 0; x < 128; x++ ) {
			for ( int z = 0; z < 128; z++ ) {
				canvas.setPixel( x, z, missingMap[ x ][ z ] );
			}
		}		
	}

}
