package io.github.bananapuncher714.waypoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.addon.TextSelector;
import io.github.bananapuncher714.cartographer.api.objects.MapText;

public class WaypointTextSelector implements TextSelector {
	// TODO Remove static modifier
	final Map< UUID, List< MapText > > selections = new HashMap< UUID, List< MapText > >();
	WaypointAddon addon;
	
	protected WaypointTextSelector( WaypointAddon addon ) {
		this.addon = addon;
	}

	@Override
	public List< MapText > getText( Player player ) {
		if ( selections.containsKey( player.getUniqueId() ) ) {
			List< MapText > texts = selections.get( player.getUniqueId() );
			selections.remove( player.getUniqueId() );
			return texts;
		} else {
			return new ArrayList< MapText >();
		}
	}
	
	public List< MapText > getSelections( Player player ) {
		if ( !selections.containsKey( player.getUniqueId() ) ) {
			selections.put( player.getUniqueId(), new ArrayList< MapText >() );
		}
		return selections.get( player.getUniqueId() );
	}
}
