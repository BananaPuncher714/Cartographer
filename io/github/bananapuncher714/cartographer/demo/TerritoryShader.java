package io.github.bananapuncher714.cartographer.demo;

import java.awt.Color;

import org.bukkit.Location;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;

public class TerritoryShader implements MapShader {

	@Override
	public Color shadeLocation( Location location, Color color ) {
		// Get your claim object here
		Object claim = null;
		// Get the color of the claim
		Color claimColor = null;
		
		int integerBetweenZeroAndOneHundred = 50;
		return ColorMixer.blend( color, claimColor, integerBetweenZeroAndOneHundred );
	}
}