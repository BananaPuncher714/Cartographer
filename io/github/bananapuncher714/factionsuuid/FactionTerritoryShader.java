package io.github.bananapuncher714.factionsuuid;

import java.awt.Color;
import java.util.Random;

import org.bukkit.Location;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;


public class FactionTerritoryShader implements MapShader {
	
	public FactionTerritoryShader() {
	}

	@Override
	public Color shadeLocation( Location location, Color color ) {
		Faction faction = Board.getInstance().getFactionAt( new FLocation( location ) );
		if ( faction.isWilderness() ) return color;
		Color factionColor = getColor( faction.getTag() );
		Color shaded;
		if ( faction.isSafeZone() ) shaded = ColorMixer.blend( color, Color.GREEN, FactionsUUIDAddon.PERCENT );
		else if ( faction.isWarZone() ) shaded = ColorMixer.blend( color, Color.RED, FactionsUUIDAddon.PERCENT );
		else shaded = ColorMixer.blend( color, factionColor, FactionsUUIDAddon.PERCENT );
		return shaded;
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
