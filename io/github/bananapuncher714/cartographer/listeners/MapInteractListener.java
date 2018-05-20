package io.github.bananapuncher714.cartographer.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.DependencyManager;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.events.CartographerInitializeMapEvent;
import io.github.bananapuncher714.cartographer.api.events.MapZoomEvent;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.objects.ZoomAction;
import io.github.bananapuncher714.cartographer.api.util.MapUtil;
import io.github.bananapuncher714.cartographer.util.NBTEditor;
import io.github.bananapuncher714.cartographer.util.ReflectionUtils;

public class MapInteractListener implements Listener {
	private static final Set< Short > MAP_IDS = new HashSet< Short >();

	@EventHandler
	public void onPlayerInteractEvent( PlayerInteractEvent e ) {
		// Must change that for 1.8/1.11
		if ( !e.getPlayer().hasPermission( "cartographer.map.use" ) ) return;
		Action a = e.getAction();
		if ( !ReflectionUtils.isMainHand( e ) ) return;
		if ( !a.equals( Action.RIGHT_CLICK_BLOCK ) && !a.equals( Action.LEFT_CLICK_BLOCK ) && !a.equals( Action.RIGHT_CLICK_AIR ) && !a.equals( Action.LEFT_CLICK_AIR ) ) return;
		if ( e.getPlayer().getInventory().getItemInHand().getType() != Material.MAP ) {
			return;
		}
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInHand();
		short mapId = item.getDurability();
		short oldMapId = mapId;
		MapView mv = Bukkit.getMap( oldMapId );
		
		if ( Cartographer.getMain().isBlacklisted( p.getWorld().getName() ) ) {
			return;
		}
		
		if ( !( MapUtil.isDefaultMap( mv ) || MapUtil.isCartographerMap( mv ) ) ) {
			return;
		}
		
		String value = ( String ) NBTEditor.getItemTag( item, "display", "LocName" );
		if ( value != null && ( value.equalsIgnoreCase( "filled_map.monument" ) || value.equalsIgnoreCase( "filled_map.mansion" ) ) ) {
			return;
		}
		
		e.setCancelled( true );
		Minimap map = MapManager.getInstance().getPlayerMap( p );
		if ( map == null ) {
			return;
		}
		if ( map.getCenterX() == 0 && map.getCenterY() == 0 ) return;
		ZoomAction action = Cartographer.getMain().getZoomAction( a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK );
		if ( action == ZoomAction.NOTHING ) {
			return;
		}
		boolean zoom = action == ZoomAction.ZOOM_IN;
		if ( Cartographer.getMain().getConfig().getBoolean( "map-bug" ) ) {
			MapView newView = Bukkit.createMap( map.getWorld() );
			p.getInventory().getItemInHand().setDurability( newView.getId() );
			MAP_IDS.add( newView.getId() );
			newView.setScale( mv.getScale() );
			mv = newView;
		}
		if ( !MAP_IDS.contains( mapId ) ) {
			MAP_IDS.add( mapId );
			Cartographer.getMain().saveConfig();
		}
		MapZoomEvent zoomEvent = new MapZoomEvent( mv.getScale(), zoom );
		Bukkit.getPluginManager().callEvent( zoomEvent );
		if ( !zoomEvent.isCancelled() ) {
			p.sendMap( MapManager.getInstance().getMapView( MapManager.getInstance().getPlayerMap( p ), mv, zoom ) );
		}
		renameMap( map, p.getItemInHand(), mv.getScale(), p );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	public void onMapInitializeEvent( MapInitializeEvent e ) {
		if ( ReflectionUtils.isExplorerMap( e.getMap() ) ) {
			return;
		}
		if ( Cartographer.getMain().isBlacklisted( e.getMap().getWorld().getName() ) ) {
			return;
		}
		if ( !( MapUtil.isDefaultMap( e.getMap() ) || MapUtil.isCartographerMap( e.getMap() ) ) ) {
			return;
		}
		MapView map = e.getMap();
		CartographerInitializeMapEvent mapEvent = new CartographerInitializeMapEvent( Scale.CLOSEST );
		Bukkit.getPluginManager().callEvent( mapEvent );
		map.setScale( mapEvent.getScale() );
		map = MapManager.getInstance().getMapView( null, map, true );
	}
	
	public static Set< Short > getMapIds() {
		return MAP_IDS;
	}
	
	public static void addMapId( short id ) {
		MAP_IDS.add( id );
	}
	
	public static void renameMap( Minimap map, ItemStack item, Scale scale, Player player ) {
		ItemMeta meta = item.getItemMeta();
		if ( map == null ) {
			meta.setDisplayName( "\u00a7fMap" );
			meta.setLore( null );
			item.setItemMeta( meta );
			return;
		}
		meta.setDisplayName( map.getName() );
		List< String > lore = new ArrayList< String >();
		for ( String loreLine : map.getLore() ) {
			lore.add( DependencyManager.parse( player, loreLine.replace( "%MAP_NAME%", map.getName() ).replace( "%MAP_SCALE%", scale.name() ) ).replace( "&", "\u00a7" ) );
		}
		meta.setLore( lore );
		item.setItemMeta( meta );
	}
}
