package io.github.bananapuncher714.worldborder.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;
import io.github.bananapuncher714.worldborder.events.WorldBorderChangeSizeAndCenterEvent;
import io.github.bananapuncher714.worldborder.events.WorldBorderChangeSizeEvent;
import io.github.bananapuncher714.worldborder.events.WorldBorderSwitchCenterEvent;

public class WorldBorderListener implements Listener {
	
	@EventHandler
	public void onWorldBorderChangeSizeEvent( WorldBorderChangeSizeEvent event ) {
		WorldBorder border = event.getBorder();
		Location center = border.getCenter().clone();
		Location mid = border.getCenter().clone();
		double rad = ( Math.max( event.getNewSize(), event.getOldSize() ) / 2 );
		double smallRad = ( Math.min( event.getNewSize(), event.getOldSize() ) / 2 ) - 16;
		center.subtract( rad, 0, rad );
		
		int size = ( int ) Math.ceil( Math.max( event.getNewSize(), event.getOldSize() ) / 16.0 );
		for ( int increment = 0; increment < size; increment ++ ) {
			int x = center.getBlockX() / 16 + increment;
			for ( int incZ = 0; incZ < size; incZ ++ ) {
				int z = center.getBlockZ() / 16 + incZ;
				if ( smallRad < Math.abs( ( x * 16 ) - mid.getBlockX() ) || smallRad < Math.abs( ( z * 16 ) - mid.getBlockZ() ) ) {
					ChunkLoadListener.addChunkToRenderQueue( new ChunkLocation( event.getWorld(), x, z ) );
				}
			}
		}
	}
	
	@EventHandler
	public void onWorldBorderSwitchCenterEvent( WorldBorderSwitchCenterEvent event ) {
		Set< ChunkLocation > chunkTotal = new HashSet< ChunkLocation >();
		WorldBorder border = event.getBorder();
		Location center = event.getOldLocation().clone();
		Location newC = event.getNewLocation().clone();
		double rad = border.getSize() / 2;
		center.subtract( rad, 0, rad );
		newC.subtract( rad, 0, rad );
		int size = ( int ) Math.ceil( border.getSize() / 16.0 );
		for ( int increment = 0; increment < size; increment ++ ) {
			int x = center.getBlockX() / 16 + increment;
			int x2 = newC.getBlockX() / 16 + increment;
			for ( int incZ = 0; incZ < size; incZ ++ ) {
				int z = ( int ) Math.floor( center.getBlockZ() / 16 ) + incZ;
				int z2 = ( int ) Math.floor( newC.getBlockZ() / 16 ) + incZ;
				chunkTotal.add( new ChunkLocation( event.getWorld(), x, z ) );
				chunkTotal.add( new ChunkLocation( event.getWorld(), x2, z2 ) );
			}
		}
		
		for ( ChunkLocation location : chunkTotal ) {
			ChunkLoadListener.addChunkToRenderQueue( location );
		}
	}
	
	@EventHandler
	public void onWorldBorderUpdateEvent( WorldBorderChangeSizeAndCenterEvent event ) {
		WorldBorder border = event.getBorder();
		
		Set< ChunkLocation > chunkTotal = new HashSet< ChunkLocation >();
		Location center = event.getOldLocation().clone();
		Location newC = event.getNewLocation().clone();
		double rad = event.getOldSize() / 2;
		double rad2 = event.getNewSize() / 2;
		center.subtract( rad, 0, rad );
		newC.subtract( rad2, 0, rad2 );
		int size = ( int ) Math.ceil( border.getSize() / 16.0 );
		int size2 = ( int ) Math.ceil( border.getSize() / 16.0 );
		
		for ( int increment = 0; increment < size; increment ++ ) {
			int x = center.getBlockX() / 16 + increment;
			for ( int incZ = 0; incZ < size; incZ ++ ) {
				int z = ( int ) Math.floor( center.getBlockZ() / 16 ) + incZ;
				chunkTotal.add( new ChunkLocation( event.getWorld(), x, z ) );
			}
		}
		
		for ( int increment = 0; increment < size2; increment ++ ) {
			int x = newC.getBlockX() / 16 + increment;
			for ( int incZ = 0; incZ < size2; incZ ++ ) {
				int z = ( int ) Math.floor( newC.getBlockZ() / 16 ) + incZ;
				chunkTotal.add( new ChunkLocation( event.getWorld(), x, z ) );
			}
		}
		
		for ( ChunkLocation location : chunkTotal ) {
			ChunkLoadListener.addChunkToRenderQueue( location );
		}
	}

}
