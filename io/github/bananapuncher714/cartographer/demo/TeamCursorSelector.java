package io.github.bananapuncher714.cartographer.demo;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;

/**
 * Gets a list of RealWorldCursors that shows on the map
 * 
 * @author BananaPuncher714
 */
public class TeamCursorSelector implements CursorSelector {
	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		List< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		
		// Add all teammates to the list of cursors
		Location cursorLoc = null;
		Type type = FailSafe.getEnum( Type.class, "WHITE_POINTER" );
		boolean highlighted = false;
		RealWorldCursor cursor = new RealWorldCursor( cursorLoc, type, !highlighted );
		
		cursors.add( cursor );
		return cursors;
	}
}
