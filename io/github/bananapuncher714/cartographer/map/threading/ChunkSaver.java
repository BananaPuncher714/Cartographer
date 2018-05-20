package io.github.bananapuncher714.cartographer.map.threading;

import java.io.File;

import org.bukkit.Bukkit;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;
import io.github.bananapuncher714.cartographer.map.core.RenderedChunk;
import io.github.bananapuncher714.cartographer.util.FileUtil;

public class ChunkSaver extends Thread {
	private volatile boolean stop = false;
	private AsyncLoader loader;
	private Cartographer main;
	
	protected ChunkSaver( AsyncLoader loader, Cartographer main ) {
		this.loader = loader;
		this.main = main;
	}
	
	@Override
	public void run() {
		while ( !stop ) {
			try {
				Thread.sleep( 1 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			RenderedChunk chunk = null;
			ChunkLocation location = null;
			synchronized ( loader ) {
				if ( loader.needSaving() ) {
					chunk = loader.fetchSave();
				} else {
					location = loader.fetchLoad();
				}
			}
			if ( chunk != null ) {
				saveRenderedChunk( chunk );
				loader.confirmSave( chunk );
			}
			if ( location != null ) {
				final RenderedChunk fchunk = loadChunk( location );
				final ChunkLocation floc = location;
				Bukkit.getScheduler().scheduleSyncDelayedTask( main, new Runnable() {
					@Override
					public void run() {
						if ( fchunk == null ) {
							Bukkit.getScheduler().scheduleSyncDelayedTask( main, new Runnable() {
								@Override
								public void run() {
									ChunkLoadListener.addChunkToRenderQueue( floc );
								}
							} );
						} else {
							loader.loadChunk( fchunk );
							loader.confirmLoad( floc );
						}
					}
				} );
			}
		}
	}
	
	protected void terminate() {
		stop = true;
	}
	
	private void saveRenderedChunk( RenderedChunk chunk ) {
		File file = new File( loader.getDataFolder() + "/" + chunk.getLocation().getX() + "/" + chunk.getLocation().getZ() );
		file.getParentFile().mkdirs();
		FileUtil.saveByteArray( chunk.getRawMap(), file );
	}
	
	private RenderedChunk loadChunk( ChunkLocation location ) {
		File file = new File( loader.getDataFolder() + "/" + location.getX() + "/" + location.getZ() );
		if ( !file.exists() ) {
			return null;
		}
		byte[][] rc = FileUtil.loadFile( file );
		RenderedChunk chunk = new RenderedChunk( location, rc );
		return chunk;
	}

}
