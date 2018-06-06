package io.github.bananapuncher714.cartographer.commands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.CPerms;
import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;
import io.github.bananapuncher714.cartographer.message.CLogger;

public class MapReloadExecutor implements CommandExecutor {
	Cartographer main;
	
	public MapReloadExecutor( Cartographer main ) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if ( !CPerms.isAdmin( arg0 ) ) {
			CLogger.msg( arg0, "header", CLogger.parse( arg0, "main.name" ), CLogger.parse( arg0, "You do not have permission to run this command!" ) );
			return false;
		}
		if ( !( arg0 instanceof Player ) ) {
			return false;
		}
		Player player = ( Player ) arg0;
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( arg3.length == 1 ) {
			map = MapManager.getInstance().getMinimap( arg3[ 0 ] );
			if ( map == null ) {
				map = Cartographer.getMain().createNewMap( true, arg3[ 0 ] );
			}
			MapManager.getInstance().setPlayerMap( player, map );
		}
		if ( map == null ) {
			CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.no-map-found" ) );
			return false;
		}
		if ( map.isUpdateEnabled() ) {
			for ( Player pl : Bukkit.getOnlinePlayers() ) {
				if ( CPerms.isAdmin( arg0 ) ) CLogger.msg( pl, "header", CLogger.parse( arg0, "main.name" ), CLogger.parse( arg0, "main.notification.reloading-map", map.getName(), map.getId() ) );
			}
			
			for ( Chunk chunk : map.getWorld().getLoadedChunks() ) {
				ChunkLoadListener.addChunkToRenderQueue( new ChunkLocation( chunk ) );
			}
			
		} else {
			CLogger.msg( arg0, "header", CLogger.parse( arg0, "main.name" ), CLogger.parse( arg0, "main.notification.updates-disabled", map.getName(), map.getId() ) );
		}
		return false;
	}

}
