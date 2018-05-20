package io.github.bananapuncher714.cartographer;

import org.bukkit.permissions.Permissible;

public class CPerms {
	
	public static boolean isAdmin( Permissible player ) {
		return player.hasPermission( "cartographer.admin" );
	}
	
	public static boolean inventoryEdit( Permissible player ) {
		return isAdmin( player ) || player.hasPermission( "cartographer.main.inventory" );
	}
	
	public static boolean canChangeLocale( Permissible player ) {
		return isAdmin( player ) || player.hasPermission( "cartographer.locale.change" );
	}
}
