package io.github.bananapuncher714.cartographer.map.core;

import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;

public class RenderedChunk {
	private ChunkLocation location;
	private byte[][] map;
	private byte[][] two = new byte[ 8 ][ 8 ];
	private byte[][] four = new byte[ 4 ][ 4 ];
	private byte[][] eight = new byte[ 2 ][ 2 ];
	private byte oneColor;
	
	public RenderedChunk( ChunkLocation location, byte[][] map ) {
		this.location = location;
		this.map = map;
		refreshColors();
	}

	public ChunkLocation getLocation() {
		return location;
	}

	public byte getColorAt( int x, int z, ZoomScale scale ) {
		switch( scale ) {
		case SIXTEEN: return oneColor;
		case EIGHT: return eight[ x / 8 ][ z / 8 ];
		case FOUR: return four[ x / 4 ][ z / 4 ];
		case TWO: return two[ x / 2 ][ z / 2 ];
		default: return map[ x ][ z ];
		}
	}
	
	public byte[][] getRawMap() {
		return map;
	}
	
	protected void setPixel( int x, int y, byte color ) {
		map[ x ][ y ] = color;
		refreshColors();
	}
	
	private void refreshColors() {
		oneColor = getBestColor( map );
	    for ( int i = 0; i < two.length; i++ ) {
	    	for ( int j = 0; j < two[ i ].length; j++ ) {
	    		two[ i ][ j ] = getBestColor( getSubArray( i * 2, j * 2, 2, 2 ) );
	    	}
	    }
	    for ( int i = 0; i < four.length; i++ ) {
	    	for ( int j = 0; j < four[ i ].length; j++ ) {
	    		four[ i ][ j ] = getBestColor( getSubArray( i * 4, j * 4, 4, 4 ) );
	    	}
	    }
	    for ( int i = 0; i < eight.length; i++ ) {
	    	for ( int j = 0; j < eight[ i ].length; j++ ) {
	    		eight[ i ][ j ] = getBestColor( getSubArray( i * 8, j * 8, 8, 8 ) );
	    	}
	    }
	}
	
	private byte[][] getSubArray( int x, int z, int w, int h ) {
		byte[][] temp = new byte[ w ][ h ];
		for ( int i = 0; i < w; i++ ) {
			for ( int j = 0; j < h; j++ ) {
				temp[ i ][ j ] = map[ i + x ][ j + z ];
			}
		}
		return temp;
	}
	
	private byte getBestColor( byte[][] items ) {
		int[] array = new int[ 128 ];
		for ( int x = 0; x < items.length; x++ ) {
			for ( int z = 0; z < items[ x ].length; z++ ) {
				int index = items[ x ][ z ];
				if ( index < 0 ) {
					continue;
				}
				array[ index ]++;
			}
		}
		int max = 0;
		byte color = 0;
		for ( int index = 0; index < array.length; index++ ) {
			if ( array[ index ] >= max ) {
				max = array[ index ];
				color = ( byte ) index;
			}
		}
		return color;
	}
}
