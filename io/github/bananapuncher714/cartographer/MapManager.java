/**
 * Manages maps and CursorSelectors for the CustomRenderer to use
 * 
 *  @author BananaPuncher714
 */

package io.github.bananapuncher714.cartographer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import io.github.bananapuncher714.cartographer.api.MapCache;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.map.CartographerRenderer;
import io.github.bananapuncher714.cartographer.map.core.BorderedMap;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.MapLoader;

/**
 * A Singleton class to manage all maps
 * 
 * @author BananaPuncher714
 */
public class MapManager implements Listener, MapCache {
	private static MapManager instance;
	
	private Map< UUID, byte[][] > overlays = new HashMap< UUID, byte[][] >();
	private Map< UUID, Minimap > registeredMaps = new HashMap< UUID, Minimap >();
	private Map< UUID, UUID > playerMaps = new HashMap< UUID, UUID >();
	private Map< Short, ZoomScale > scales = new TreeMap< Short, ZoomScale >();
	
	private MapManager() {
		for ( BorderedMap map : MapLoader.getMaps( new File( Cartographer.getMain().getDataFolder() + "/" + Cartographer.MAP_SAVE_FOLDER + "/" ) ) ) {
			CLogger.info( "Loaded map " + map.getId() );
			registerMinimap( map );
		}
	}
	
	public void disable() {
		for ( Minimap map : registeredMaps.values() ) {
			if ( map instanceof BorderedMap ) {
				( ( BorderedMap ) map ).disable();
			}
		}
	}
	
	public void stop( UUID uuid ) {
		Minimap map = registeredMaps.get( uuid );
		if ( map instanceof BorderedMap ) {
			( ( BorderedMap ) map ).disable();
		}
		registeredMaps.remove( uuid );
	}

	@Override
	public MapView getMapView( Minimap bMap, MapView map, boolean zoom, boolean refresh ) {
		// Get mapview
		MapView mv = map;
		// Clear the renderers
		for ( MapRenderer r : mv.getRenderers() ) {
			mv.removeRenderer( r );
		}
		
		Scale s = mv.getScale();
		if ( isValidScale( bMap, s ) ) {
			int maxScale = bMap.isInfinite() ? 2048 : Math.min( bMap.getWidth(), bMap.getHeight() );
			if ( refresh ) {
				
			} else if ( s.equals( Scale.CLOSEST ) ) {
				if ( !zoom && maxScale >= 256 ) mv.setScale( Scale.CLOSE );
			} else if ( s.equals( Scale.CLOSE ) ) {
				if ( zoom ) mv.setScale( Scale.CLOSEST );
				else if ( maxScale >= 512 ) mv.setScale( Scale.NORMAL );
			} else if ( s.equals( Scale.NORMAL ) ) {
				if ( zoom ) mv.setScale( Scale.CLOSE );
				else if ( maxScale >= 1024 ) mv.setScale( Scale.FAR );
			} else if ( s.equals( Scale.FAR ) ) {
				if ( zoom ) mv.setScale( Scale.NORMAL );
				else if ( maxScale >= 2048 ) mv.setScale( Scale.FARTHEST );
			} else if ( s.equals( Scale.FARTHEST )  ) {
				if ( zoom ) mv.setScale( Scale.FAR );
			} else {
				mv.setScale( Scale.CLOSEST );
			}
		} else {
			mv.setScale( getHighestScale( null ) );
		}
		
		ZoomScale zs = ZoomScale.ONE;
		if ( bMap != null ) {
			if ( !scales.containsKey( mv.getId() ) ) {
				scales.put( mv.getId(), ZoomScale.getScaleFromBukkit( mv.getScale() ) );
			}
			zs = scales.get( mv.getId() );
			ZoomScale oldZoom, prevScale = zs;
			// Ensures that the scale is never going to be an invalid one, and it always zooms at least once
			do {
				oldZoom = zs;
				if ( zoom ) {
					zs = zs.getLower( Cartographer.getMain().isCircularZooming() );
				} else {
					zs = zs.getHigher( Cartographer.getMain().isCircularZooming() );
				}
			} while ( oldZoom != zs && ( !bMap.isZoomAllowed( zs ) || !isValidZoomScale( bMap, zs ) ) );
			if ( !bMap.isZoomAllowed( zs ) || !isValidZoomScale( bMap, zs ) ) {
				zs = prevScale;
			}
			scales.put( mv.getId(), zs );
		}
		// And add the custom renderer
		mv.addRenderer( new CartographerRenderer( true, Cartographer.getMain(), zs, Cartographer.getMain().getSelectorLoadDelay() ) );
		return mv;
	}
	
	@Override
	public MapView getMapView( Minimap bMap, MapView map, boolean zoom ) {
		return getMapView( bMap, map, zoom, false );
	}
	
	@Override
	public MapView getMapView( ItemStack map, boolean zoom ) {
		// Get mapview
		short d = map.getDurability();
		MapView mv = Bukkit.getMap( d );
		return getMapView( null, mv, zoom );
	}
	
	@Override
	public Scale getHighestScale( Minimap map ) {
		if ( map == null ) return Scale.CLOSEST;
		int maxScale = map.isInfinite() ? 2048 : Math.min( map.getWidth(), map.getHeight() );
		if ( maxScale >= 2048 ) return Scale.FARTHEST;
		else if ( maxScale >= 1024 ) return Scale.FAR;
		else if ( maxScale >= 512 ) return Scale.NORMAL;
		else if ( maxScale >= 256 ) return Scale.CLOSE;
		else if ( maxScale >= 128 ) return Scale.CLOSEST;
		else return Scale.CLOSEST;
	}
	
	@Override
	public ZoomScale getHighestZoomScale( Minimap map ) {
		if ( map == null ) {
			return null;
		}
		ZoomScale maxScale = ZoomScale.SIXTY_FOURTH;
		for ( ZoomScale scale : ZoomScale.values() ) {
			if ( isValidZoomScale( map, scale ) && scale.getBlocksPerPixel() > maxScale.getBlocksPerPixel() ) {
				maxScale = scale;
			}
		}
		return maxScale;
	}
	
	@Override
	public boolean isValidZoomScale( Minimap map, ZoomScale scale ) {
		return map == null ? false : ( map.isInfinite() ) || scale.getBlocksPerPixel() * 128 <= Math.min( map.getWidth(), map.getHeight() );
	}
	
	@Override
	public boolean isValidScale( Minimap map, Scale scale ) {
		if ( map == null ) return false;
		int maxScale = map.isInfinite() ? 2048 : Math.min( map.getWidth(), map.getHeight() );
		if ( scale == Scale.CLOSEST && maxScale < 128 ) return false;
		else if ( scale == Scale.CLOSE & maxScale < 256 ) return false;
		else if ( scale == Scale.NORMAL & maxScale < 512 ) return false;
		else if ( scale == Scale.FAR & maxScale < 1024 ) return false;
		else if ( scale == Scale.FARTHEST & maxScale < 2048) return false;
		return true;
	}
	
	@Override
	public void registerMinimap( Minimap map ) {
		registeredMaps.put( map.getUID(),  map );
	}
	
	@Override
	public Map< UUID, Minimap > getMinimaps() {
		return registeredMaps;
	}
	
	@Override
	public Minimap getMinimap( UUID uuid ) {
		return registeredMaps.get( uuid );
	}
	
	@Override
	public Minimap getMinimap( String id ) {
		for ( Minimap map : registeredMaps.values() ) {
			if ( map.getId().equals( id ) ) {
				return map;
			}
		}
		return null;
	}
	
	@Override
	public Minimap getPlayerMap( Player player ) {
		if ( playerMaps.containsKey( player.getUniqueId() ) ) {
			if ( !registeredMaps.containsKey( playerMaps.get( player.getUniqueId() ) ) ) {
				playerMaps.remove( player.getUniqueId() );
				return null;
			}
			return registeredMaps.get( playerMaps.get( player.getUniqueId() ) );
		} else {
			return null;
		}
	}
	
	@Override
	public void setPlayerMap( Player player, Minimap map ) {
		if ( map == null ) {
			playerMaps.remove( player.getUniqueId() );
		} else {
			playerMaps.put( player.getUniqueId(), map.getUID() );
		}
	}
	
	@Override
	public byte[][] getOverlayFor( Player player ) {
		return overlays.get( player.getUniqueId() );
	}
	
	@Override
	public void setOverlayForPlayer( Player player, byte[][] overlay ) {
		if ( overlay == null ) {
			if ( overlays.containsKey( player.getUniqueId() ) ) {
				overlays.remove( player.getUniqueId() );
			}
		} else {
			overlays.put( player.getUniqueId(), overlay );
		}
	}
	
	public static MapManager getInstance() {
		if ( instance == null ) {
			instance = new MapManager();
		}
		return instance;
	}
}
