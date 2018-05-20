package io.github.bananapuncher714.worldborder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;

public class WorldBorderCursorSelector implements CursorSelector {
	private Type c, b;
	private double d;
	private WorldBorderAddon addon;
	
	WorldBorderCursorSelector( WorldBorderAddon addon, Type center, Type border, double dist ) {
		this.addon = addon;
		c = center;
		b = border;
		d = dist;
	}

	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		List< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
			return cursors;
		}
		WorldBorder border = map.getWorld().getWorldBorder();
		
		if ( border == null ) return cursors;
		
		if ( addon.isBorder() ) {
			double size = border.getSize();
			Location center = border.getCenter().clone().subtract( size / 2, 0, size / 2 );
			for ( double i = 0; i < size; i = i + d ) {
				cursors.add( new RealWorldCursor( center.clone().add( i, 0, 0 ), b, true ) );
				cursors.add( new RealWorldCursor( center.clone().add( 0, 0, i ), b, true ) );
				cursors.add( new RealWorldCursor( center.clone().add( i, 0, size ), b, true ) );
				cursors.add( new RealWorldCursor( center.clone().add( size, 0, i ), b, true ) );
			}
		}
		
		if ( addon.isShowCenter() ) {
			cursors.add( new RealWorldCursor( border.getCenter(), c,  addon.isHighlight() ) );
		}
		
		return cursors;
	}

}
