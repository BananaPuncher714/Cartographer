package io.github.bananapuncher714.cartographer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.map.interactive.MapViewer;
import io.github.bananapuncher714.cartographer.message.CLogger;

public class CursorExecutor implements CommandExecutor {

	@Override
	public boolean onCommand( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		if ( arg3.length == 0 ) {
			toggle( arg0, arg3 );
		} else if ( arg3.length > 0 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "toggle" ) ) toggle( arg0, arg3 );
		}
		return false;
	}

	private void toggle( CommandSender sender, String... args ) {
		if ( !( sender instanceof Player ) ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.must-be-player" ) );
		}
		
		Player player = ( Player ) sender;
		MapViewer viewer = MapViewer.getMapViewer( player.getUniqueId() );
		viewer.setState( !viewer.getState() );
	}
}
