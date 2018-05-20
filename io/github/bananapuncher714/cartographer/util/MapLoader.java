package io.github.bananapuncher714.cartographer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.bananapuncher714.cartographer.map.core.BorderedMap;

public final class MapLoader {
	
	public static List< BorderedMap > getMaps( File directory ) {
		List< BorderedMap > mapList = new ArrayList< BorderedMap >();
		if ( !directory.exists() ) {
			directory.mkdirs();
		}
		for ( File file : directory.listFiles() ) {
			if ( file.isDirectory() ) {
				mapList.add( new BorderedMap( file ) );
			}
		}
		return mapList;
	}

	public static BorderedMap getMap( File mapDirectory ) {
		if ( !mapDirectory.exists() ) {
			mapDirectory.mkdirs();
		}
		if ( mapDirectory.isDirectory() ) {
			return new BorderedMap( mapDirectory );
		} else {
			return null;
		}
	}
}
