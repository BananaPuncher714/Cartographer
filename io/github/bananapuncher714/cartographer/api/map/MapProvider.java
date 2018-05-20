package io.github.bananapuncher714.cartographer.api.map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;

public interface MapProvider {
	public boolean onMap( Location location );
	public MapData getMap( Player player, ZoomScale scale );
	public void updateChunk( ChunkLocation location );
	public void updateLocation( Location location );
	public void disable();
	public void activate( Player player );
}
