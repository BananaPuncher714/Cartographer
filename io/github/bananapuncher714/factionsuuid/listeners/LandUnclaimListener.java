package io.github.bananapuncher714.factionsuuid.listeners;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;

import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;

public class LandUnclaimListener implements Listener {
	
	@EventHandler
	public void onLandUnclaimEvent( LandUnclaimEvent event ) {
		int x = ( int ) event.getLocation().getX();
		int z = ( int ) event.getLocation().getZ();
		Chunk chunk = event.getLocation().getWorld().getChunkAt( x, z );
		ChunkLoadListener.addChunkToRenderQueue( chunk.getWorld(), chunk.getX(), chunk.getZ() );
	}

	@EventHandler
	public void onLandUnclaimAllEvent( LandUnclaimAllEvent event ) {
		for ( FLocation floc : event.getFaction().getAllClaims() ) {
			int x = ( int ) floc.getX();
			int z = ( int ) floc.getZ();
			Chunk chunk = floc.getWorld().getChunkAt( x, z );
			ChunkLoadListener.addChunkToRenderQueue( chunk.getWorld(), chunk.getX(), chunk.getZ() );
		}
	}
	
	@EventHandler
	public void onFactionDisbandEvent( FactionDisbandEvent event ) {
		for ( FLocation floc : event.getFaction().getAllClaims() ) {
			int x = ( int ) floc.getX();
			int z = ( int ) floc.getZ();
			Chunk chunk = floc.getWorld().getChunkAt( x, z );
			ChunkLoadListener.addChunkToRenderQueue( chunk.getWorld(), chunk.getX(), chunk.getZ() );
		}
	}
	
	@EventHandler
	public void onFPlayerLeaveEvent( FPlayerLeaveEvent event ) {
		for ( FLocation floc : event.getFaction().getAllClaims() ) {
			int x = ( int ) floc.getX();
			int z = ( int ) floc.getZ();
			Chunk chunk = floc.getWorld().getChunkAt( x, z );
			ChunkLoadListener.addChunkToRenderQueue( chunk.getWorld(), chunk.getX(), chunk.getZ() );
		}
	}
}
