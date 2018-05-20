package io.github.bananapuncher714.kingdoms;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;
import org.kingdoms.constants.Rank;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.manager.game.GameManagement;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;

public class KingdomCursorSelector implements CursorSelector {
	KingdomsAddon addon;
	
	protected KingdomCursorSelector( KingdomsAddon addon ) {
		this.addon = addon;
	}

	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		ArrayList< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		if ( addon.unloaded() ) return cursors;
		KingdomPlayer kplayer = GameManagement.getPlayerManager().getSession( player );
		if ( kplayer == null ) return cursors;
		Kingdom kingdom = kplayer.getKingdom();
		if ( kingdom == null ) return cursors;
		for ( KingdomPlayer member : kingdom.getOnlineMembers() ) {
			Location playerLocation = member.getPlayer().getLocation();
			if ( member.getPlayer().getUniqueId() != player.getUniqueId() ) {
				Type type = member.getRank() == Rank.KING ? addon.getKingCursorType() : ( member.getRank() == Rank.MODS ? addon.getModCursorType() : addon.getMemberCursorType() );
				cursors.add( new RealWorldCursor( playerLocation, type, true ) );
			}
		}
		Location home = kingdom.getHome_loc();
		if ( home != null ) {
			home.setYaw( 180 );
			cursors.add( new RealWorldCursor( home, addon.getHomeCursorType(), false ) );
		}
		return cursors;
	}

}
