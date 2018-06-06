package io.github.bananapuncher714.askyblock;

import java.awt.Color;

import org.bukkit.Location;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;

public class ASkyBlockShader implements MapShader {
	ASkyBlock plugin = ASkyBlock.getPlugin();
	int maxColor = 16777215;
	
	@Override
	public Color shadeLocation( Location location, Color color ) {
		Island island = plugin.getGrid().getIslandAt( location );
		if ( island == null ) {
			return color;
		}
		int hash = island.getOwner().hashCode();
		Color shade = new Color( hash % maxColor );
		return ColorMixer.blend( color, shade, 50 );
	}

}
