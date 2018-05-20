package io.github.bananapuncher714.waypoints.inv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.CustomMenu;
import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.actionItem.ActionItemIntention;
import io.github.bananapuncher714.inventory.actionItem.ButtonItem;
import io.github.bananapuncher714.inventory.components.BananaButton;
import io.github.bananapuncher714.inventory.components.BoxPanel;
import io.github.bananapuncher714.inventory.components.ButtonComponent;
import io.github.bananapuncher714.inventory.items.ItemBuilder;
import io.github.bananapuncher714.inventory.panes.OptionPane;
import io.github.bananapuncher714.inventory.util.ICEResponse;
import io.github.bananapuncher714.waypoints.WPerms;
import io.github.bananapuncher714.waypoints.Waypoint;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.waypoints.WaypointManager;
import io.github.bananapuncher714.waypoints.WaypointViewer;
import io.github.bananapuncher714.waypoints.WaypointViewer.DisplayType;

public class WaypointEditor {
	private static HashMap< UUID, Object[] > waypoints = new HashMap< UUID, Object[] >();
	
	public enum WType {
		PUBLIC, PRIVATE, SHARED;
	}
	
	public static CustomInventory getInventory( Player player ) {
		Object[] objects = waypoints.get( player.getUniqueId() );
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
				return null;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			return null;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		Waypoint waypoint = addon.getManager().getWaypoint( ( UUID ) objects[ 0 ] );
		WType custom = ( WType ) objects[ 1 ];
		CustomMenu menu = new CustomMenu( "WaypointEditor", 2, CLogger.parse( player, "waypoints.inventory.editor.title" ) );
		ButtonComponent button = new BananaButton( "waypoint", 4 );
		ItemBuilder builder = new ItemBuilder( Material.BANNER, 1, ( byte ) ( new Random().nextInt() % 16 ), CLogger.parse( player, "waypoints.inventory.editor.items.waypoint.name", waypoint.getName() ), CLogger.parse( player, "waypoints.inventory.editor.items.waypoint.line-1", waypoint.getCreatorName() ) );
		button.setItem( new ButtonItem( "nothing", "nothing", ActionItemIntention.NONE, builder.getItem() ) );
		BoxPanel optionPanel = new BoxPanel( "Option Panel", 9 );
		OptionPane options = new OptionPane( "options" );
		setPane( addon.getManager(), player, options, custom, waypoint );
		optionPanel.addPane( options );
		menu.addComponent( button, optionPanel );
		return menu;
	}
	
	public static boolean executeInventoryClickEvent( InventoryClickEvent event, CustomInventory inventory ) {
		// Cancel the event if they try to shift click or swap items in the inventory
        if ( event.getClick().isKeyboardClick() || event.getClick().isShiftClick() ) event.setCancelled( true );
        // Return if they didn't click inside the inventory
        if ( event.getSlot() != event.getRawSlot() ) return false;
        Player player = ( Player ) event.getWhoClicked();
        ICEResponse response = inventory.parseICE( event );
        // Return if they didn't click on anything
        if ( response.getActionItem() == null ) return false;
        event.setCancelled( true );
        ActionItem item = response.getActionItem();
        ArrayList< String > actions = new ArrayList< String >( item.getActions() );
        Object[] objects = waypoints.get( player.getUniqueId() );
		WType type = ( WType ) objects[ 1 ];
        
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
				return false;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			return false;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		Waypoint waypoint = addon.getManager().getWaypoint( ( UUID ) objects[ 0 ] );
		WaypointViewer viewer = addon.getManager().getViewer( player );
		
        if ( actions.contains( "discover" ) ) {
        	if ( event.getClick() == ClickType.RIGHT ) {
        		waypoint.setDiscover( waypoint.getRange() + 1 );
        	} else if ( event.getClick() == ClickType.LEFT ) {
        		waypoint.setDiscover( Math.max( 0, waypoint.getRange() - 1 ) );
        	} else if ( event.getClick() == ClickType.SHIFT_RIGHT ) {
        		waypoint.setDiscover( waypoint.getRange() + 5 );
        	} else if ( event.getClick() == ClickType.SHIFT_LEFT ) {
        		waypoint.setDiscover( Math.max( 0, waypoint.getRange() - 5 ) );
        	} else if ( event.getClick() == ClickType.DROP ) {
        		if ( !addon.getManager().getDiscoverable().contains( waypoint.getId() ) ) {
            		addon.getManager().getDiscoverable().add( waypoint.getId() );
            	} else {
            		addon.getManager().getDiscoverable().remove( waypoint.getId() );
            	}
        	}
        }
		if ( actions.contains( "type" ) ) {
			waypoint.setType( WaypointManagerInventory.getNextType( waypoint.getType() ) );
        }
        if ( actions.contains( "visibility" ) ) {
        	if ( type == WType.PRIVATE ) viewer.getWaypoints().put( waypoint, WaypointManagerInventory.getNextDisplay( viewer.getWaypoints().get( waypoint ) ) );
			if ( type == WType.SHARED ) viewer.getShared().put( waypoint, WaypointManagerInventory.getNextDisplay( viewer.getShared().get( waypoint ) ) );
			if ( type == WType.PUBLIC ) viewer.getPublic().put( waypoint.getId(), WaypointManagerInventory.getNextDisplay( viewer.getPublic().get( waypoint.getId() ) ) );

        }
        if ( actions.contains( "share" ) ) {
        	WaypointPlayerChooser.setWaypoint( player.getUniqueId(), waypoint );
			CustomInventory inv = WaypointBananaManager.getCustomInventory( player, "WaypointPlayerChooser" );
			if ( inv == null ) {
				player.closeInventory();
				CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.command.disabled-module" ) );
				return false;
			}
			player.openInventory( inv.getInventory( true ) );
        }
        if ( actions.contains( "delete" ) ) {
        	CLogger.msg( player, "header", CLogger.parse( player, "waypoints.name" ), CLogger.parse( player, "waypoints.notification.deleted-waypoint", waypoint.getName() ) );
			waypoint.remove();
			viewer.getWaypoints().remove( waypoint );
			CustomInventory inv = WaypointBananaManager.getCustomInventory( player, "WaypointManagerInventory" );
			player.openInventory( inv.getInventory( false ) );
			return true;
        }
        if ( actions.contains( "teleport" ) ) {
        	if ( waypoint.teleportable() && WPerms.canTeleport( player ) ) {
    			player.teleport( waypoint.getLocation() );
    		}
        }
        if ( actions.contains( "public" ) ) {
        	if ( WPerms.canPublic( player ) ) {
				if ( addon.getManager().isPublic( waypoint ) ) {
					addon.getManager().removePublicWaypoint( waypoint );
				} else {
					addon.getManager().addPublicWaypoint( waypoint );
				}
			}
        }
        if ( actions.contains( "staff" ) ) {
        	if ( WPerms.hasStaff( player ) ) {
				addon.getManager().setStaff( waypoint, !addon.getManager().isStaff( waypoint ) );
			}
        }
        if ( actions.contains( "lock" ) ) {
        	if ( WPerms.canLock( player ) ) {
        		waypoint.setUnToggleable( !waypoint.isUnToggleable() );
        	}
        }
        BoxPanel panel = ( BoxPanel ) inventory.getComponent( "Option Panel" );
        OptionPane pane = ( OptionPane ) panel.getPanes().get( "options" );
        setPane( addon.getManager(), player, pane, type, waypoint );
        inventory.updateInventory();
		return false;
	}
	
	public static boolean executeInventoryCloseEvent( InventoryCloseEvent event, CustomInventory inventory ) {
		waypoints.remove( event.getPlayer().getUniqueId() );
		return true;
	}
	
	public static void setWaypoint( Player player, Waypoint waypoint, WType playerOwns ) {
		waypoints.put( player.getUniqueId(), new Object[] { waypoint.getId(), playerOwns } );
	}

	private static void setPane( WaypointManager manager, Player player, OptionPane options, WType custom, Waypoint waypoint ) {
		options.getContents().clear();
		for ( int i = 0; i < 9; i++ ) {
			if ( i == 0 && WPerms.canDiscoverable( player ) ) {
				ItemBuilder builder = new ItemBuilder( Material.EMPTY_MAP, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.discover.name" ) );
				String[] lore = new String[ 8 ];
				lore[ 0 ] = CLogger.parse( player, "waypoints.inventory.editor.items.discover.line-1", manager.isDiscoverable( waypoint ) );
				lore[ 1 ] = CLogger.parse( player, "waypoints.inventory.editor.items.discover.line-2", waypoint.getRange() );
				lore[ 2 ] = "";
				lore[ 3 ] = CLogger.parse( player, "waypoints.inventory.editor.items.discover.line-3" );
				lore[ 4 ] = CLogger.parse( player, "waypoints.inventory.editor.items.discover.line-4" );
				lore[ 5 ] = CLogger.parse( player, "waypoints.inventory.editor.items.discover.line-5" );
				lore[ 6 ] = CLogger.parse( player, "waypoints.inventory.editor.items.discover.line-6" );
				lore[ 7 ] = CLogger.parse( player, "waypoints.inventory.editor.items.discover.line-7" );
				builder.setLore( lore );
				options.addActionItem( new ActionItem( "nothing", "discover", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 1 && ( custom == WType.PRIVATE || WPerms.hasAdmin( player ) ) ) {
				ItemBuilder builder = new ItemBuilder( Material.ANVIL, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.icon.name" ), CLogger.parse( player, "waypoints.inventory.editor.items.icon.line-1", waypoint.getType().name().replaceAll( "_", " " ) ) );
				options.addActionItem( new ActionItem( "nothing", "type", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 2 && ( !waypoint.isUnToggleable() || WPerms.canLock( player ) )) {
				WaypointViewer viewer = manager.getViewer( player );
				DisplayType type;
				if ( custom == WType.PRIVATE ) type = viewer.getWaypoints().get( waypoint );
				else if ( custom == WType.PUBLIC ) type = viewer.getPublic().get( waypoint.getId() );
				else type = viewer.getShared().get( waypoint );
				ItemBuilder builder = new ItemBuilder( Material.EYE_OF_ENDER, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.visibility.name" ), CLogger.parse( player, "waypoints.inventory.editor.items.visibility.line-1", type.getDisplayName() ) );
				options.addActionItem( new ActionItem( "nothing", "visibility", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 3 && custom == WType.PRIVATE ) {
				ItemBuilder builder = new ItemBuilder( Material.CAKE, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.share.name" ), CLogger.parse( player, "waypoints.inventory.editor.items.share.line-1", waypoint.getShared().size() ) );
				options.addActionItem( new ActionItem( "nothing", "share", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 4 && custom == WType.PRIVATE ) {
				ItemBuilder builder = new ItemBuilder( Material.BARRIER, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.delete.name" ) );
				options.addActionItem( new ActionItem( "nothing", "delete", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 5 && WPerms.canTeleport( player ) && waypoint.teleportable() ) {
				ItemBuilder builder = new ItemBuilder( Material.ENDER_PEARL, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.teleport.name" ), CLogger.parse( player, "waypoints.inventory.editor.items.teleport.line-1", waypoint.getLocation().getX() ), CLogger.parse( player, "waypoints.inventory.editor.items.teleport.line-2", waypoint.getLocation().getY() ), CLogger.parse( player, "waypoints.inventory.editor.items.teleport.line-3", waypoint.getLocation().getZ() ) );
				options.addActionItem( new ActionItem( "nothing", "teleport", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 6 && WPerms.canPublic( player ) ) {
				ItemBuilder builder = new ItemBuilder( Material.STORAGE_MINECART, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.public.name" ), CLogger.parse( player, "waypoints.inventory.editor.items.public.line-1", manager.isPublic( waypoint ) ) );
				options.addActionItem( new ActionItem( "nothing", "public", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 7 && WPerms.hasStaff( player ) ) {
				ItemBuilder builder = new ItemBuilder( Material.COMMAND_MINECART, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.staff.name" ), CLogger.parse( player, "waypoints.inventory.editor.items.staff.line-1", manager.isStaff( waypoint ) ) );
				options.addActionItem( new ActionItem( "nothing", "staff", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 8 && WPerms.canLock( player ) ) {
				ItemBuilder builder = new ItemBuilder( Material.TRIPWIRE_HOOK, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.editor.items.lock-visibility.name" ), CLogger.parse( player, "waypoints.inventory.editor.items.lock-visibility.line-1", waypoint.isUnToggleable() ) );
				options.addActionItem( new ActionItem( "nothing", "lock", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else {
				ItemBuilder builder = new ItemBuilder( Material.STAINED_GLASS_PANE, 1, ( byte ) 15, " " );
				options.addActionItem( new ActionItem( "nothing", "nothing", ActionItemIntention.NONE, builder.getItem() ) );
			}
		}
	}
}
