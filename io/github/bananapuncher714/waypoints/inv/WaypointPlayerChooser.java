package io.github.bananapuncher714.waypoints.inv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
import io.github.bananapuncher714.inventory.items.ItemBuilder;
import io.github.bananapuncher714.inventory.items.SkullBuilder;
import io.github.bananapuncher714.inventory.panes.OptionPane;
import io.github.bananapuncher714.inventory.panes.PagedOptionPane;
import io.github.bananapuncher714.inventory.util.ICEResponse;
import io.github.bananapuncher714.waypoints.Waypoint;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.waypoints.WaypointManager;
import io.github.bananapuncher714.waypoints.WaypointViewer;
import io.github.bananapuncher714.waypoints.WaypointViewer.DisplayType;

public class WaypointPlayerChooser {

	private static HashMap< UUID, Waypoint > tempStore = new HashMap< UUID, Waypoint >();
	
	public static CustomInventory getInventory( Player player ) {
		CustomInventory menu = new CustomMenu( "WaypointPlayerChooser", 4, CLogger.parse( player, "waypoints.inventory.share-waypoint.title" ) );
		Waypoint waypoint = tempStore.get( player.getUniqueId() );
		waypoint.getShared();
		BoxPanel selectedPanel = new BoxPanel( "Shared",0 );
		BoxPanel playerPanel = new BoxPanel( "Players", 5 );
		BoxPanel divider = new BoxPanel( "Divider", 4 );
		OptionPane div = new OptionPane( "divider" );
		for ( int i = 0; i < 4; i++ ) {
			ItemBuilder builder = new ItemBuilder( Material.STAINED_GLASS_PANE, 1, ( byte ) 11, " " );
			div.addActionItem( new ActionItem( "NOTHING", "NOTHING", ActionItemIntention.NONE, builder.getItem() ) );
		}
		divider.addPane( div );
		

		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
				return null;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			return null;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		WaypointManager manager = addon.getManager();
		
		PagedOptionPane shared = new PagedOptionPane( "sheads" );
		PagedOptionPane others = new PagedOptionPane( "uheads" );
		selectedPanel.addPane( shared );
		playerPanel.addPane( others );
		fillPanes( manager, player, shared, others, waypoint );
		
		BananaButton np1 = new BananaButton( "next page", 31 );
		np1.setItem( new ButtonItem( "next", "change page 1", ActionItemIntention.NEXT, new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.share-waypoint.buttons.next-page" ) ).getItem() ) );
		BananaButton pp1 = new BananaButton( "prev page", 29 );
		pp1.setItem( new ButtonItem( "prev", "change page 1", ActionItemIntention.PREVIOUS, new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.share-waypoint.buttons.previous-page" ) ).getItem() ) );
		BananaButton np2 = new BananaButton( "next page", 35 );
		np2.setItem( new ButtonItem( "next", "change page 2", ActionItemIntention.NEXT, new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.share-waypoint.buttons.next-page" ) ).getItem() ) );
		BananaButton pp2 = new BananaButton( "prev page", 32 );
		pp2.setItem( new ButtonItem( "prev", "change page 2", ActionItemIntention.PREVIOUS, new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.share-waypoint.buttons.previous-page" ) ).getItem() ) );
		
		shared.addButtons( np1, pp1 );
		others.addButtons( np2, pp2 );
		menu.addComponent( selectedPanel, playerPanel, divider, np1, pp1, np2, pp2 );
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
        Waypoint waypoint = tempStore.get( player.getUniqueId() );
        BoxPanel panel1 = ( BoxPanel ) inventory.getComponent( "Shared" );
        BoxPanel panel2 = ( BoxPanel ) inventory.getComponent( "Players" );
        PagedOptionPane p1 = ( PagedOptionPane ) panel1.getPanes().get( "sheads" );
        PagedOptionPane p2 = ( PagedOptionPane ) panel2.getPanes().get( "uheads" );

        Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
				return false;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			return false;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		WaypointManager manager = addon.getManager();
        
        if ( actions.contains( "change page 1" ) ) {
        	if ( item.getName().equalsIgnoreCase( "next" ) ) {
        		p1.setPage( p1.getPage() + 1 );
        	} else if ( item.getName().equalsIgnoreCase( "prev" ) ) {
        		p1.setPage( p1.getPage() - 1 );
        	}
        }
        if ( actions.contains( "change page 2" ) ) {
        	if ( item.getName().equalsIgnoreCase( "next" ) ) {
        		p2.setPage( p2.getPage() + 1 );
        	} else if ( item.getName().equalsIgnoreCase( "prev" ) ) {
        		p2.setPage( p2.getPage() - 1 );
        	}
        }
        if ( actions.contains( "add player" ) ) {
        	Player onlinePlayer = Bukkit.getPlayer( item.getName() );
        	if ( onlinePlayer != null ) {
        		WaypointViewer viewer = manager.getViewer( onlinePlayer );
        		viewer.getShared().put( waypoint, DisplayType.HIGHLIGHTED );
        		waypoint.getShared().add( viewer.getUUID() );
        	}
        }
        if ( actions.contains( "remove player" ) ) {
        	waypoint.getShared().remove( UUID.fromString( item.getName() ) );
        	WaypointViewer viewer = manager.getViewer( UUID.fromString( item.getName() ) );
        	viewer.getShared().remove( waypoint );
        }
        fillPanes( manager, player, p1, p2, waypoint );
        inventory.updateInventory();
		return false;
	}
	
	public static boolean executeInventoryCloseEvent( InventoryCloseEvent event, CustomInventory inventory ) {
		tempStore.remove( event.getPlayer().getUniqueId() );
		return true;
	}
	
	public static void setWaypoint( UUID uuid, Waypoint waypoint ) {
		tempStore.put( uuid, waypoint );
	}
	
	private static void fillPanes( WaypointManager manager, Player p, PagedOptionPane p1, PagedOptionPane p2, Waypoint w ) {
		p2.getAllContents().clear();
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			if ( player.getUniqueId().equals( w.getCreator() ) ) continue;
			if ( !w.getShared().contains( player.getUniqueId() ) ) {
				ItemBuilder builder = new SkullBuilder( CLogger.parse( p, "waypoints.inventory.share-waypoint.items.name", player.getName() ), player.getName(), 1, CLogger.parse( p, "waypoints.inventory.share-waypoint.items.add" ) );
				p2.addActionItem( new ActionItem( player.getName(), "add player", ActionItemIntention.NEXT, builder.getItem() ) );
			}
		}
		p1.getAllContents().clear();
		for ( UUID u : w.getShared() ) {
			String name = manager.getViewer( u ).getName();
			ItemBuilder builder = new SkullBuilder( CLogger.parse( p, "waypoints.inventory.share-waypoint.items.name", name ), name, 1, CLogger.parse( p, "waypoints.inventory.share-waypoint.items.add" ) );
			p1.addActionItem( new ActionItem( u.toString(), "remove player", ActionItemIntention.NEXT, builder.getItem() ) );
		}
	}
	
}
