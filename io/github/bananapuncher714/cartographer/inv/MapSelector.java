package io.github.bananapuncher714.cartographer.inv;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;

import io.github.bananapuncher714.cartographer.CPerms;
import io.github.bananapuncher714.cartographer.Cartographer;
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
import io.github.bananapuncher714.inventory.components.RevolvingPanel;
import io.github.bananapuncher714.inventory.items.ItemBuilder;
import io.github.bananapuncher714.inventory.panes.OptionPane;
import io.github.bananapuncher714.inventory.panes.PagedOptionPane;
import io.github.bananapuncher714.inventory.util.ICEResponse;

public class MapSelector {
	
	public static CustomInventory getInventory( Player player ) {
		CustomMenu menu = new CustomMenu( "MapSelector", 4, CLogger.parse( player, "main.inventory.map-selector.title" ) );
		BoxPanel topMenu = new BoxPanel( "menu panel", 0 );
		OptionPane options = new OptionPane( "options" );
		topMenu.addPane( options );
		for ( int i = 0; i < 9; i++ ) {
			if ( i == 2 ) {
				ItemBuilder builder = new ItemBuilder( Material.MAP, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.buttons.map" ) ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "maps", "change tab", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 4 ) {
				ItemBuilder builder = new ItemBuilder( Material.GLOWSTONE_DUST, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.buttons.minimap" ), true ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "minimaps", "change tab", ActionItemIntention.CUSTOM, builder.getItem() ) );

			} else if ( i == 6 ) { 
				ItemBuilder builder = new ItemBuilder( Material.APPLE, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.buttons.overlay" ) ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "overlays", "change tab", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else {
				ItemBuilder builder = new ItemBuilder( Material.STAINED_GLASS_PANE, 1, ( byte ) 15, " " );
				options.addActionItem( new ActionItem( "Nothing", "NONE", ActionItemIntention.NONE, builder.getItem() ) );
			}
		}
		RevolvingPanel panel = new RevolvingPanel( "MapSelectionPanel", 9 );
		PagedOptionPane maps = new PagedOptionPane( "maps" );
		PagedOptionPane overlays = new PagedOptionPane( "overlays" );
		PagedOptionPane minimaps = new PagedOptionPane( "minimaps" );
		panel.addPane( maps, overlays, minimaps );
		panel.unhidePane( minimaps );
		BananaButton np = new BananaButton( "next page", 35 );
		BananaButton pp = new BananaButton( "prev page", 27 );
		ItemBuilder builder = new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.buttons.next-page" ) );
		np.setItem( new ButtonItem( "next page", "change page", ActionItemIntention.NEXT, builder.getItem() ) );
		builder.setName( CLogger.parse( player, "main.inventory.map-selector.buttons.previous-page" ) );
		pp.setItem( new ButtonItem( "prev page", "change page", ActionItemIntention.PREVIOUS, builder.getItem() ) );
		fillPane( player, maps, overlays, minimaps );
		maps.addButtons( np, pp );
		overlays.addButtons( np, pp );
		minimaps.addButtons( np, pp );
		menu.addComponent( panel, np, pp );
		if ( CPerms.isAdmin( player ) ) {
			menu.addComponent( topMenu );
		}
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
        
        Minimap map = MapManager.getInstance().getPlayerMap( player );
        
        if ( actions.contains( "change map" ) ) {
        	File file = new File( item.getName() );
        	System.out.println( "Changing maps is disabled!" );
        	CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.map-set" ), map.getName(), map.getId() );
        }
        if ( actions.contains( "change overlay" ) ) {
        	File file = new File( item.getName() );
        	map.setOverlay( file );
        	CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.overlay-set" ), map.getName(), map.getId() );
        }
        if ( actions.contains( "remove overlay map" ) ) {
        	byte[][] overlay = new byte[ 128 ][ 128 ];
        	map.setOverlay( overlay );
        	CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.notification.overlay-removed" ), map.getName(), map.getId() );
        }
        if ( actions.contains( "change page" ) ) {
        	RevolvingPanel panel = ( RevolvingPanel ) inventory.getComponent( "MapSelectionPanel" );
        	PagedOptionPane pane = ( PagedOptionPane ) panel.getMainPane();
        	if ( item.getName().equalsIgnoreCase( "next page" ) ) {
        		pane.setPage( pane.getPage() + 1 );
        	} else if ( item.getName().equalsIgnoreCase( "prev page" ) ) {
        		pane.setPage( pane.getPage() - 1 );
        	}
        	inventory.updateInventory();
        }
        if ( actions.contains( "change tab" ) ) {
        	BoxPanel menuPanel = ( BoxPanel ) inventory.getComponent( "menu panel" );
        	OptionPane pane = ( OptionPane ) menuPanel.getPanes().get( "options" );
        	for ( ActionItem aitem : pane.getContents() ) {
        		if ( aitem.getItem().containsEnchantment( Enchantment.DURABILITY ) ) aitem.getItem().removeEnchantment( Enchantment.DURABILITY );
        	}
        	item.getItem().addUnsafeEnchantment( Enchantment.DURABILITY , 1 );
        	RevolvingPanel panel = ( RevolvingPanel ) inventory.getComponent( "MapSelectionPanel" );
        	panel.unhidePane( item.getName() );
        	inventory.updateInventory();
        }
        if ( actions.contains( "edit minimap" ) ) {
        	ClickType click = event.getClick();
        	Minimap bMap = MapManager.getInstance().getMinimap( UUID.fromString( item.getName() ) );
        	if ( click == ClickType.LEFT ) {
        		MapManager.getInstance().setPlayerMap( player, bMap );
        	} else if ( click == ClickType.RIGHT ) {
        		bMap.refreshMap( true );
        	}
        	// TODO Add some more actions and things to edit
        }
        if ( actions.contains( "change minimap" ) ) {
        	MapManager.getInstance().setPlayerMap( player, MapManager.getInstance().getMinimap( UUID.fromString( item.getName() ) ) );
        }
        return false;
	}
	
	public static boolean executeInventoryCloseEvent( InventoryCloseEvent event, CustomInventory inventory ) {
		return true;
	}
	
	private static void fillPane( Player player, PagedOptionPane pane, PagedOptionPane panel2, PagedOptionPane panel3 ) {
		pane.getAllContents().clear();
		panel2.getAllContents().clear();
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
			return;
		}
		File dir = new File( Cartographer.getMain().getDataFolder() + "/maps" );
		if ( dir.exists() ) {
			for ( File file : dir.listFiles() ) {
				if ( file.isDirectory() ) {
					continue;
				}
				ItemBuilder builder = new ItemBuilder( Material.ENDER_CHEST, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.items.map-name", file.getName().replaceAll( "\\.ser$", "" ) ) );
				pane.addActionItem( new ActionItem( file.getAbsolutePath(), "change map", ActionItemIntention.CUSTOM, builder.getItem() ) );
			}
		}
		File mapDir = new File( map.getDataFolder() + "/maps" );
		if ( mapDir.exists() ) {
			for ( File file : mapDir.listFiles() ) {
				if ( file.isDirectory() ) {
					continue;
				}
				ItemBuilder builder = new ItemBuilder( Material.CHEST, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.items.map-name", file.getName().replaceAll( "\\.ser$", "" ) ) );
				pane.addActionItem( new ActionItem( file.getAbsolutePath(), "change map", ActionItemIntention.CUSTOM, builder.getItem() ) );
			}
		}
			
		File dir2 = new File( Cartographer.getMain().getDataFolder() + "/maps/overlays" );
		if ( dir2.exists() ) {
			for ( File file : dir2.listFiles() ) {
				ItemBuilder builder = new ItemBuilder( Material.ENDER_CHEST, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.items.overlay-name", file.getName().replaceAll( "\\.ser$", "" ) ) );
				panel2.addActionItem( new ActionItem( file.getAbsolutePath(), "change overlay", ActionItemIntention.CUSTOM, builder.getItem() ) );
			}
		}
		File overlayDir = new File( map.getDataFolder() + "/maps/overlays" );
		if ( overlayDir.exists() ) {
			for ( File file : overlayDir.listFiles() ) {
				ItemBuilder builder = new ItemBuilder( Material.CHEST, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.items.overlay-name", file.getName().replaceAll( "\\.ser$", "" ) ) );
				panel2.addActionItem( new ActionItem( file.getAbsolutePath(), "change overlay", ActionItemIntention.CUSTOM, builder.getItem() ) );

			}
		}
		ItemBuilder builder = new ItemBuilder( Material.BARRIER, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.items.none" ) );
		panel2.addActionItem( new ActionItem( "none", "remove overlay map", ActionItemIntention.CUSTOM, builder.getItem() ) );
		
		panel3.getAllContents().clear();
		for ( Minimap bMap : MapManager.getInstance().getMinimaps().values() ) {
			ItemBuilder mapItemBuilder = new ItemBuilder( Material.DIAMOND, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.map-selector.items.minimap-name", bMap.getName() ) );
			String[] lore = new String[ 5 ];
			lore[ 0 ] = CLogger.parse( player, "main.inventory.map-selector.items.minimap-lore-1", bMap.getId() );
			lore[ 1 ] = CLogger.parse( player, "main.inventory.map-selector.items.minimap-lore-2", bMap.getWorld().getName() );
			lore[ 3 ] = CLogger.parse( player, "main.inventory.map-selector.items.minimap-lore-3" );
			lore[ 4 ] = CLogger.parse( player, "main.inventory.map-selector.items.minimap-lore-4" );
			mapItemBuilder.setLore( lore );
			panel3.addActionItem( new ActionItem( bMap.getUID().toString(), CPerms.isAdmin( player ) ? "edit minimap" : "change minimap", ActionItemIntention.CUSTOM, mapItemBuilder.getItem() ) );

		}
	}
}
