package io.github.bananapuncher714.towny;

import java.awt.Color;
import java.util.Random;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer.Tint;

public class TownyLandShader implements MapShader {

	@Override
	public Color shadeLocation( Location location, Color color ) {
		TownBlock townBlock = TownyUniverse.getTownBlock( location );
		if ( townBlock == null ) return color;
		return ColorMixer.tintColor( Tint.YELLOW, color, 35 );
//		Town town = null;
//		try {
//			town = townBlock.getTown();
//			return ColorMixer.blend( color, getColor( town.getNation().getName() ), 50 );
//		} catch ( Exception exception ) {
//			return color;
//		}
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
