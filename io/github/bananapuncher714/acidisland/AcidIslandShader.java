package io.github.bananapuncher714.acidisland;

import java.awt.Color;

import org.bukkit.Location;

import com.wasteofplastic.acidisland.ASkyBlock;
import com.wasteofplastic.acidisland.Island;

import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;

public class AcidIslandShader implements MapShader {
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
