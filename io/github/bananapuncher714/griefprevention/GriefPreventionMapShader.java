package io.github.bananapuncher714.griefprevention;

import java.awt.Color;
import java.util.Random;

import org.bukkit.Location;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GriefPreventionMapShader implements MapShader {
	GriefPreventionAddon addon;
	
	GriefPreventionMapShader( GriefPreventionAddon addon ) {
		this.addon = addon;
	}
	
	@Override
	public Color shadeLocation(Location location, Color color) {
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt( location, true, null );
		String name = claim.getOwnerName();
		return ColorMixer.blend( color, getColor( name ), addon.getShadePercent() );
	}
	
	public Color getColor( String name ) {
		int id = name.hashCode();
		Random rng = new Random( id );
		int r = rng.nextInt( 3 );
		int g = rng.nextInt( 3 );
		int b = rng.nextInt( 3 );
		return new Color( r * 127, g * 127, b * 127 );
	}
}
