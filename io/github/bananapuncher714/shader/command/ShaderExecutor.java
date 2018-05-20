package io.github.bananapuncher714.shader.command;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.command.OptionParser;
import io.github.bananapuncher714.shader.MapRegion;
import io.github.bananapuncher714.shader.RegionManager;

public class ShaderExecutor implements CommandExecutor {
	private final Map< String, String > parameters = new HashMap< String, String >();

	public ShaderExecutor() {
		parameters.put( "-l", "LOCATION" );
		parameters.put( "-location", "LOCATION" );
		parameters.put( "-c", "COLOR" );
		parameters.put( "-color", "COLOR" );
		parameters.put( "-w", "WIDTH" );
		parameters.put( "-width", "WIDTH" );
		parameters.put( "-h", "HEIGHT" );
		parameters.put( "-height", "HEIGHT" );
		parameters.put( "-t", "TRANSPARENCY" );
		parameters.put( "-transparency", "TRANSPARENCY" );
	}
	
	@Override
	public boolean onCommand( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		if ( arg3.length == 0 ) {
			cmd_help( arg0 );
		} else if ( arg3.length == 1 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "add" ) || arg3[ 0 ].equalsIgnoreCase( "remove" ) ) {
				msg( arg0, "shaders.command.usage-add-remove" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "create" ) ) {
				msg( arg0, "shaders.command.usage-create" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "destroy" ) ) {
				msg( arg0, "shaders.command.usage-destroy" );
			} else {
				cmd_help( arg0 );
			}
		} else if ( arg3.length == 2 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "create" ) ) {
				cmd_create( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "destroy" ) ) {
				cmd_destroy( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "add" ) || arg3[ 0 ].equalsIgnoreCase( "remove" ) ) {
				msg( arg0, "shaders.command.usage-add-remove" );
			} else {
				cmd_help( arg0 );
			}
		} else if ( arg3.length >= 3 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "add" ) ) {
				cmd_add( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "remove" ) ) {
				cmd_remove( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "create" ) ) {
				cmd_create( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "destroy" ) ) {
				cmd_destroy( arg0, arg3 );
			} else {
				cmd_help( arg0 );
			}
		} else {
			CLogger.severe( "THIS ISNT EVER SUPPOSED TO HAPPEN!!!" );
		}
		return false;
	}
	
	private void cmd_help( CommandSender sender ) {
		msg( sender, "shaders.command.usage" );
	}

	private void cmd_create( CommandSender sender, String... args ) {
		if ( !sender.hasPermission( "cartographer.shader.create" ) ) {
			msg( sender, "main.command.no-permission" );
			return ;
		}
		Map< String, String > arguments = new HashMap< String, String >();

		arguments.put( "COLOR", "16777215" );
		arguments.put( "WIDTH", "16" );
		arguments.put( "HEIGHT", "16" );
		arguments.put( "TRANSPARENCY", "50" );
		
		OptionParser.parseArguments( parameters, arguments, 2, false, args );
		
		Color color;
		int height, width;
		Location loc;
		
		try {
			color = new Color( Integer.parseInt( arguments.get( "COLOR" ) ) );
			color = new Color( color.getRed(), color.getBlue(), color.getGreen(), Integer.parseInt( arguments.get( "TRANSPARENCY" ) ) * 255 / 100 );
			height = Integer.parseInt( arguments.get( "HEIGHT" ) );
			width = Integer.parseInt( arguments.get( "WIDTH" ) );
			if ( sender instanceof Entity && !arguments.containsKey( "LOCATION" ) ) {
				loc = ( ( Entity ) sender ).getLocation().getBlock().getLocation();
			} else {
				String[] locStr = arguments.get( "LOCATION" ).split( ":" );
				loc = new Location( Bukkit.getWorlds().get( 0 ), Double.parseDouble( locStr[0 ] ), 0, Double.parseDouble( locStr[ 1 ] ) );
			}
		} catch( Exception exception ) {
			msg( sender, "shaders.notification.invalid-argument" );
			return;
		}
		MapRegion region = new MapRegion( loc, height, width, args[ 1 ], color );
		msg( sender, "shaders.command.created", args[ 1 ] );
		RegionManager.getInstance().addRegion( region );
	}
	
	private void cmd_destroy( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.shader.destroy" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		
		MapRegion region = RegionManager.getInstance().getRegion( args[ 1 ] );
		if ( region == null ) {
			msg( sender, "shaders.notification.invalid-region" );
			return;
		}
		RegionManager.getInstance().removeRegion( args[ 1 ] );
		msg( sender, "shaders.command.destroyed", args[ 1 ] );
	}
	
	private void cmd_add( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.shader.add" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		
		MapRegion region = RegionManager.getInstance().getRegion( args[ 1 ] );
		if ( region == null ) {
			msg( sender, "shaders.notification.invalid-region" );
			return;
		}
		Player player = Bukkit.getPlayer( args[ 2 ] );
		if ( player == null ) {
			msg( sender, "shaders.notification.invalid-player", args[ 2 ] );
			return;
		}
		region.addViewer( player );
		msg( sender, "shaders.command.added", args[ 2 ], args[ 1 ] );
	}
	
	private void cmd_remove( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.shader.remove" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		
		MapRegion region = RegionManager.getInstance().getRegion( args[ 1 ] );
		if ( region == null ) {
			msg( sender, "shaders.notification.invalid-region" );
			return;
		}
		Player player = Bukkit.getPlayer( args[ 2 ] );
		if ( player == null ) {
			msg( sender, "shaders.notification.invalid-player", args[ 2 ] );
			return;
		}
		region.removeViewer( player );
		msg( sender, "shaders.command.removed", args[ 2 ], args[ 1 ] );
	}
	
	// I got tired of writing boilerplate code
	private void msg( CommandSender sender, String unlocalized, Object... params ) {
		CLogger.msg( sender, "header", CLogger.parse( sender, "shaders.name" ), CLogger.parse( sender, unlocalized, params ) );
	}
}
