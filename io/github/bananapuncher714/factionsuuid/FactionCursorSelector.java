package io.github.bananapuncher714.factionsuuid;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;

public class FactionCursorSelector implements CursorSelector {

	public FactionCursorSelector() {
	}
	
	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		ArrayList< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		FPlayer fPlayer = FPlayers.getInstance().getByPlayer( player );
		
		Faction faction = fPlayer.getFaction();
		if ( faction == null || faction.isWilderness() ) return cursors;
		
		List< Player > members = faction.getOnlinePlayers();
		for ( Player member : members ) {
			if ( player.equals( member ) ) continue;
			FPlayer fMember = FPlayers.getInstance().getByPlayer( member );
			fMember.getRole();
			if ( fMember.getRole() == Role.ADMIN ) cursors.add( new RealWorldCursor( member.getLocation(), MapCursor.Type.valueOf( FactionsUUIDAddon.admin ), FactionsUUIDAddon.OOB ) );
			else if ( fMember.getRole() == Role.MODERATOR ) cursors.add( new RealWorldCursor( member.getLocation(), MapCursor.Type.valueOf( FactionsUUIDAddon.moderator ), FactionsUUIDAddon.OOB ) );
			else if ( fMember.getRole() == Role.NORMAL ) cursors.add( new RealWorldCursor( member.getLocation(), MapCursor.Type.valueOf( FactionsUUIDAddon.normal ), FactionsUUIDAddon.OOB ) );
		}
		
		if ( !faction.hasHome() ) return cursors;
		Location home = faction.getHome().clone();
		home.setYaw( 180 );
		cursors.add( new RealWorldCursor( home, MapCursor.Type.valueOf( FactionsUUIDAddon.home ), FactionsUUIDAddon.HOOB ) );
		
		return cursors;
	}

}
