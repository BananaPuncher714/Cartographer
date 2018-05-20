package io.github.bananapuncher714.cartographer.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;

public class SetMapCenterTabCompleter implements TabCompleter {

	@Override
	public List< String > onTabComplete( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		List< String > completions = new ArrayList< String >();
		List< String > aos = new ArrayList< String >();
		for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
			aos.add( map.getId() );
		}
		StringUtil.copyPartialMatches( arg3[ 0 ], aos, completions );
		Collections.sort( completions );
		return completions;
	}

}
