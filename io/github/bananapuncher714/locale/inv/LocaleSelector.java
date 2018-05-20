package io.github.bananapuncher714.locale.inv;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import io.github.bananapuncher714.cartographer.Cartographer;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.CustomMenu;
import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.actionItem.ActionItemIntention;
import io.github.bananapuncher714.inventory.actionItem.ButtonItem;
import io.github.bananapuncher714.inventory.components.BananaButton;
import io.github.bananapuncher714.inventory.components.BoxPanel;
import io.github.bananapuncher714.inventory.items.ItemBuilder;
import io.github.bananapuncher714.inventory.panes.PagedOptionPane;
import io.github.bananapuncher714.inventory.util.ICEResponse;
import io.github.bananapuncher714.locale.Locale;
import io.github.bananapuncher714.locale.LocaleAddon;

public class LocaleSelector {
	private static LocaleAddon addon = Cartographer.getMain().getLocaleAddon();
	
	public static CustomInventory getInventory( Player player ) {
		CustomMenu menu = new CustomMenu( "LocaleSelector", 3, CLogger.parse( player, "locale.inventory.locale-selector.title" ) );
		BoxPanel panel = new BoxPanel( "LocaleSelectionPanel",0 );
		PagedOptionPane pane = new PagedOptionPane( "localePane" );
		panel.addPane( pane );
		BananaButton np = new BananaButton( "next page", 26 );
		ItemBuilder builder = new ItemBuilder( Material.ARROW, 1, ( byte ) 0, "RENAME ME" );
		builder.setName( CLogger.parse( player, "locale.inventory.locale-selector.buttons.next-page" ) );
		np.setItem( new ButtonItem( "next page", "change page", ActionItemIntention.NEXT, builder.getItem() ) );
		BananaButton pp = new BananaButton( "previous page", 18 );
		builder.setName( CLogger.parse( player, "locale.inventory.locale-selector.buttons.previous-page" ) );
		pp.setItem( new ButtonItem( "prev page", "change page", ActionItemIntention.PREVIOUS, builder.getItem() ) );
		pane.addButtons( np, pp );
		fillPane( player, pane );
		menu.addComponent( panel, np, pp );
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
        
        BoxPanel panel = ( BoxPanel ) inventory.getComponent( "LocaleSelectionPanel" );
        PagedOptionPane pane = ( PagedOptionPane ) panel.getPanes().get( "localePane" );
        if ( actions.contains( "change page" ) ) {
        	if ( item.getName().equalsIgnoreCase( "next page" ) ) {
        		pane.setPage( pane.getPage() + 1 );
        	} else if ( item.getName().equalsIgnoreCase( "prev page" ) ) {
        		pane.setPage( pane.getPage() - 1 );
        	}
        }
        if ( actions.contains( "change locale" ) ) {
        	addon.setPreference( player, item.getName() );
        	CLogger.msg( player, "header", CLogger.parse( player, "locale.name" ), CLogger.parse( player, "locale.notification.changed-locale", item.getName() ) );
        	fillPane( player, pane );
        }
        inventory.updateInventory();
        return false;
	}
	
	public static boolean executeInventoryCloseEvent( InventoryCloseEvent event, CustomInventory inventory ) {
		return true;
	}
	
	private static void fillPane( Player player, PagedOptionPane pane ) {
		pane.getAllContents().clear();
		Locale playerPref = addon.getLocale( player );
		for ( String id : addon.getLocales().keySet() ) {
			Locale locale = addon.getLocales().get( id );
			ItemBuilder builder = new ItemBuilder( Material.INK_SACK, 1, ( byte ) 8, CLogger.parse( player, "locale.inventory.locale-selector.items.name", locale.getName() ) );
			if ( locale.equals( playerPref ) ) {
				builder.setDurability( ( short ) 10 ) ;
			}
			pane.addActionItem( new ActionItem( locale.getId(), "change locale", ActionItemIntention.CUSTOM, builder.getItem() ) );
		}
	}
}
