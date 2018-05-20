package io.github.bananapuncher714.cartographer.demo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;

public class ClaimListener implements Listener {
	
	@EventHandler
	public void onChunkClaimEvent( ChunkClaimEvent event ) {
		ChunkLoadListener.addChunkToRenderQueue( new ChunkLocation( event.getChunk() ) );
	}
	
	public void onChunkUnclaimEvent( ChunkUnclaimEvent event ) {
		ChunkLoadListener.addChunkToRenderQueue( new ChunkLocation( event.getChunk() ) );
	}
}
