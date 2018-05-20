package io.github.bananapuncher714.citizens;

import java.util.HashMap;
import java.util.UUID;

public class CursorManager {
	CitizensAddon addon;
	private HashMap< UUID, CitizenCursor > cursors = new HashMap< UUID, CitizenCursor >();
	
	public CursorManager( CitizensAddon addon ) {
		this.addon = addon;
	}
	
	public CitizenCursor getCursor( UUID uuid ) {
		if ( cursors.containsKey( uuid ) ) {
			return cursors.get( uuid );
		}
		CitizenCursor cursor = CitizensFileManager.loadCursor( addon.getDatafolder(), uuid ); 
		if ( cursor == null ) cursor = new CitizenCursor( addon.getType(), addon.isVisible(), addon.isHidden(), addon.getRange() );
		cursors.put( uuid,  cursor );
		return cursor;
	}
	
	public void saveCursors() {
		for ( UUID uuid : cursors.keySet() ) CitizensFileManager.saveCursor( addon.getDatafolder(), uuid, cursors.get( uuid ) );
	}
}
