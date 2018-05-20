package io.github.bananapuncher714.towny;

import java.io.File;

import org.bukkit.Bukkit;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.towny.listeners.TownyClaimListener;
import io.github.bananapuncher714.towny.listeners.TownyUnclaimListener;

public class TownyAddon extends Module {

	static {
		Bukkit.getPluginManager().registerEvents( new TownyClaimListener(), Cartographer.getMain() );
		Bukkit.getPluginManager().registerEvents( new TownyUnclaimListener(), Cartographer.getMain() );
	}
	
	@Override
	public void load( Minimap map, File dataFolder ) {
		map.registerShader( new TownyLandShader() );
		map.registerCursorSelector( new TownyCursorSelector() );
	}

	@Override
	public void unload() {
		
	}
}
