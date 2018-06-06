package io.github.bananapuncher714.cartographer.commands;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import com.google.common.io.Files;

import io.github.bananapuncher714.cartographer.CPerms;
import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.Module;
import io.github.bananapuncher714.cartographer.api.objects.ChunkLocation;
import io.github.bananapuncher714.cartographer.api.util.ImageUtil;
import io.github.bananapuncher714.cartographer.inv.CartographerBananaManager;
import io.github.bananapuncher714.cartographer.listeners.ChunkLoadListener;
import io.github.bananapuncher714.cartographer.listeners.MapInteractListener;
import io.github.bananapuncher714.cartographer.map.core.BorderedMap;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.MapLoader;
import io.github.bananapuncher714.citizens.inv.CitizensBananaManager;
import io.github.bananapuncher714.imager.ImagerAddon;
import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.locale.inv.LocaleBananaManager;

public class CartographerExecutor implements CommandExecutor {

	@Override
	public boolean onCommand( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		if ( arg3.length > 0 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "addimage" ) ) {
				cmd_image( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "open" ) ) {
				cmd_inventory( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "addoverlay" ) ) {
				cmd_overlay( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "locale" ) ) {
				cmd_locale( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "npc" ) ) {
				cmd_npc( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "reload" ) ) {
				cmd_reload( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "head" ) ) {
				cmd_head( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "overlay" ) ) {
				cmd_overlayer( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "chunkreload" ) ) {
				cmd_chunkreload( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "give" ) ) {
				cmd_give( arg0, arg3 );
			} else if ( arg3[ 0 ].equalsIgnoreCase( "delete" ) ) {
				cmd_delete( arg0, arg3 );
			} else {
				CLogger.msg( arg0, "main.command.usage" );
			}
		} else {
			cmd_inventory( arg0, arg3 );
		}
		return false;
	}
	
	private void cmd_chunkreload( CommandSender arg0, String[] arg3 ) {
		if ( arg0 instanceof Player ) {
			if ( !CPerms.isAdmin( arg0 ) ) {
				CLogger.msg( arg0, "header", CLogger.parse( arg0, "main.name" ), CLogger.parse( arg0, "main.command.no-permission" ) );
				return;
			}
			Player player = ( Player ) arg0;
			ChunkLoadListener.addChunkToRenderQueue( new ChunkLocation( player.getLocation().getChunk() ) );
		} else {
			CLogger.msg( arg0, "header", CLogger.parse( arg0, "main.name" ), CLogger.parse( arg0, "main.command.must-be-player" ) );
		}
	}

	@SuppressWarnings( "deprecation" )
	private void cmd_image( CommandSender s, String[] a ) {
		if ( !CPerms.isAdmin( s ) ) {
			CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.command.no-permission" ) );
			return;
		}
		int height = 0, width = 0;
		boolean dither = false;
		if ( a.length == 3 ) {
		} else if ( a.length == 4 ) {
			try {
				dither = Boolean.parseBoolean( a[ 3 ] );
			}  catch ( Exception exception ) {
				CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.command.addimage-usage" ) );
				return;
			}
		} else if ( a.length == 6 ) {
			try {
				dither = Boolean.parseBoolean( a[ 3 ] );
				width = Math.max( Integer.parseInt( a[ 4 ] ), 128 );
				height = Math.max( Integer.parseInt( a[ 5 ] ), 128 );
			} catch ( Exception exception ) {
				CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.command.addimage-usage" ) );
				return;
			}
		} else {
			CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.command.addimage-usage" ) );
			return;
		}
		final int h = height, w = width;
		final boolean d = dither;
		CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.saving-image" ) );
		Bukkit.getScheduler().scheduleAsyncDelayedTask( Cartographer.getMain(), new Runnable() {
			@Override
			public void run() {
				BufferedImage image = null;
				try {
					image = ImageUtil.getImage( a[ 2 ] );
				} catch ( MalformedURLException exception ) {
					CLogger.msg( s, "header" , CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.invalid-url" ) );
					return;
				} catch ( IOException exception ) {
					CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.download-error" ) );
					exception.printStackTrace();
					return;
				}
				int smallest = Math.min( image.getHeight(), image.getWidth() );
				if ( h != 0 && w != 0 ) {
					image = ImageUtil.toBufferedImage( image.getScaledInstance( w, h, Image.SCALE_SMOOTH ) );
				} else if ( smallest < 128 ) {
					image = ImageUtil.toBufferedImage( image.getScaledInstance( ( image.getWidth() * 128 ) / smallest, ( image.getHeight() * 128 ) / smallest, Image.SCALE_SMOOTH ) );
				}
				
				if ( d ) ImageUtil.dither( image );
				
				File map = ImageUtil.saveImage( image, new File( Cartographer.getMain().getDataFolder() + "/maps/" ), a[ 1 ] );
				if ( map != null ) {
					CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.saved-image" ) );
				} else {
					CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.save-error" ) );
				}
			}
		} );
	}
	
	@SuppressWarnings( "deprecation" )
	private void cmd_overlay( CommandSender s, String[] a ) {
		if ( !CPerms.isAdmin( s ) ) {
			CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.command.no-permission" ) );
			return;
		}
		boolean dither = false;
		if ( a.length != 3 ) {
			CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.command.addoverlay-usage" ) );
			return;
		}
		final boolean d = dither;
		CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.saving-image" ) );
		Bukkit.getScheduler().scheduleAsyncDelayedTask( Cartographer.getMain(), new Runnable() {
			@Override
			public void run() {
				BufferedImage image = null;
				try {
					image = ImageUtil.getImage( a[ 2 ] );
				} catch ( MalformedURLException exception ) {
					CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.invalid-url" ) );
					return;
				} catch ( IOException exception ) {
					CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.download-error" ) );
					exception.printStackTrace();
					return;
				}
				image = ImageUtil.toBufferedImage( image.getScaledInstance( 128, 128, Image.SCALE_SMOOTH ) );
				
				if ( d ) ImageUtil.dither( image );
				
				File map = ImageUtil.saveImage( image, new File( Cartographer.getMain().getDataFolder() + "/maps/overlays/" ), a[ 1 ], true );
				if ( map != null ) {
					CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.saved-image" ) );
				} else {
					CLogger.msg( s, "header", CLogger.parse( s, "main.name" ), CLogger.parse( s, "main.notification.save-error" ) );
				}
			}
		} );
	}
	
	private void cmd_inventory( CommandSender sender, String[] args ) {
		if ( !CPerms.inventoryEdit( sender ) ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.no-permission" ) );
			return;
		}
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			CustomInventory inv = CartographerBananaManager.getCustomInventory( player, "MapSelector" );
			player.openInventory( inv.getInventory( false ) );
		} else {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.must-be-player" ) );
		}
	}
	
	private void cmd_locale( CommandSender sender, String[] args ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			player.openInventory( LocaleBananaManager.getCustomInventory( player, "LocaleSelector" ).getInventory( false ) );
		} else {
			CLogger.msg( sender, "header", CLogger.parse( sender, "locale.name" ), CLogger.parse( sender, "main.command.must-be-player" ) );
		}
	}
	
	private void cmd_npc( CommandSender sender, String args[] ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			Minimap map = MapManager.getInstance().getPlayerMap( player );
			if ( !player.hasPermission( "cartographer.citizens.inventory" ) ) {
				CLogger.msg( player, "header", CLogger.parse( sender, "citizens.name" ), CLogger.parse( sender, "main.command.no-permission" ) );
				return;
			}
			if ( map == null ) {
				CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.no-map-found" ) );
				return;
			}
			if ( !map.getModules().containsKey( "citizens" ) ) {
				CLogger.msg( sender, "header", CLogger.parse( sender, "citizens.name" ), CLogger.parse( sender, "main.command.disabled-module" ) );
				return;
			}
			player.openInventory( CitizensBananaManager.getCustomInventory( player, "CitizensManager" ).getInventory( false ) );
		} else {
			CLogger.msg( sender, "header", CLogger.parse( sender, "citizens.name" ), CLogger.parse( sender, "main.command.must-be-player" ) );
		}
	}
	
	private void cmd_reload( CommandSender sender, String args[] ) {
		if ( !CPerms.isAdmin( sender ) ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.no-permission" ) );
			return;
		}
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			Minimap map = MapManager.getInstance().getPlayerMap( player );
			if ( map == null ) {
				CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.no-map-found" ) );
				return;
			}
			MapManager.getInstance().getMinimaps().remove( map.getUID() );
			if ( map instanceof BorderedMap ) {
				( ( BorderedMap ) map ).disable();
			}
			map = MapLoader.getMap( new File( Cartographer.getMain().getDataFolder() + "/saves/" + map.getId() ) );
			MapManager.getInstance().getMinimaps().put( map.getUID(), map );
		}
		Cartographer.getMain().reload();
		CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.config-reloaded" ) );
	}
	
	private void cmd_head( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.playerheads.shader" ) ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.no-permission" ) );
			return;
		}
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			Minimap map = MapManager.getInstance().getPlayerMap( player );
			if ( map == null ) {
				CLogger.msg( player, "header", CLogger.parse( player, "playerheads.name" ), CLogger.parse( player, "main.notification.no-map-found" ) );
				return;
			}
			Module addon = map.getModules().get( "playerheads" );
			if ( addon != null && addon instanceof ImagerAddon ) {
				CLogger.msg( player, "header", CLogger.parse( player, "playerheads.name" ), CLogger.parse( player, "playerheads.notification.toggled-visibility" ) );
				ImagerAddon imager = ( ImagerAddon ) addon;
				imager.toggle( player );
			} else {
				CLogger.msg( sender, "header", CLogger.parse( sender, "playerheads.name" ), CLogger.parse( sender, "main.command.disabled-module" ) );
				return;
			}
		} else {
			CLogger.msg( sender, "header", CLogger.parse( sender, "playerheads.name" ), CLogger.parse( sender, "main.command.must-be-player" ) );
		}
	}
	
	private void cmd_overlayer( CommandSender sender, String[] a ) {
		if ( !sender.hasPermission( "cartographer.main.overlay" ) ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.no-permission" ) );
			return;
		}
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			Minimap map = MapManager.getInstance().getPlayerMap( player );
			if ( map == null ) {
				CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.no-map-found" ) );
				return;
			}
			if ( map.isForceOverlay() ) {
				CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.no-overlay-4-u" ) );
				return;
			}
			player.openInventory( CartographerBananaManager.getCustomInventory( player, "OverlaySelector" ).getInventory( false ) );
		} else {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.must-be-player" ) );
		}
	}
	
	private void cmd_give( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.admin" ) ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.no-permission" ) );
			return;
		}
		if ( args.length != 4 ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.give-usage" ) );
			return;
		}
		
		Player player = Bukkit.getPlayer( args[ 1 ] );
		if ( player == null ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.specify-player", args[ 1 ] ) );
			return;
		}
		Minimap fmap = null;
		for ( Minimap map : MapManager.getInstance().getMinimaps().values() ) {
			if ( map.getId().equals( args[ 2 ] ) ) {
				fmap = map;
			}
		}
		if ( fmap == null ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.invalid-map", args[ 2 ] ) );
			return;
		}
		try {
			int slot = Integer.valueOf( args[ 3 ] );
			ItemStack mapItem = new ItemStack( Material.MAP );
			MapView newView = Bukkit.createMap( fmap.getWorld() );
			mapItem.setDurability( newView.getId() );
			MapManager.getInstance().getMapView( mapItem, false );
			MapInteractListener.renameMap( fmap, mapItem, Scale.CLOSE, player );
			player.getInventory().setItem( slot, mapItem );
		} catch ( Exception exception ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.give-usage" ) );
			return;
		}
	}
	
	private void cmd_delete( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "cartographer.admin" ) ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.no-permission" ) );
			return;
		}
		if ( args.length < 2 ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.delete-help" ) );
			return;
		}
		Minimap fmap = MapManager.getInstance().getMinimap( args[ 1 ] );
		if ( fmap == null ) {
			CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.command.invalid-map", args[ 1 ] ) );
			return;
		}
		MapManager.getInstance().stop( fmap.getUID() );
		final Minimap finMap = fmap;
		Bukkit.getScheduler().scheduleAsyncDelayedTask( Cartographer.getMain(), new Runnable() {
			@Override
			public void run() {
				try {
					FileUtils.deleteDirectory( finMap.getDataFolder() );
					CLogger.info( "Deleted " + finMap.getId() + " successfully!");
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}, 1 );
		CLogger.msg( sender, "header", CLogger.parse( sender, "main.name" ), CLogger.parse( sender, "main.notification.deleted-map", fmap.getId() ) );
	}
}
