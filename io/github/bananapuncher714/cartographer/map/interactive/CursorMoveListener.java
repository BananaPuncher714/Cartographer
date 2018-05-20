package io.github.bananapuncher714.cartographer.map.interactive;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.map.MapView;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.util.MapUtil;

public class CursorMoveListener implements Listener, Runnable {
	private Map< UUID, Location > startPos = new HashMap< UUID, Location >();
	
	public CursorMoveListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask( Cartographer.getMain(), this, 0, 1 );
	}

	@Override
	public void run() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			Location before = startPos.containsKey( player.getUniqueId() ) ? startPos.get( player.getUniqueId() ) : player.getLocation();
			Location after = player.getLocation();
			MapViewer viewer = MapViewer.getMapViewer( player.getUniqueId() );
		
			double degree = before.getYaw() - after.getYaw();
			double pitch = 4 * ( before.getPitch() - after.getPitch() );

			viewer.setX( Math.min( 126, Math.max( viewer.getX() - degree, - 128 ) ) );
			viewer.setY( Math.min( 126, Math.max( viewer.getY() - pitch, - 128 ) ) );
			startPos.put( player.getUniqueId(), after );
		}
	}
	
	@EventHandler
	public void onPlayerChangeItem( PlayerItemHeldEvent event ) {
		MapViewer viewer = MapViewer.getMapViewer( event.getPlayer().getUniqueId() );
		viewer.setX( 0 );
		viewer.setY( 0 );
	}

	@EventHandler
	public void onPlayerDropItemEvent( PlayerDropItemEvent event ) {
		MapViewer viewer = MapViewer.getMapViewer( event.getPlayer().getUniqueId() );
		if ( !viewer.getState() ) {
			return;
		}
		if ( event.getItemDrop().getItemStack().getType() == Material.MAP ) {
			MapView view = Bukkit.getMap( event.getItemDrop().getItemStack().getDurability() );
			if ( !MapUtil.isCartographerMap( view ) ) {
				return;
			}
			event.setCancelled( true );
			Minimap bMap = MapManager.getInstance().getPlayerMap( event.getPlayer() );

			if ( bMap == null ) {
				return;
			}
			bMap.getProvider().activate( event.getPlayer() );
		}
	}
}
