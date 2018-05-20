package io.github.bananapuncher714.cartographer.commands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.CPerms;
import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.map.core.BorderedMap;
import io.github.bananapuncher714.cartographer.message.CLogger;

public class SetMapCenterExecutor implements CommandExecutor {
	Cartographer main;
	
	public SetMapCenterExecutor( Cartographer main ) {
		this.main = main;
	}

	@Override
	public boolean onCommand( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		if ( !( arg0 instanceof Player ) ) return false;
		Player p = ( Player ) arg0;
		Minimap map = MapManager.getInstance().getPlayerMap( p );
		if ( arg3.length == 1 ) {
			map = MapManager.getInstance().getMinimap( arg3[ 0 ] );
			if ( map == null ) {
				map = Cartographer.getMain().createNewMap( true, arg3[ 0 ] );
			}
			MapManager.getInstance().setPlayerMap( p, map );
		}
		if ( !CPerms.isAdmin( p ) ) {
			CLogger.msg( p, "header", CLogger.parse( p, "main.name" ), CLogger.parse( p, "main.command.no-permission" ) );
			return false;
		}
		if ( map == null ) {
			CLogger.msg( p, "header", CLogger.parse( p, "main.name" ), CLogger.parse( p, "main.command.setmapcenter-usage" ) );
			return false;
		}
		int x, y;
		World world;
		int xLen = map.getWidth();
		int yLen = map.getHeight();
		if ( map.isCenterChunk() ) {
			Chunk ch = p.getLocation().getChunk();
			x = ( ch.getX() * 16 ) + 8;
			y = ( ch.getZ() * 16 ) + 8;
			world = ch.getWorld();
		} else {
			Location ploc = p.getLocation();
			x = ploc.getBlockX();
			y = ploc.getBlockZ();
			world = p.getWorld();
		}
		
		map.relocateMap( x, y, xLen, yLen, world );
		for ( Player pl : Bukkit.getOnlinePlayers() ) {
			if ( pl.hasPermission( "cartographer.admin" ) ) CLogger.msg( pl, "header", CLogger.parse( pl, "main.name" ), CLogger.parse( pl, "main.notification.map-centered", map.getName(), map.getId(), x, y ) );
		}
		if ( map instanceof BorderedMap  ) {
			( ( BorderedMap ) map ).saveData();
		}
		return false;
	}

}
