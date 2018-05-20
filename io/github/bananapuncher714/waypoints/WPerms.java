package io.github.bananapuncher714.waypoints;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.CPerms;

public class WPerms {
	
	public static boolean canTeleport( Player player ) {
		return player.hasPermission( "cartographer.waypoints.teleport" ) || hasAdmin( player );
	}
	
	public static boolean hasAdmin( Player player ) {
		return player.hasPermission( "cartographer.waypoints.admin" ) || CPerms.isAdmin( player );
	}
	
	public static boolean canPublic( Player player ) {
		return player.hasPermission( "cartographer.waypoints.public" ) || hasAdmin( player );
	}
	
	public static boolean hasStaff( Player player ) {
		return player.hasPermission( "cartographer.waypoints.staff" ) || hasAdmin( player );
	}
	
	public static boolean canLock( Player player ) {
		return player.hasPermission( "cartographer.waypoints.lock" ) || hasAdmin( player );
	}
	
	public static boolean canCreate( Player player ) {
		return player.hasPermission( "cartographer.waypoints.create" ) || hasAdmin( player );
	}
	
	public static boolean canUse( Player player ) {
		return player.hasPermission( "cartographer.waypoints.use" ) || hasAdmin( player ) || canCreate( player );
	}
	
	public static boolean canBypassLimit( Player player ) {
		return player.hasPermission( "cartographer.waypoints.bypass.limit" ) || hasAdmin( player );
	}
	
	public static boolean canDiscoverable( Player player ) {
		return hasAdmin( player ) || player.hasPermission( "cartographer.waypoints.discoverable" );
	}
}
