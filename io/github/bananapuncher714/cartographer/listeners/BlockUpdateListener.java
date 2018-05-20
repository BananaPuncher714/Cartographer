package io.github.bananapuncher714.cartographer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;

public class BlockUpdateListener implements Listener {
	Cartographer main = Cartographer.getMain();
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onBlockBreakEvent( BlockBreakEvent e ) {
		for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
			if ( e.isCancelled() || !map.isUpdateEnabled() ) return;
			Bukkit.getScheduler().runTask( main, new Runnable() {
				@Override
				public void run() {
					map.updateLocation( e.getBlock().getLocation() );
				}
			} );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onBlockBreakEvent( BlockPlaceEvent e ) {
		for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
			if ( e.isCancelled() || !map.isUpdateEnabled() ) return;
			Bukkit.getScheduler().runTask( main, new Runnable() {
				@Override
				public void run() {
					map.updateLocation( e.getBlock().getLocation() );
				}
			} );
		}
	}
}
