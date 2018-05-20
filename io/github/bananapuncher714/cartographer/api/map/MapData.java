package io.github.bananapuncher714.cartographer.api.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;

import io.github.bananapuncher714.cartographer.api.objects.MapPixel;
import io.github.bananapuncher714.cartographer.api.objects.MapText;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;

public class MapData {
	private byte[][] map;
	private ZoomScale scale;
	private Player player;
	private List< MapCursor > cursors = new ArrayList< MapCursor >();
	private List< MapText > texts = new ArrayList< MapText >();
	private List< MapPixel > pixels = new ArrayList< MapPixel >();
	
	public MapData( byte[][] map, ZoomScale scale, Player player ) {
		this.map = map;
		this.scale = scale;
		this.player = player;
	}

	public byte[][] getMap() {
		return map;
	}

	public ZoomScale getScale() {
		return scale;
	}

	public Player getPlayer() {
		return player;
	}

	public void addCursor( MapCursor cursor ) {
		cursors.add( cursor );
	}
	
	public List< MapCursor > getMapCursors() {
		return cursors;
	}
	
	public void addMapText( MapText text ) {
		texts.add( text );
	}
	
	public List< MapText > getText() {
		return texts;
	}
	
	public void addMapPixel( MapPixel pixel ) {
		pixels.add( pixel );
	}
	
	public List< MapPixel > getMapPixels() {
		return pixels;
	}
}
