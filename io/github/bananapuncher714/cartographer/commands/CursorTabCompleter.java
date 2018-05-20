package io.github.bananapuncher714.cartographer.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class CursorTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List< String > completions = new ArrayList< String >();
//		if ( !( arg0 instanceof Player ) || !CartographerMain.getMain().isCursorEnabled() ) {
//			return completions;
//		}
		List< String > aos = new ArrayList< String >();
		if ( arg3.length == 1 ) {
			aos.add( "toggle" );
			StringUtil.copyPartialMatches( arg3[ 0 ], aos, completions);
		}
		Collections.sort( completions );
		return completions;
	}

}
