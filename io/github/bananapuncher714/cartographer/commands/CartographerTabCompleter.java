package io.github.bananapuncher714.cartographer.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import io.github.bananapuncher714.cartographer.CPerms;

public class CartographerTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List< String > completions = new ArrayList< String >();
		if ( !( arg0 instanceof Player ) ) {
			completions.add( "addimage" );
			completions.add( "addoverlay" );
			return completions;
		}
		List< String > aos = new ArrayList< String >();
		if ( arg3.length == 1 ) {
			if ( CPerms.isAdmin( arg0 ) ) {
				aos.add( "open" );
				aos.add( "addimage" );
				aos.add( "addoverlay" );
				aos.add( "reload" );
				aos.add( "chunkreload" );
				aos.add( "give" );
			}
			if ( CPerms.canChangeLocale( arg0 ) ) {
				aos.add( "locale" );
			}
			if ( arg0.hasPermission( "cartographer.citizens.inventory" ) ) {
				aos.add( "npc" );
			}
			if ( arg0.hasPermission( "cartographer.playerheads.shader" ) ) {
				aos.add( "head" );
			}
			if ( arg0.hasPermission( "cartographer.main.overlay" ) ) {
				aos.add( "overlay" );
			}
			StringUtil.copyPartialMatches( arg3[ 0 ], aos, completions);
		}
		Collections.sort( completions );
		return completions;
	}

}
