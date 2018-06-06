package io.github.bananapuncher714.cartographer.listeners;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.DependencyManager;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.ReflectionUtils;

public class ChunkLoadListener implements Listener {
	private static Set< ChunkLocation > RENDER_CACHE = new HashSet< ChunkLocation >();
	private static Queue< ChunkLocation > RENDER_QUEUE = new ArrayDeque< ChunkLocation >();
	
	public ChunkLoadListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask( Cartographer.getMain(), this::update, 0, 1 );
	}

	@EventHandler
	public void onChunkPopulateEvent( ChunkPopulateEvent event ) {
		Chunk chunk = event.getChunk();
		addChunkToRenderQueue( new ChunkLocation( chunk.getWorld(), chunk.getX(), chunk.getZ() ) );
	}
	
	@EventHandler
	public void onChunkLoadEvent( ChunkLoadEvent event ) {
		if ( event.isNewChunk() ) {
			return;
		}
		if ( !Cartographer.getMain().isRenderOnChunkLoad() ) {
			return;
		}
		Chunk chunk = event.getChunk();
		addChunkToRenderQueue( new ChunkLocation( event.getWorld(), chunk.getX(), chunk.getZ() ) );
	}

	private void update() {
		if ( ReflectionUtils.getTps()[ 0 ] < Cartographer.getMain().getTpsThreshold() ) {
			CLogger.debug( "TPS Threshold reached!" );
			return;
		}
		if ( Bukkit.getOnlinePlayers().size() == 0 ) {
			RENDER_QUEUE.clear();
		}
		int MLS = Cartographer.getMain().getMapLoadSpeed();
		Queue< ChunkLocation > chunks = ChunkLoadListener.RENDER_QUEUE;
		while( !chunks.isEmpty() && MLS-- > 0 ) {
			ChunkLocation location = RENDER_QUEUE.poll();
			RENDER_CACHE.remove( location );
			World world = location.getWorld();
			int x = location.getX();
			int z = location.getZ();
			if ( !DependencyManager.doesChunkExist( world, x, z ) ) {
				continue;
			}
//			if ( !world.isChunkLoaded( x, z ) ) {
//				world.loadChunk( x, z ); // is this needed?
//				continue;
//			}
			for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
				if ( map.isRenderOnChunkLoad() && map.isUpdateEnabled() ) {
					map.recolorChunk( world, x, z );
				}
			}
		}
		if ( !chunks.isEmpty() ) {
			CLogger.debug( RENDER_QUEUE.size() + " chunks in processing queue!" );
		}
	}

	public static void addChunkToRenderQueue( World world, int x, int z )  {
		addChunkToRenderQueue( new ChunkLocation( world, x, z ) );
	}

	public static void addChunkToRenderQueue( ChunkLocation location ) {
		if ( RENDER_CACHE.contains( location ) ) {
			return;
		}
		RENDER_CACHE.add( location );
		RENDER_QUEUE.add( location );
	}
}
