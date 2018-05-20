package io.github.bananapuncher714.waypoints.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.waypoints.WPerms;
import io.github.bananapuncher714.waypoints.Waypoint;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.waypoints.WaypointViewer;
import io.github.bananapuncher714.waypoints.WaypointViewer.DisplayType;
import io.github.bananapuncher714.waypoints.file.WaypointFileManager;
import io.github.bananapuncher714.waypoints.inv.WaypointBananaManager;

public class WaypointExecutor implements CommandExecutor {

	@Override
	public boolean onCommand( CommandSender s, Command c, String l, String[] a ) {
		if ( !( s instanceof Player ) ) {
			CLogger.msg( s, "header", CLogger.parse( s, "waypoints.name" ), CLogger.parse( s, "waypoints.command.must-be-player" ) );
			return false;
		}
		Player player = ( Player ) s;
		if ( a.length == 0 ) cmd_inventory( player, c, a );
		else if ( a[ 0 ].equalsIgnoreCase( "create" ) ) cmd_create( player, c, a );
		else if ( a[ 0 ].equalsIgnoreCase( "open" ) ) cmd_inventory( player, c, a );
		else if ( a[ 0 ].equalsIgnoreCase( "refresh" ) ) refresh( player, a );
		else CLogger.msg( s, "header", CLogger.parse( s, "waypoints.name" ), CLogger.parse( s, "waypoints.command.usage" ) );
		return false;
	}
	
	private void cmd_create( Player p, Command c, String[] a ) {
		if ( !WPerms.canCreate( p ) ) {
			CLogger.msg( p, "header", CLogger.parse( p, "waypoints.name" ), CLogger.parse( p, "waypoints.command.no-permission" ) );
			return;
		}
		Minimap map = MapManager.getInstance().getPlayerMap( p );
		if ( map == null ) {
				CLogger.msg( p, "header", CLogger.parse( p, "main.name" ), CLogger.parse( p, "main.notifications.no-map-found" ) );
				return;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			CLogger.msg( p, "header", CLogger.parse( p, "main.name" ), CLogger.parse( p, "main.command.disabled-module" ) );
			return;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		WaypointViewer viewer = addon.getManager().getViewer( p );
		if ( !WPerms.canBypassLimit( p ) ) {
			int waypointAmount = viewer.getWaypoints().size();
			if ( waypointAmount >= addon.maxWaypoints() ) {
				CLogger.msg( p, "header", CLogger.parse( p, "waypoints.name" ), CLogger.parse( p, "waypoints.notification.too-many-waypoints", addon.maxWaypoints() ) );
				return;
			}
		}
		Waypoint created = null;
		if ( a.length == 4 ) {
			try {
				int x = Integer.parseInt( a[ 2 ] );
				int z = Integer.parseInt( a[ 3 ] );
				Location l = new Location( map.getWorld(), x, 0, z );
				created = new Waypoint( addon.getManager(), p, a[ 1 ], l, false );
				CLogger.msg( p, "header", CLogger.parse( p, "waypoints.name" ), CLogger.parse( p, "waypoints.notification.created-waypoint", a[ 1 ] ) );
			} catch( Exception exception ) {
				CLogger.msg( p, "header", CLogger.parse( p, "waypoints.name" ), CLogger.parse( p, "waypoints.command.create-usage" ) );
			}
		} else if ( a.length == 2 ) {
			Location location = p.getLocation().clone();
			location.setYaw( 180 );
			created = new Waypoint( addon.getManager(), p, a[ 1 ], location, true );
			CLogger.msg( p, "header", CLogger.parse( p, "waypoints.name" ), CLogger.parse( p, "waypoints.notification.created-waypoint", a[ 1 ] ) );
		} else {
			CLogger.msg( p, "header", CLogger.parse( p, "waypoints.name" ), CLogger.parse( p, "waypoints.command.create-usage" ) );
			return;
		}
		created.setDiscover( addon.getDefaultRange() );
		viewer.getWaypoints().put( created, DisplayType.HIGHLIGHTED );
	}
	
	private void cmd_inventory( Player player, Command c, String[] a ) {
		if ( !WPerms.canUse( player ) ) {
			CLogger.msg( player, "header", CLogger.parse( player, "waypoints.name" ), CLogger.parse( player, "waypoints.command.no-permission" ) );
			return;
		}
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
				CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notifications.no-map-found" ) );
				return;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.command.disabled-module" ) );
			return;
		}
		CustomInventory inv = WaypointBananaManager.getCustomInventory( player, "WaypointManagerInventory" );
		if ( inv == null ) {
			CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.command.disabled-module" ) );
			return;
		}
		player.openInventory( inv.getInventory( false ) );
	}
	
	private void refresh( Player player, String[] a ) {
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
				CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notifications.no-map-found" ) );
				return;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.command.disabled-module" ) );
			return;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		WaypointFileManager.refresh( addon );
	}

}
