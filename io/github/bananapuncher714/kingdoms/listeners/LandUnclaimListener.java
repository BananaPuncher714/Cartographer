package io.github.bananapuncher714.kingdoms.listeners;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kingdoms.events.LandUnclaimEvent;

import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;

public class LandUnclaimListener implements Listener {

	@EventHandler
	public void onLandUnclaimEvent( LandUnclaimEvent event ) {
		Chunk chunk = event.getLand().getLoc().toChunk();
		ChunkLoadListener.addChunkToRenderQueue( new ChunkLocation( chunk ) );
	}
	
}
