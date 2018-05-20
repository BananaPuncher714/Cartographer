package io.github.bananapuncher714.towny.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.event.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.WorldCoord;

import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;

public class TownyUnclaimListener implements Listener {
	
	@EventHandler
	public void onTownyUnlaimEvent( TownUnclaimEvent event ) {
		WorldCoord coord = event.getWorldCoord();
		final int x = coord.getX() * 16;
		final int z = coord.getZ() * 16;
		final World world = coord.getBukkitWorld();
		ChunkLoadListener.addChunkToRenderQueue( world, x, z );
	}
}
