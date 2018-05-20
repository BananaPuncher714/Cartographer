package io.github.bananapuncher714.beacon.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class BeaconTabCompleter implements TabCompleter {

	@Override
	public List< String > onTabComplete( CommandSender sender, Command arg1, String arg2, String[] args ) {
		List< String > completions = new ArrayList< String >();
		List< String > aos = new ArrayList< String >();
		
		if ( args.length == 1 ) {
			if ( sender.hasPermission( "cartographer.beacon.add" ) ) {
				aos.add( "add" );
			}
			if ( sender.hasPermission( "cartographer.beacon.remove" ) ) {
				aos.add( "remove" );
			}
			if ( sender.hasPermission( "cartographer.beacon.create" ) ) {
				aos.add( "create" );
			}
			if ( sender.hasPermission( "cartographer.beacon.destroy" ) ) {
				aos.add( "destroy" );
			}
		}
		
		StringUtil.copyPartialMatches( args[ args.length - 1 ], aos, completions );
		Collections.sort( completions );
		return completions;
	}

}
