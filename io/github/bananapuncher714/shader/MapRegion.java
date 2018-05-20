package io.github.bananapuncher714.shader;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MapRegion {
	private Location lower;
	private int x, y;
	private String name;
	private Color regionColor;
	Set< UUID > viewers = new HashSet< UUID >();
	
	public MapRegion(Location lower, int x, int y, String name, Color regionColor) {
		this.lower = lower;
		this.x = x;
		this.y = y;
		this.name = name;
		this.regionColor = regionColor;
	}

	public Location getLower() {
		return lower;
	}

	public Color getRegionColor() {
		return regionColor;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getName() {
		return name;
	}
	
	public boolean isViewer( Player player ) {
		return viewers.contains( player.getUniqueId() );
	}
	
	public void addViewer( Player player ) {
		viewers.add( player.getUniqueId() );
	}
	
	public void addViewer( UUID uuid ) {
		viewers.add( uuid );
	}
	
	public void removeViewer( Player player ) {
		viewers.remove( player.getUniqueId() );
	}
	
	public Set< UUID > getViewers() {
		return viewers;
	}
}
