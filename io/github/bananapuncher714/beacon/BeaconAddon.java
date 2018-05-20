package io.github.bananapuncher714.beacon;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bananapuncher714.beacon.command.BeaconExecutor;
import io.github.bananapuncher714.beacon.command.BeaconTabCompleter;
import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;

public class BeaconAddon extends Module {
	Minimap map;
	Map< Beacon, Integer > beacons = new HashMap< Beacon, Integer >();
	
	static {
		Cartographer.getMain().getCommand( "beacon" ).setExecutor( new BeaconExecutor() );
		Cartographer.getMain().getCommand( "beacon" ).setTabCompleter( new BeaconTabCompleter() );
	}
	
	BukkitRunnable decrementer = new BukkitRunnable() {
		@Override
		public void run() {
			Set< Beacon > beaconsToRemove = new HashSet< Beacon >();
			for ( Beacon beacon : beacons.keySet() ) {
				if ( beacons.get( beacon ) <= 0 ) {
					beaconsToRemove.add( beacon );
				} else {
					beacons.put( beacon, beacons.get( beacon ) - 1 );
				}
			}
			for ( Beacon beacon : beaconsToRemove ) {
				beacons.remove( beacon );
				beacon.destroy();
			}
		}
	};
	
	@Override
	public void load( Minimap map, File dataFolder) {
		this.map = map;
		decrementer.runTaskTimer( Cartographer.getMain(), 0, 1 );
		map.registerPixelShader( new BeaconPixelShader( this ) );
	}

	@Override
	public void unload() {
		decrementer.cancel();
	}
	
	public Set< Beacon > getBeacons() {
		return beacons.keySet();
	}

	public void addBeacon( Beacon beacon, int ticks ) {
		beacons.put( beacon, ticks );
	}
	
	public void removeBeacon( String name ) {
		for ( Beacon beacon : beacons.keySet() ) {
			if ( beacon.getName().equals( name ) ) {
				beacons.put( beacon, 0 );
			}
		}
	}
	
	public void add( Player player, String name ) {
		for ( Beacon beacon : beacons.keySet() ) {
			if ( beacon.getName().equals( name ) ) {
				beacon.addViewer( player );
			}
		}
	}
	
	public void remove( Player player, String name ) {
		for ( Beacon beacon : beacons.keySet() ) {
			if ( beacon.getName().equals( name ) ) {
				beacon.removeViewer( player );
			}
		}
	}
}
