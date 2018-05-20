package io.github.bananapuncher714.cartographer.map.interactive;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;

public class MapViewer {
	private static final Map< UUID, MapViewer > viewers = new HashMap< UUID, MapViewer >();
	private static final Set< ChunkLocation > discovered = new HashSet< ChunkLocation >();
	
	boolean cursorOn;
	double x, y;
	private UUID uuid;
	
	private MapViewer( UUID uuid ) {
		cursorOn = true;
		this.uuid = uuid;
		x = 0;
		y = 0;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX( double x ) {
		this.x = x;
	}
	
	public void setY( double y ) {
		this.y = y;
	}
	
	public boolean getState() {
		return cursorOn;
	}

	public void setState( boolean state ) {
		cursorOn = state;
	}
	
	public boolean isDiscovered( ChunkLocation chunk ) {
		return discovered.contains( chunk );
	}
	
	public void discover( ChunkLocation location ) {
		discovered.add( location );
	}
	
	public boolean undiscover( ChunkLocation location ) {
		if ( discovered.contains( location ) ) {
			discovered.remove( location );
			return true;
		}
		return false;
	}
	
	public Set< ChunkLocation > getDiscovered() {
		return discovered;
	}
	
	public UUID getUUID() {
		return uuid;
	}

	public static Collection< MapViewer > getViewers() {
		return viewers.values();
	}
	
	public static MapViewer getMapViewer( UUID uuid ) {
		if ( viewers.containsKey( uuid ) ) {
			return viewers.get( uuid );
		} else {
			MapViewer viewer = new MapViewer( uuid );
			viewers.put( uuid, viewer );
			return viewer;
		}
	}
}
