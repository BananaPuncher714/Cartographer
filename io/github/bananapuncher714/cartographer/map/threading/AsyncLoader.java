package io.github.bananapuncher714.cartographer.map.threading;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.map.core.RenderedChunk;
import io.github.bananapuncher714.cartographer.map.core.UniversalMapProvider;

public class AsyncLoader {
	private UniversalMapProvider provider;
	private final File dataFolder;
	private Set< RenderedChunk > beingSaved = new HashSet< RenderedChunk >();
	private Set< RenderedChunk > saveQueue = new HashSet< RenderedChunk >();
	private Set< ChunkLocation > beingLoaded = new HashSet< ChunkLocation >();
	private Set< ChunkLocation > loadQueue = new HashSet< ChunkLocation >();

	private List< ChunkSaver > threadpool = new ArrayList< ChunkSaver >();

	public AsyncLoader( UniversalMapProvider provider ) {
		this.provider = provider;
		dataFolder = new File( provider.getMap().getDataFolder() + "/chunks/" );

		for ( int i = 0; i < 3; i++ ) {
			ChunkSaver saver = new ChunkSaver( this, Cartographer.getMain() );
			threadpool.add( saver );
			saver.start();
		}
	}

	public void stop() {
		for ( ChunkSaver saver : threadpool ) {
			saver.terminate();
		}
	}

	public void loadChunk( ChunkLocation location ) {
		synchronized ( loadQueue ) {
			if ( !beingLoaded.contains( location )  ) {
				loadQueue.add( location );
			}
		}
	}

	public void saveChunk( RenderedChunk chunk ) {
		synchronized( loadQueue ) {
			beingLoaded.remove( chunk.getLocation() );
		}
		synchronized ( saveQueue ) {
			if ( !beingSaved.contains( chunk ) ) {
				saveQueue.add( chunk );
			}
		}
	}

	public File getDataFolder() {
		return dataFolder;
	}

	protected ChunkLocation fetchLoad() {
		synchronized ( loadQueue ) {
			if ( !loadQueue.isEmpty() ) {
				ChunkLocation loc = loadQueue.iterator().next();
				loadQueue.remove( loc );
				beingLoaded.add( loc );
				return loc;
			}
		}
		return null;
	}
	
	protected void confirmLoad( ChunkLocation location ) {
		synchronized ( loadQueue ) {
			beingLoaded.remove( location );
		}
	}

	protected void loadChunk( RenderedChunk chunk ) {
		provider.addRenderedChunk( chunk );
	}

	protected RenderedChunk fetchSave() {
		synchronized ( saveQueue ) {
			if ( !saveQueue.isEmpty() ) {
				RenderedChunk chunk = saveQueue.iterator().next();
				saveQueue.remove( chunk );
				beingSaved.add( chunk );
				return chunk;
			}
		}
		return null;
	}
	
	protected void confirmSave( RenderedChunk chunk ) {
		synchronized ( saveQueue ) {
			beingSaved.remove( chunk );
		}
	}

	protected synchronized boolean needSaving() {
		return saveQueue.size() > loadQueue.size();
	}

	public void debug() {
		synchronized( loadQueue ) {
			System.out.println( "Items in load queue: " + loadQueue.size() );
		}
		synchronized( saveQueue ) {
			System.out.println( "Items in save queue: " + saveQueue.size() );
		}
	}

	public boolean isLoading( ChunkLocation location ) {
		synchronized ( loadQueue ) {
			return loadQueue.contains( location );
		}
	}
}
