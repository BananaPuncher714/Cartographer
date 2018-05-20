package io.github.bananapuncher714.cartographer.map.core;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.MapData;
import io.github.bananapuncher714.cartographer.api.map.MapProvider;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;

public class ImageMapProvider implements MapProvider {

	@Override
	public boolean onMap(Location location) {
		return false;
	}

	@Override
	public MapData getMap(Player player, ZoomScale scale) {
		return null;
	}

	@Override
	public void updateChunk(ChunkLocation location) {

	}

	@Override
	public void updateLocation(Location location) {

	}

	@Override
	public void disable() {

	}

	@Override
	public void activate(Player player) {

	}

}
