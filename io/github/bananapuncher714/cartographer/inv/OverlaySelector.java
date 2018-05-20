package io.github.bananapuncher714.cartographer.inv;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.cartographer.util.FileUtil;
import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.CustomMenu;
import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.actionItem.ActionItemIntention;
import io.github.bananapuncher714.inventory.actionItem.ButtonItem;
import io.github.bananapuncher714.inventory.components.BananaButton;
import io.github.bananapuncher714.inventory.components.BoxPanel;
import io.github.bananapuncher714.inventory.components.ButtonComponent;
import io.github.bananapuncher714.inventory.items.ItemBuilder;
import io.github.bananapuncher714.inventory.panes.PagedOptionPane;
import io.github.bananapuncher714.inventory.util.ICEResponse;

public class OverlaySelector {
	public static CustomInventory getInventory( Player player ) {
		CustomMenu menu = new CustomMenu( "OverlaySelector", 4, CLogger.parse( player, "main.inventory.overlay-selector.title" ) );
		BoxPanel selectionPanel = new BoxPanel( "SelectionPanel", 0 );
		PagedOptionPane selectionPane = new PagedOptionPane( "selectionPane" );
		ButtonComponent np = new BananaButton( "nextpage", 26 );
		ButtonComponent pp = new BananaButton( "prevpage", 18 );
		ItemBuilder builder = new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.overlay-selector.buttons.next-page" ) );
		np.setItem( new ButtonItem( "next page", "change page", ActionItemIntention.NEXT, builder.getItem() ) );
		builder.setName( CLogger.parse( player, "main.inventory.overlay-selector.buttons.previous-page" ) );
		pp.setItem( new ButtonItem( "prev page", "change page", ActionItemIntention.PREVIOUS, builder.getItem() ) );
		fillPane( selectionPane, player, MapManager.getInstance().getPlayerMap( player ) );
		selectionPane.addButtons( np, pp );
		selectionPanel.addPane( selectionPane );
		menu.addComponent( selectionPanel, np, pp );
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
        if ( map == null ) {
        	return false;
        }
        
        if ( actions.contains( "change default overlay" ) ) {
        	MapManager.getInstance().setOverlayForPlayer( player, null );
        }
        if ( actions.contains( "change overlay" ) ) {
        	File global = new File( item.getName() );
        	MapManager.getInstance().setOverlayForPlayer( player, FileUtil.loadFile( global ) );
        }
        
        return false;
	}
	
	public static boolean executeInventoryCloseEvent( InventoryCloseEvent event, CustomInventory inventory ) {
		return true;
	}
	
	public static void fillPane( PagedOptionPane panel, Player player, Minimap map ) {
		panel.getAllContents().clear();
		if ( map == null )  {
			return;
		}
		ItemBuilder defBuilder = new ItemBuilder( Material.GLASS, 1, ( byte ) 0, CLogger.parse( player,  "main.inventory.overlay-selector.items.default-name" ) );
		panel.addActionItem( new ActionItem( "default", "change default overlay", ActionItemIntention.CUSTOM, defBuilder.getItem() ) );
		
		File dir2 = new File( Cartographer.getMain().getDataFolder() + "/maps/overlays" );
		if ( dir2.exists() ) {
			for ( File file : dir2.listFiles() ) {
				ItemBuilder builder = new ItemBuilder( Material.ENDER_CHEST, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.overlay-selector.items.global-name", file.getName().replaceAll( "\\.ser$", "" ) ) );
				panel.addActionItem( new ActionItem( file.getAbsolutePath(), "change overlay", ActionItemIntention.CUSTOM, builder.getItem() ) );
			}
		}
		File overlayDir = new File( map.getDataFolder() + "/maps/overlays" );
		if ( overlayDir.exists() ) {
			for ( File file : overlayDir.listFiles() ) {
				ItemBuilder builder = new ItemBuilder( Material.CHEST, 1, ( byte ) 0, CLogger.parse( player, "main.inventory.overlay-selector.items.local-name", file.getName().replaceAll( "\\.ser$", "" ) ) );
				panel.addActionItem( new ActionItem( file.getAbsolutePath(), "change overlay", ActionItemIntention.CUSTOM, builder.getItem() ) );

			}
		}
	}
}
