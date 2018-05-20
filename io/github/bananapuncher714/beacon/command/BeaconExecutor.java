package io.github.bananapuncher714.beacon.command;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.beacon.Beacon;
import io.github.bananapuncher714.beacon.BeaconAddon;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.command.OptionParser;

public class BeaconExecutor implements CommandExecutor {
	private final Map< String, String > parameters = new HashMap< String, String >();

	public BeaconExecutor() {
		parameters.put( "-m", "MAP_NAME" );
		parameters.put( "-map", "MAP_NAME" );
		parameters.put( "-l", "LOCATION" );
		parameters.put( "-location", "LOCATION" );
		parameters.put( "-c", "COLOR" );
		parameters.put( "-color", "COLOR" );
		parameters.put( "-t", "TIME" );
		parameters.put( "-time", "TIME" );
		parameters.put( "-r", "RADIUS" );
		parameters.put( "-radius", "RADIUS" );
		parameters.put( "-s", "SPEED" );
		parameters.put( "-speed", "SPEED" );
		parameters.put( "-v", "VISIBLE" );
		parameters.put( "-visible", "VISIBLE" );
	}

	@Override
	public boolean onCommand( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		if ( arg3.length == 0 ) {
			cmd_help( arg0 );
		} else if ( arg3.length == 1 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "add" ) || arg3[ 0 ].equalsIgnoreCase( "remove" ) ) {
				msg( arg0, "beacons.command.usage-add-remove" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "create" ) ) {
				msg( arg0, "beacons.command.usage-create" );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "destroy" ) ) {
				msg( arg0, "beacons.command.usage-destroy" );
			} else {
				cmd_help( arg0 );
			}
		} else if ( arg3.length == 2 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "create" ) ) {
				cmd_create( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "destroy" ) ) {
				cmd_destroy( arg0, arg3 );
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
		msg( sender, "beacons.command.usage" );
	}

	private void cmd_create( CommandSender sender, String... args ) {
		if ( !sender.hasPermission( "cartographer.beacon.create" ) ) {
			msg( sender, "main.command.no-permission" );
			return ;
		}
		Map< String, String > arguments = new HashMap< String, String >();

		arguments.put( "COLOR", "16777215" );
		arguments.put( "TIME", "200" );
		arguments.put( "RADIUS", "10" );
		arguments.put( "SPEED", "1" );
		arguments.put( "VISIBLE", "true" );
		
		Minimap map;
		OptionParser.parseArguments( parameters, arguments, 1, false, args );
		if ( !arguments.containsKey( "MAP_NAME" ) ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				map = MapManager.getInstance().getPlayerMap( player );
				if ( map == null ) {
					msg( sender, "beacons.notification.specify-map" );
					return;
				}
			} else {
				msg( sender, "beacons.notification.specify-map" );
				return;
			}
		} else {
			map = MapManager.getInstance().getMinimap( arguments.get( "MAP_NAME" ) );
			if ( map == null ) {
				msg( sender, "beacons.notification.invalid-map" );
			}
		}

		Location beaconLoc;
		if ( !arguments.containsKey( "LOCATION" ) ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				beaconLoc = player.getLocation();
			} else {
				msg( sender, "beacons.notification.specify-location" );
				return;
			}
		} else {
			try {
				String[] loc = arguments.get( "LOCATION" ).split( ":" );
				beaconLoc = new Location( map.getWorld(), Double.parseDouble( loc[0 ] ), 0, Double.parseDouble( loc[ 1 ] ) );
			} catch ( Exception exception ) {
				msg( sender, "beacons.notification.invalid-argument" );
				return;
			}
		}

		Beacon beacon;
		try {
			beacon = new Beacon( args[ 1 ], new Color( Integer.parseInt( arguments.get( "COLOR" ) ) ), Integer.parseInt( arguments.get( "RADIUS" ) ), Double.parseDouble( arguments.get( "SPEED" ) ), beaconLoc, Boolean.parseBoolean( arguments.get( "VISIBLE" ) ) );
		} catch ( Exception exception ) {
			msg( sender, "beacons.notification.invalid-argument" );
			return;
		}
		Module addon = map.getModules().get( "beacon" );
		if ( addon == null ) {
			msg( sender, "beacons.notification.invalid-argument" );
			return;
		}
		BeaconAddon beaconAddon = ( BeaconAddon ) addon;
		try {
			beaconAddon.addBeacon( beacon, Integer.parseInt( arguments.get( "TIME" ) ) );
		} catch ( Exception exception ) {
			msg( sender, "beacons.notification.invalid-argument" );
			return;
		}
		msg( sender, "beacons.command.created", args[ 1 ] );
		return;
	}
	
	private void cmd_destroy( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.beacon.destroy" ) ) {
			msg( sender, "main.command.no-permission" );
			return ;
		}
		Minimap map = null;
		if ( args.length == 2 ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				map = MapManager.getInstance().getPlayerMap( player );
				if ( map == null ) {
					msg( sender, "beacons.notification.specify-map" );
					return;
				}
			} else {
				msg( sender, "beacons.notification.specify-map" );
				return;
			}
		} else {
			map = MapManager.getInstance().getMinimap( args[ 2 ] );
			if ( map == null ) {
				msg( sender, "beacons.notification.invalid-map" );
			}
		}
		Module addon = map.getModules().get( "beacon" );
		if ( addon == null ) {
			msg( sender, "beacons.notification.not-enabled" );
			return;
		}
		BeaconAddon beaconAddon = ( BeaconAddon ) addon;
		beaconAddon.removeBeacon( args[ 1 ] );
		msg( sender, "beacons.command.destroyed", args[ 1 ] );
	}
	
	private void cmd_add( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.beacon.add" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		Minimap map = null;
		if ( args.length == 3 ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				map = MapManager.getInstance().getPlayerMap( player );
				if ( map == null ) {
					msg( sender, "beacons.notification.specify-map" );
					return;
				}
			} else {
				msg( sender, "beacons.notification.specify-map" );
				return;
			}
		} else {
			map = MapManager.getInstance().getMinimap( args[ 3 ] );
			if ( map == null ) {
				msg( sender, "beacons.notification.invalid-map" );
				return;
			}
		}
		Module addon = map.getModules().get( "beacon" );
		if ( addon == null ) {
			msg( sender, "beacons.notification.not-enabled" );
			return;
		}
		Player player = Bukkit.getPlayer( args[ 2 ] );
		if ( player == null ) {
			msg( sender, "beacons.notification.invalid-player", args[ 2 ] );
			return;
		}
		
		BeaconAddon beaconAddon = ( BeaconAddon ) addon;
		beaconAddon.add( player, args[ 1 ] );
		msg( sender, "beacons.command.added", args[ 2 ], args[ 1 ] );
	}
	
	private void cmd_remove( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.beacon.remove" ) ) {
			msg( sender, "main.command.no-permission" );
			return;
		}
		Minimap map = null;
		if ( args.length == 3 ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				map = MapManager.getInstance().getPlayerMap( player );
				if ( map == null ) {
					msg( sender, "beacons.notification.specify-map" );
					return;
				}
			} else {
				msg( sender, "beacons.notification.specify-map" );
				return;
			}
		} else {
			map = MapManager.getInstance().getMinimap( args[ 3 ] );
			if ( map == null ) {
				msg( sender, "beacons.notification.invalid-map" );
				return;
			}
		}
		Module addon = map.getModules().get( "beacon" );
		if ( addon == null ) {
			msg( sender, "beacons.notification.not-enabled" );
			return;
		}
		Player player = Bukkit.getPlayer( args[ 2 ] );
		if ( player == null ) {
			msg( sender, "beacons.notification.invalid-player", args[ 2 ] );
			return;
		}
		
		BeaconAddon beaconAddon = ( BeaconAddon ) addon;
		beaconAddon.remove( player, args[ 1 ] );
		msg( sender, "beacons.command.removed", args[ 2 ], args[ 1 ] );
	}
	
	// I got tired of writing boilerplate code
	private void msg( CommandSender sender, String unlocalized, Object... params ) {
		CLogger.msg( sender, "header", CLogger.parse( sender, "beacons.name" ), CLogger.parse( sender, unlocalized, params ) );
	}
}
