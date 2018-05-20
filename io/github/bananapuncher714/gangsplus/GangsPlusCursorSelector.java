package io.github.bananapuncher714.gangsplus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;

public class GangsPlusCursorSelector implements CursorSelector {
	private GangsPlusAddon addon;
	
	public GangsPlusCursorSelector( GangsPlusAddon addon ) {
		this.addon = addon;
	}

	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		List< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		Gang gang = GangsPlusApi.getPlayersGang( player );
		if ( gang == null ) {
			return cursors;
		}
		for ( Player member : gang.getOnlineMembers() ) {
			if ( member.getUniqueId().equals( player.getUniqueId() ) ) {
				continue;
			}
			int rank = gang.getMemberData( member ).getRank();
			cursors.add( addon.getCursor( player, rank ) );
		}
		
		return cursors;
	}

}
