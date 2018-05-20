package io.github.bananapuncher714.marker.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.api.util.FailSafe;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.command.OptionParser;
import io.github.bananapuncher714.marker.MarkerGroup;
import io.github.bananapuncher714.marker.UniversalMarkerManager;

public class MarkerExecutor implements CommandExecutor {
	private final Map< String, String > parameters = new HashMap< String, String >();

	public MarkerExecutor() {
		parameters.put( "-w", "WORLD" );
		parameters.put( "-world", "WORLD" );
		parameters.put( "-d", "DIRECTION" );
		parameters.put( "-direction", "DIRECTION" );
		parameters.put( "-h", "HIGHLIGHTED" );
		parameters.put( "-highlight", "HIGHLIGHTED" );
		parameters.put( "-t", "TYPE" );
		parameters.put( "-type", "TYPE" );
		parameters.put( "-l", "LOCATION" );
		parameters.put( "-location", "LOCATION" );
	}

	@Override
	public boolean onCommand( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		if ( arg3.length == 0 ) {
			cmd_help( arg0 );
		} else if ( arg3.length == 1 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "add" ) || arg3[ 0 ].equalsIgnoreCase( "remove" ) ) {
				msg( arg0, "markers.command.usage-add-remove" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "create" ) ) {
				msg( arg0, "markers.command.usage-create" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "destroy" ) ) {
				msg( arg0, "markers.command.usage-destroy" );
			} else {
				cmd_help( arg0 );
			}
		} else if ( arg3.length == 2 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "create" ) ) {
				msg( arg0, "markers.command.usage-create" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "destroy" ) ) {
				msg( arg0, "markers.command.usage-destroy" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "removeall" ) ) {
				cmd_remove_all( arg0, arg3 );
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
		msg( sender, "markers.command.usage" );
	}

	private void cmd_create( CommandSender sender, String... args ) {
		if ( !sender.hasPermission( "cartographer.beacon.create" ) ) {
			msg( sender, "main.command.no-permission" );
			return ;
		}
		Map< String, String > arguments = new HashMap< String, String >();

		arguments.put( "DIRECTION", "0" );
		arguments.put( "HIGHLIGHTED", "true" );
		arguments.put( "TYPE", "WHITE_CROSS" );

		World world;
		double x = 0, z = 0;
		Type type;
		byte direction;
		boolean highlighted;
		OptionParser.parseArguments( parameters, arguments, 2, false, args );
		if ( !arguments.containsKey( "WORLD" ) ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				world = player.getWorld();
			} else {
				msg( sender, "markers.notification.specify-world" );
				return;
			}
		} else {
			world = Bukkit.getWorld( arguments.get( "WORLD" ) );
			if ( world == null ) {
				msg( sender, "markers.notification.specify-world" );
				return;
			}
		}
		
		try {
			highlighted = Boolean.parseBoolean( arguments.get( "HIGHLIGHTED" ) );
			direction = Byte.parseByte( arguments.get( "DIRECTION" ) );
		} catch ( Exception exception ) {
			msg( sender, "markers.notification.invalid-argument" );
			return;
		}
		
		Location location;
		if ( !arguments.containsKey( "LOCATION" ) ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				location = player.getLocation();
			} else {
				msg( sender, "markers.notification.specify-location" );
				return;
			}
		} else {
			try {
				String[] loc = arguments.get( "LOCATION" ).split( ":" );
				x = Double.parseDouble( loc[ 0 ] );
				z = Double.parseDouble( loc[ 1 ] );
				location = new Location( world, x, 0, z, direction * 22.5f, 0 );
			} catch ( Exception exception ) {
				msg( sender, "markers.notification.invalid-argument" );
				return;
			}
		}

		type = FailSafe.getEnum( Type.class, arguments.get( "TYPE" ).toUpperCase() );

		RealWorldCursor cursor = new RealWorldCursor( location, type, !highlighted );
		MarkerGroup group = UniversalMarkerManager.getUMM().getGroup( args[ 1 ] );
		if ( group == null ) {
			group = new MarkerGroup( args[ 1 ] );
			UniversalMarkerManager.getUMM().registerGroup( group );
		}
		group.addMarker( args[ 2 ], cursor );
		
		msg( sender, "markers.command.created", args[ 2 ] );
	}

	private void cmd_destroy( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.markers.destroy" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		
		MarkerGroup group = UniversalMarkerManager.getUMM().getGroup( args[ 1 ] );
		if ( group == null ) {
			msg( sender, "markers.notification.invalid-group" );
			return;
		}
		group.removeCursor( args[ 2 ] );
		
		msg( sender, "markers.command.destroyed", args[ 2 ] );
	}

	private void cmd_add( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.beacon.add" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		Player player = Bukkit.getPlayer( args[ 2 ] );
		if ( player == null ) {
			msg( sender, "markers.notification.invalid-player", args[ 2 ] );
			return;
		}
		
		MarkerGroup group = UniversalMarkerManager.getUMM().getGroup( args[ 1 ] );
		if ( group == null ) {
			msg( sender, "markers.notification.invalid-group" );
			return;
		}
		group.addViewer( player );
		
		msg( sender, "markers.command.added", args[ 2 ], args[ 1 ] );
	}
	
	private void cmd_remove_all( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.markers.remove" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		Player player = Bukkit.getPlayer( args[ 1 ] );
		if ( player == null ) {
			msg( sender, "markers.notification.invalid-player", args[ 1 ] );
			return;
		}
		for ( MarkerGroup group : UniversalMarkerManager.getUMM().getGroups() ) {
			group.removeViewer( player );
			msg( sender, "markers.command.removed", args[ 1 ], group.getId() );
		}
	}

	private void cmd_remove( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.markers.remove" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		Player player = Bukkit.getPlayer( args[ 2 ] );
		if ( player == null ) {
			msg( sender, "markers.notification.invalid-player", args[ 2 ] );
			return;
		}
		
		MarkerGroup group = UniversalMarkerManager.getUMM().getGroup( args[ 1 ] );
		if ( group == null ) {
			msg( sender, "markers.notification.invalid-group" );
			return;
		}
		group.removeViewer( player );

		msg( sender, "markers.command.removed", args[ 2 ], args[ 1 ] );
	}

	private void msg( CommandSender sender, String unlocalized, Object... params ) {
		CLogger.msg( sender, "header", CLogger.parse( sender, "markers.name" ), CLogger.parse( sender, unlocalized, params ) );
	}
}
