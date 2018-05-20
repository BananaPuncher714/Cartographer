package io.github.bananapuncher714.worldguard;

import java.awt.Color;

import org.bukkit.Location;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;

public class WorldGuardLandShader implements MapShader {
	Color tint;
	
	public WorldGuardLandShader( Color tint ) {
		this.tint = tint;
	}

	@Override
	public Color shadeLocation( Location location, Color color ) {
		RegionManager regionManager = WGBukkit.getRegionManager( location.getWorld() );
		if ( regionManager == null ) return color;
		ApplicableRegionSet set = regionManager.getApplicableRegions( location );
		Color shaded = color;
		for ( int regAmount = 0; regAmount < set.getRegions().size(); regAmount++ ) {
			shaded = ColorMixer.blend( shaded, tint, 30 );
		}
		return shaded;
	}

}
