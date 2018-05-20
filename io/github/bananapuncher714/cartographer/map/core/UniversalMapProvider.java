package io.github.bananapuncher714.cartographer.map.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapPalette;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.DependencyManager;
import io.github.bananapuncher714.cartographer.api.map.MapData;
import io.github.bananapuncher714.cartographer.api.map.MapProvider;
import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.map.addon.MapCursorSelector;
import io.github.bananapuncher714.cartographer.api.map.addon.MapShader;
import io.github.bananapuncher714.cartographer.api.map.addon.PixelShader;
import io.github.bananapuncher714.cartographer.api.map.addon.TextSelector;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.api.objects.MapCursorLocation;
import io.github.bananapuncher714.cartographer.api.objects.MapDirection;
import io.github.bananapuncher714.cartographer.api.objects.MapPixel;
import io.github.bananapuncher714.cartographer.api.objects.MapText;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.api.util.ColorMixer;
import io.github.bananapuncher714.cartographer.api.util.MapUtil;
import io.github.bananapuncher714.cartographer.map.interactive.MapViewer;
import io.github.bananapuncher714.cartographer.map.threading.AsyncLoader;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.BlockUtil;

public class UniversalMapProvider implements MapProvider {
	private BorderedMap map;

	// Things pertaining to how the worlds are loaded
	private boolean squareRenderBorder;
	private double renderDistance;

	// Map of all the chunks rendered, basically the new map
	Map< ChunkLocation, RenderedChunk > chunks = new HashMap< ChunkLocation, RenderedChunk >();

	// Things pertaining to map coloring
	Set< UUID > activated = new HashSet< UUID >();

	// Map loading and unloading
	AsyncLoader loader;

	private BukkitRunnable saver = new BukkitRunnable() {
		@Override
		public void run() {
			synchronized ( chunks ) {
				Set< ChunkLocation > remove = new HashSet< ChunkLocation >();
				for ( ChunkLocation location : chunks.keySet() ) {
					if ( location.getWorld().getPlayers().isEmpty() ) {
						remove.add( location );
						loader.saveChunk( chunks.get( location ) );
						continue;
					}
					int x = location.getX() * 16;
					int z = location.getZ() * 16;
					boolean unload = true;
					for ( Player player : location.getWorld().getPlayers() ) {
						double px = player.getLocation().getX();
						double pz = player.getLocation().getZ();
						if ( Math.abs( px - x ) < 1056 && Math.abs( pz - z ) < 1056 ) {
							unload = false;
							break;
						}
					}
					if ( unload ) {
						remove.add( location );
						loader.saveChunk( chunks.get( location ) );
					}
				}
				for ( ChunkLocation location : remove ) {
					chunks.remove( location );
				}
				if ( !remove.isEmpty() ) {
					CLogger.debug( "Unloaded " + remove.size() + " chunks!" );
				}
			}
		}
	};

	public UniversalMapProvider( BorderedMap map, Map< Material, Map< Byte, Color > > colorMap, Set< Material > skip ) {
		this.map = map;

		squareRenderBorder = map.isSquareLoad();
		renderDistance = map.getLoadRadius();

		loader = new AsyncLoader( this );

		saver.runTaskTimer( Cartographer.getMain(), 5, 5 );
	}

	@Override
	public void disable() {
		saver.cancel();
		loader.stop();
	}

	@Override
	public MapData getMap( Player player, ZoomScale scale ) {
		byte[][] map = new byte[ 128 ][ 128 ];
		byte[][] overlay = this.map.getOverlay();

		if ( !player.getWorld().getUID().equals( this.map.getWorld().getUID() ) ) {
			return new MapData( map, scale, player );
		}
		
		double centerx = this.map.getCenterX();
		double centery = this.map.getCenterY();
		double width = this.map.getWidth();
		double height = this.map.getHeight();

		Location mapCenter = player.getLocation();

		if ( !isInfiniteRendering() ) {
			double xo = mapCenter.getX() - centerx;
			double zo = mapCenter.getZ() - centery;

			double mapWidth = scale.getBlocksPerPixel() * 128;
			double borderx = ( width - mapWidth ) / 2.0;
			double bordery = ( height - mapWidth ) / 2.0;

			boolean overlapx = Math.abs( xo ) <= borderx;
			boolean overlapy = Math.abs( zo ) <= bordery;

			if ( mapWidth > width || mapWidth > height ) {
				return null;
			}

			double mapcenterx;
			double mapcentery;
			if ( !overlapx ) {
				mapcenterx = centerx;
				if ( xo < 0 ) mapcenterx -= borderx;
				else mapcenterx += borderx;
			}
			else mapcenterx = mapCenter.getX();
			if ( !overlapy ) {
				mapcentery = centery;
				if ( zo < 0 ) mapcentery -= bordery;
				else mapcentery += bordery;
			}
			else mapcentery = mapCenter.getZ();

			mapCenter = new Location( this.map.getWorld(), mapcenterx, 0, mapcentery );
		}

		double x = mapCenter.getX();
		double z = mapCenter.getZ();

		double nwx = x - ( scale.getBlocksPerPixel() * 64.0 );
		double nwz = z - ( scale.getBlocksPerPixel() * 64.0 );

		MapViewer viewer = MapViewer.getMapViewer( player.getUniqueId() );
		synchronized ( chunks ) {
			for ( int i = 0; i < 128; i++ ) {
				for ( int j = 0; j < 128; j++ ) {
					byte topColor = overlay[ i ][ j ];
					if ( topColor <= 0 ) {
						int localX = ( int ) ( nwx + ( i * scale.getBlocksPerPixel() ) );
						int localZ = ( int ) ( nwz + ( j * scale.getBlocksPerPixel() ) );
						int chunkX = localX >> 4;
						int chunkZ = localZ >> 4;
						ChunkLocation location = new ChunkLocation( this.map.getWorld(), chunkX, chunkZ );
			
						Location relative = new Location( this.map.getWorld(), localX, player.getLocation().getY(), localZ );
						if ( !viewer.isDiscovered( location ) ) {
							if ( player.getLocation().distanceSquared( relative ) > this.map.getDiscoverRadius() ) {
								continue;
							} else {
								viewer.discover( location );
							}
						}
			
						if ( chunks.get( location ) == null ) {
							if ( needsRender( location ) ) {
								loader.loadChunk( location );
							}
							continue;
						}
			
						int cx = localX & 15;
						int cz = localZ & 15;
						
						byte mcColor; 
						if ( this.map.isStaticColors() ) {
							mcColor = chunks.get( location ).getColorAt( cx, cz, scale );
						} else {
							mcColor = chunks.get( location ).getRawMap()[ cx ][ cz ];
						}
						
						if ( topColor != 0 ) {
							topColor = loadColor( topColor, mcColor );
						} else {
							topColor = mcColor;
						}
					}
					map[ i ][ j ] = topColor;
				}
			}
		}

		// REAL WORLD CURSOR SELECTOR BEGIN!
		List< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		for ( CursorSelector selector : this.map.getCursorSelectors() ) {
			cursors.addAll( selector.getCursors( player ) );
		}
		if ( this.map.showPlayer() ) {
			cursors.add( new RealWorldCursor( player.getLocation(), this.map.getDefPointer(), false ) );
		}

		MapData data = new MapData( map, scale, player );
		for ( RealWorldCursor cursor : cursors ) {
			MapCursorLocation cursorLocation = MapUtil.findRelCursorPosition( cursor.getLocation(), mapCenter.getX(), mapCenter.getZ(), scale );
			if ( cursor.hideWhenOOB() && cursorLocation.getDirection() != MapDirection.CENTER ) {
				continue;
			}
			double cursorX = cursorLocation.getX();
			double cursorY = cursorLocation.getY();
			MapCursor mapCursor = new MapCursor( ( byte ) 0, ( byte ) 0, ( byte ) 0, ( byte ) 0, true );
			mapCursor.setDirection( ( byte ) MapUtil.getDirection( cursor.getLocation().getYaw() ) );
			mapCursor.setType( cursor.getType() );
			mapCursor.setX( ( byte ) cursorX ); 
			mapCursor.setY( ( byte ) cursorY );
			if ( viewer.getState() && this.map.isCursorEnabled() && Math.abs( cursorX - viewer.getX() ) < this.map.getCursorRadius() && Math.abs( cursorY - viewer.getY() ) < this.map.getCursorRadius() ) {
				if ( activated.contains( player.getUniqueId() ) ) {
					cursor.executeActivate( player, cursorX, cursorY );
				} else {
					cursor.executeHover( player, cursorX, cursorY );
				}
			}
			data.addCursor( mapCursor );
		}

		if ( activated.contains( player.getUniqueId() ) ) {
			activated.remove( player.getUniqueId() );
		}

		if ( this.map.isCursorEnabled() && viewer.getState() ) {
			MapCursor cursor = new MapCursor( ( byte ) 0, ( byte ) 0, ( byte ) 0, ( byte ) 0, true );
			cursor.setType( this.map.getCursorType() );
			cursor.setX( ( byte ) viewer.getX() );
			cursor.setY( ( byte ) viewer.getY() );
			data.addCursor( cursor );
		}
		// REAL WORLD CURSOR SELECTOR END!

		// TEXT SELECTOR BEGIN!
		for ( TextSelector selector : this.map.getTextSelectors() ) {
			if ( selector == null ) continue;
			for ( MapText component : selector.getText( player ) ) {
				if ( component == null || component.getText() == null ) {
					continue;
				}
				if ( !component.getFont().isValid( component.getText() ) ) {
					data.addMapText( new MapText( "NULL", component.getX(), component.getY(), component.getFont() ) );
				} else {
					data.addMapText( component );
				}
			}
		}
		// TEXT SELECTOR END!

		// MAP CURSOR BEGIN!
		for ( MapCursorSelector selector : this.map.getMapCursorSelectors() ) {
			if ( selector == null ) {
				continue;
			}
			for ( MapCursor realMapCursor : selector.getCursors( player ) ) {
				data.addCursor( realMapCursor );
			}
		}
		// MAP CURSOR END!

		// PIXEL SHADERS BEGIN!
		for ( PixelShader shader : this.map.getPixelShaders() ) {
			if ( shader == null ) {
				continue;
			}
			for ( MapPixel pixel : shader.getPixels( player, mapCenter.getX(), mapCenter.getZ(), scale ) ) {
				data.addMapPixel( pixel );
			}
		}
		// PIXEL SHADERS END!

		return data;
	}

	@Override
	public void updateChunk( ChunkLocation location ) {
		if ( !needsRender( location ) ) {
			return;
		}
		byte[][] chunkMap = new byte[ 16 ][ 16 ];
		for ( int i = 0; i < 16; i++ ) {
			for ( int j = 0; j < 16; j++ ) {
				Location render = new Location( map.getWorld(), location.getX() * 16 + i, 0, location.getZ() * 16 + j );
				Color color = getColorAt( render );
				byte paletteColor = MapPalette.matchColor( color );
				chunkMap[ i ][ j ] = paletteColor;
			}
		}
		RenderedChunk chunk = new RenderedChunk( location, chunkMap );
		loader.saveChunk( chunk );
		chunks.put( location, chunk );
	}

	public boolean needsRender( ChunkLocation location ) {
		World mainWorld = map.getWorld();
		if ( !location.getWorld().getUID().equals( mainWorld.getUID() ) ) {
			return false;
		}
		int x = location.getX() * 16;
		int z = location.getZ() * 16;
		Location chunk = new Location( mainWorld, x, 0, z );
		if ( isInfiniteRendering() ) {
			for ( Player player : mainWorld.getPlayers() ) {
				Location ploc = player.getLocation();
				if ( squareRenderBorder ) {
					if ( Math.abs( ploc.getX() - x ) + 16 < 1024 && Math.abs( ploc.getZ() - z ) + 16 < renderDistance ) {
						return true;
					}
				} else {
					if ( chunk.distanceSquared( ploc ) < renderDistance * renderDistance ) {
						return true;
					}
				}
			}
		} else {
			return onMap( new Location( mainWorld, location.getX() * 16 + 16, 0, location.getZ() * 16 + 16 ) )
					|| onMap( new Location( mainWorld, location.getX() * 16 + 16, 0, location.getZ() * 16 ) )
					|| onMap( new Location( mainWorld, location.getX() * 16, 0, location.getZ() * 16 + 16 ) )
					|| onMap( new Location( mainWorld, location.getX() * 16, 0, location.getZ() * 16 ) );
		}
		return false;
	}

	@Override
	public boolean onMap( Location location ) {
		if ( isInfiniteRendering() ) {
			return location.getWorld().equals( map.getWorld() );
		}
		int xdistance = location.getBlockX() - map.getCenterX();
		int zdistance = location.getBlockZ() - map.getCenterY();
		return ( location.getWorld() == map.getWorld() && Math.abs( xdistance ) <= ( map.getWidth() / 2 ) && Math.abs( zdistance ) <= ( map.getHeight() / 2 ) );
	}

	@Override
	public void updateLocation( Location loc ) {
		for ( int i = -1; i < 2; i++ ) {
			Location l = loc.clone().add( 0, 0, i );
			int chunkX = l.getBlockX() >> 4;
		int chunkZ = l.getBlockZ() >> 4;
		ChunkLocation location = new ChunkLocation( map.getWorld(), chunkX, chunkZ );
		RenderedChunk chunk = chunks.get( location );
		if ( chunk == null ) {
			continue;
		}
		Color color = getColorAt( l );
		chunk.setPixel( l.getBlockX() & 15, l.getBlockZ() & 15, MapPalette.matchColor( color ) );
		}
	}

	public Color shadeLocation( Location location, Color color ) {
		Color c = color;
		for ( MapShader shader : map.getShaders() ) {
			c = shader.shadeLocation( location, c );
		}
		return c;
	}


	// Utility methods
	public Color getColorAt( Location location ) {
		Set< Material > skipBlocks = map.getSkipBlocks();
		Block b = BlockUtil.getHighestBlockAt( location, skipBlocks, map.getMapHeight() );
		int abovey;
		Location north = location.clone().subtract( 0, 0, 1 );
		if ( DependencyManager.doesChunkExist( north.getWorld(), north.getBlockX() >> 4, north.getBlockZ() >> 4 ) ) {
			abovey = BlockUtil.getHighestBlockAt( north, skipBlocks, map.getMapHeight() ).getY();
		} else {
			abovey = b.getLocation().getBlockY();
		}
		Material mat = b.getType();
		byte data = b.getData();
		Color c = map.getDefaultColor();
		if ( map.isHighestIfNotHidden() && !skipBlocks.contains( b.getRelative( BlockFace.UP ).getType() ) ) {
			if ( map.isShowHidden() ) {
				b = BlockUtil.getNextHighestBlockAt( location, skipBlocks, map.getMapHeight() );
				if ( DependencyManager.doesChunkExist( north.getWorld(), north.getBlockX() >> 4, north.getBlockZ() >> 4 ) ) {
					abovey = BlockUtil.getNextHighestBlockAt( north, skipBlocks, map.getMapHeight() ).getY();
				} else {
					abovey = b.getLocation().getBlockY();
				}
				mat = b.getType();
				data = b.getData();
				c = getColor( mat, data );
			} else if ( map.isHighestIfNotHidden() ) {
				c = Color.BLACK;
			}
		} else {
			c = getColor( mat, data );
		}
		Color tempColor = c;
		if ( tempColor == null ) {
			tempColor = map.getDefaultColor();
		}
		if ( b.getY() < abovey ) {
			tempColor = ColorMixer.brightenColor( tempColor, -30 );
		} else if ( b.getY() == abovey ) {
			tempColor = ColorMixer.brightenColor( tempColor, -10 );
		}
		if ( b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER ) {
			tempColor = ColorMixer.brightenColor( tempColor, - Math.min( 100, ( map.getWaterShading() * BlockUtil.getWaterDepth( b.getLocation() ) ) / 100 ) );
		}
		return shadeLocation( location, tempColor );
	}

	public Color getColor( Material mat, byte data ) {
		Map< Material, Map< Byte, Color > > mapColors = map.getMapColors();
		Color c = null;
		if ( mapColors.get( mat ) != null ) {
			c = mapColors.get( mat ).get( data );
			if ( c == null ) {
				c = mapColors.get( mat ).get( ( byte ) -1 );
			}
		}
		return c;
	}

	private byte loadColor( byte c1, byte mc ) {
		Color c2 = MapPalette.getColor( mc );
		c1 = (byte) -c1;
		int a = c1 & 1;
		int r = ( c1 >>> 5 ) & 3;
		int g = ( c1 >>> 3 ) & 3;
		int b = ( c1 >>> 1 ) & 3;
		Color color = new Color( r * 85, g * 85, b * 85, a == 1 ? 172 : 86 );
		return MapPalette.matchColor( ColorMixer.blendARGB( c2, color ) );
	}

	public boolean isInfiniteRendering() {
		return map.isInfinite();
	}

	public void addRenderedChunk( RenderedChunk chunk ) {
		synchronized ( chunks ) {
			chunks.put( chunk.getLocation(), chunk );
		}
	}

	public BorderedMap getMap() {
		return map;
	}

	@Override
	public void activate( Player player ) {
		activated.add( player.getUniqueId() );
	}
}
