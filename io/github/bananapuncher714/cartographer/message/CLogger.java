package io.github.bananapuncher714.cartographer.message;

import org.bukkit.command.CommandSender;

import io.github.bananapuncher714.cartographer.Cartographer;

public class CLogger {

	public static void info( String msg ) {
		Cartographer.getMain().getLogger().info( msg );
	}
	
	public static void debug( String msg ) {
		if ( Cartographer.DEBUG ) Cartographer.getMain().getLogger().info( "[DEBUG]" + msg );
	}
	
	public static void warning( String msg ) {
		Cartographer.getMain().getLogger().warning( msg );
	}
	
	public static void severe( String msg ) {
		Cartographer.getMain().getLogger().severe( msg );
	}
	
	public static void msg( CommandSender sender, String identifier, Object... paramStrings ) {
		String message = Cartographer.getMain().getLocaleAddon().parse( sender, identifier, paramStrings );
		if ( !message.isEmpty() ) sender.sendMessage( message );
	}
	
	public static String parse( CommandSender sender, String identifier, Object... paramStrings ) {
		return Cartographer.getMain().getLocaleAddon().parse( sender, identifier, paramStrings );
	}
}
