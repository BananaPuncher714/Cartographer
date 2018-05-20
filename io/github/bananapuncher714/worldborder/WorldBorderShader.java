package io.github.bananapuncher714.worldborder;

import java.awt.Color;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;

public class WorldBorderShader implements MapShader {
	WorldBorderAddon addon;
	
	public WorldBorderShader( WorldBorderAddon addon ) {
		this.addon = addon;
	}
	
	@Override
	public Color shadeLocation( Location location, Color color ) {
		World world = location.getWorld();
		WorldBorder border = world.getWorldBorder();
		
		if ( border == null ) {
			return color;
		}
		
		Location newLoc = location.clone().subtract( border.getCenter() );
		if ( Math.abs( newLoc.getX() ) > border.getSize() / 2.0 || Math.abs( newLoc.getZ() ) > border.getSize() / 2.0 ) {
			return ColorMixer.blend( color, addon.getBorderColor(), addon.getTint() );
		}
		
		return color;
	}
}
