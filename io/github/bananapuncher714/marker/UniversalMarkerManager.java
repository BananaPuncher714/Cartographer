package io.github.bananapuncher714.marker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.marker.file.MarkerSaver;

public class UniversalMarkerManager {
	private static UniversalMarkerManager instance = null;
	
	private List< MarkerGroup > groups = new ArrayList< MarkerGroup >();
	
	private UniversalMarkerManager() {
	}
	
	public void registerGroup( MarkerGroup group ) {
		groups.add( group );
	}
	
	public void removeGroup( String id ) {
		for ( int i = 0; i < groups.size(); i++ ) {
			if ( groups.get( i ).getId().equalsIgnoreCase( id ) ) {
				groups.remove( i );
			}
		}
	}
	
	public MarkerGroup getGroup( String id ) {
		for ( MarkerGroup group : groups ) {
			if ( group.getId().equalsIgnoreCase( id ) ) {
				return group;
			}
		}
		MarkerGroup group = MarkerSaver.getMarkerGroup( new File( Cartographer.getMain().getDataFolder() + "/modules/" + "markers" ), id );
		if ( group != null ) {
			groups.add( group );
			return group;
		}
		return null;
	}
	
	public List< MarkerGroup > getGroups() {
		return groups;
	}
	
	public static UniversalMarkerManager getUMM() {
		if ( instance == null ) {
			instance = new UniversalMarkerManager();
		}
		return instance;
	}

}
