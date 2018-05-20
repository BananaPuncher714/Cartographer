package io.github.bananapuncher714.kingdoms;

import java.awt.Color;

import org.bukkit.Location;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.SimpleChunkLocation;
import org.kingdoms.manager.game.GameManagement;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;

public class KingdomLandShader implements MapShader {
	KingdomsAddon addon;
	
	protected KingdomLandShader( KingdomsAddon addon ) {
		this.addon = addon;
	}
	
	@Override
	public Color shadeLocation( Location location, Color color ) {
		Land land = GameManagement.getLandManager().getOrLoadLand( new SimpleChunkLocation( location.getChunk() ) );
		String owner = land.getOwner();
		if ( owner == null ) return color;
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom( owner );
		return ColorMixer.blend( color, new Color( kingdom.getDynmapColor() ), addon.getShadingPercent() );
	}

}
