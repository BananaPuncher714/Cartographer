package io.github.bananapuncher714.cartographer.api;

import org.bukkit.map.MapFont;

import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.objects.ZoomAction;

public interface CartographerPlugin {
	public int getMapLoadSpeed();
	public int getSelectorLoadDelay();
	public int getMaxCursors();
	public MapFont getFont( String id );
	public byte[][] getMissingMap();
	public double getTpsThreshold();
	public boolean isBlacklisted( String world );
	public boolean isCircularZooming();
	public ZoomAction getZoomAction( boolean leftClick );
	public Minimap createNewMap( boolean register, String id );
	public Minimap createNewMap( boolean register, String id, String name );
	public MapCache getMapCache();
}
