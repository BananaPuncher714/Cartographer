package io.github.bananapuncher714.factionsuuid.listeners;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.event.LandClaimEvent;

import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;

public class LandClaimListener implements Listener {
	
	@EventHandler
	public void onLandClaimEvent( LandClaimEvent event ) {
		int x = ( int ) event.getLocation().getX();
		int z = ( int ) event.getLocation().getZ();
		Chunk chunk = event.getLocation().getWorld().getChunkAt( x, z );
		ChunkLoadListener.addChunkToRenderQueue( chunk.getWorld(), chunk.getX(), chunk.getZ() );
	}

}
