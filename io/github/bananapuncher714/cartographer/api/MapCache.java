package io.github.bananapuncher714.cartographer.api;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;

public interface MapCache {
	public MapView getMapView( Minimap bMap, MapView map, boolean zoom, boolean refresh );
	public MapView getMapView( Minimap bMap, MapView map, boolean zoom );
	public MapView getMapView( ItemStack map, boolean zoom );
	public Scale getHighestScale( Minimap map );
	public ZoomScale getHighestZoomScale( Minimap map );
	public boolean isValidZoomScale( Minimap map, ZoomScale scale );
	public boolean isValidScale( Minimap map, Scale scale );
	public void registerMinimap( Minimap map );
	public Map< UUID, Minimap > getMinimaps();
	public Minimap getMinimap( UUID uuid );
	public Minimap getMinimap( String id );
	public Minimap getPlayerMap( Player player );
	public void setPlayerMap( Player player, Minimap map );
	public byte[][] getOverlayFor( Player player );
	public void setOverlayForPlayer( Player player, byte[][] overlay );
}

