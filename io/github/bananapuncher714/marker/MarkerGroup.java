package io.github.bananapuncher714.marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;

public class MarkerGroup {
	private Map< String, RealWorldCursor > cursors = new HashMap< String, RealWorldCursor >();
	private Set< UUID > viewers = new HashSet< UUID >();
	private String id;
	
	public MarkerGroup( String id ) {
		this.id = id;
	}
	
	public void addMarker( String name, RealWorldCursor cursor ) {
		cursors.put( name, cursor );
	}
	
	public void removeCursor( String name ) {
		cursors.remove( name );
	}
	
	public void addViewer( Player player ) {
		viewers.add( player.getUniqueId() );
	}
	
	public void removeViewer( Player player ) {
		viewers.remove( player.getUniqueId() );
	}
	
	public boolean isViewer( Player player ) {
		return viewers.contains( player.getUniqueId() );
	}
	
	public Collection< RealWorldCursor > getCursors() {
		return cursors.values();
	}
	
	public Map< String, RealWorldCursor > getRawCursors() {
		return cursors;
	}
	
	public String getId() {
		return id;
	}
}
