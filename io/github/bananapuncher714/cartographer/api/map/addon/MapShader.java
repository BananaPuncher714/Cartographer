package io.github.bananapuncher714.cartographer.api.map.addon;

import java.awt.Color;

import org.bukkit.Location;

public interface MapShader {
	public abstract Color shadeLocation(Location location, Color color);
}
