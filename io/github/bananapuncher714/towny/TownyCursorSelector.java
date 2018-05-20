package io.github.bananapuncher714.towny;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;

public class TownyCursorSelector implements CursorSelector {

	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		ArrayList< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		Resident resident;
		try {
			resident = TownyUniverse.getDataSource().getResident( player.getName() );
		} catch ( NotRegisteredException e ) {
			return cursors;
		}
		if ( !resident.hasTown() ) return cursors;
		Location l;
		try {
			l = resident.getTown().getSpawn();
		} catch ( TownyException e ) {
			return cursors;
		}
		if ( l != null ) {
			l.setYaw( 180 );
			cursors.add( new RealWorldCursor( l, MapCursor.Type.WHITE_CROSS, false ) );
		}
		ArrayList< Player > players;
		try {
			players = new ArrayList< Player >( TownyUniverse.getOnlinePlayers( resident.getTown() ) );
		} catch ( NotRegisteredException e ) {
			return cursors;
		}
		for ( Player res : players ) {
			if ( !res.getName().equalsIgnoreCase( resident.getName() ) )
				cursors.add( new RealWorldCursor( res.getLocation(), MapCursor.Type.GREEN_POINTER, false ) );
		}
		return cursors;
	}

}
