package io.github.bananapuncher714.waypoints.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import io.github.bananapuncher714.waypoints.WPerms;

public class WaypointTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List< String > completions = new ArrayList< String >();
		if ( !( arg0 instanceof Player ) ) return completions;
		Player player = ( Player ) arg0;
		List< String > aos = new ArrayList< String >();
		if ( arg3.length == 1 ) {
			if ( WPerms.canCreate( player ) ) aos.add( "create" );
			if ( WPerms.hasAdmin( player ) ) aos.add( "refresh" );
			aos.add( "open" );
			StringUtil.copyPartialMatches( arg3[ 0 ], aos, completions);
		}
		Collections.sort( completions );
		return completions;
	}

}
