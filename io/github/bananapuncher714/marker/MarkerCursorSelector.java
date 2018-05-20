package io.github.bananapuncher714.marker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;

public class MarkerCursorSelector implements CursorSelector {

	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		List< RealWorldCursor > cursors = new ArrayList< RealWorldCursor>();
		List< MarkerGroup> groups = UniversalMarkerManager.getUMM().getGroups();
		for ( MarkerGroup group : groups ) {
			if ( group.isViewer( player ) && player.hasPermission( "cartographer.markers.view." + group.getId() ) || player.hasPermission( "cartographer.markers.viewall" ) ) {
				cursors.addAll( group.getCursors() );
			}
		}
		
		return cursors;
	}

}
