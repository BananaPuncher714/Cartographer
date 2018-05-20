package io.github.bananapuncher714.citizens.inv;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.citizens.CitizenCursor;
import io.github.bananapuncher714.citizens.CitizensAddon;
import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.CustomMenu;
import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.actionItem.ActionItemIntention;
import io.github.bananapuncher714.inventory.actionItem.ButtonItem;
import io.github.bananapuncher714.inventory.components.BananaButton;
import io.github.bananapuncher714.inventory.components.BoxPanel;
import io.github.bananapuncher714.inventory.items.ItemBuilder;
import io.github.bananapuncher714.inventory.items.SkullBuilder;
import io.github.bananapuncher714.inventory.panes.PagedOptionPane;
import io.github.bananapuncher714.inventory.util.ICEResponse;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class CitizensManager {
	
	public static CustomInventory getInventory( Player player ) {
		CustomMenu menu = new CustomMenu( "CitizensManager", 3, CLogger.parse( player, "citizens.inventory.manager.title" ) );
		BoxPanel panel = new BoxPanel( "NPC Panel" );
		PagedOptionPane pane = new PagedOptionPane( "head pane" );
		panel.addPane( pane );
		
		BananaButton np = new BananaButton( "next page", 26 );
		BananaButton pp = new BananaButton( "prev page", 18 );
		ItemBuilder builder = new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "citizens.inventory.manager.buttons.next-page" ) );
		np.setItem( new ButtonItem( "next", "change page", ActionItemIntention.NEXT, builder.getItem() ) );
		builder.setName( CLogger.parse( player, "citizens.manager.inventory.buttons.previous-page" ) );
		pp.setItem( new ButtonItem( "prev", "change page", ActionItemIntention.PREVIOUS, builder.getItem() ) );
		
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		fillPane( map, player, pane );
		pane.addButtons( pp, np );
		menu.addComponent( panel, pp, np );
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
        CitizensAddon addon = ( CitizensAddon ) map.getModules().get( "citizens" );
        BoxPanel panel = ( BoxPanel ) inventory.getComponent( "NPC Panel" );
        PagedOptionPane pane = ( PagedOptionPane ) panel.getPanes().get( "head pane" );
        if ( actions.contains( "edit npc" ) ) {
        	CitizenCursor cursor = addon.getManager().getCursor( UUID.fromString( item.getName() ) );
        	ClickType click = event.getClick();
        	if ( click == ClickType.LEFT ) {
        		cursor.setType( getNextType( cursor.getType() ) );
        	} else if ( click == ClickType.RIGHT ) {
        		cursor.setHidden( !cursor.isHidden() );
        	} else if ( click == ClickType.DROP ) {
        		cursor.setVisible( !cursor.isVisible() );
        	} else if ( click == ClickType.SHIFT_LEFT ) {
        		cursor.setRange( Math.max( 0, cursor.getRange() - 1 ) );
        	} else if ( click == ClickType.SHIFT_RIGHT ) {
        		cursor.setRange( cursor.getRange() + 1 );
        	}
        }
        if ( actions.contains( "change page" ) ) {
        	if ( item.getName().equalsIgnoreCase( "next" ) ) {
        		pane.setPage( pane.getPage() + 1 );
        	} else if ( item.getName().equalsIgnoreCase( "prev" ) ) {
        		pane.setPage( pane.getPage() - 1 );;
        	}
        }
        fillPane( map, player, pane );
        inventory.updateInventory();
        return false;
	}
	
	public static boolean executeInventoryCloseEvent( InventoryCloseEvent event, CustomInventory inventory ) {
		return true;
	}
	
	private static void fillPane( Minimap map, Player player, PagedOptionPane pane ) {
		pane.getAllContents().clear();
		if ( map == null ) return;
		CitizensAddon addon = ( CitizensAddon ) map.getModules().get( "citizens" );
		for ( NPC npc : CitizensAPI.getNPCRegistry().sorted() ) {
			CitizenCursor cursor = addon.getManager().getCursor( npc.getUniqueId() );
			String name = CLogger.parse( player, "citizens.inventory.manager.items.npc.name", npc.getFullName() );
			String[] lore = new String[ 10 ];
			lore[ 0 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-1", cursor.getType().name().replaceAll( "_", " " ) );
			lore[ 1 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-2", cursor.getRange() );
			lore[ 2 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-3", !cursor.isHidden() );
			lore[ 3 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-4", cursor.isVisible() );
			lore[ 4 ] = "";
			lore[ 5 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-5" );
			lore[ 6 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-6" );
			lore[ 7 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-7" );
			lore[ 8 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-8" );
			lore[ 9 ] = CLogger.parse( player, "citizens.inventory.manager.items.npc.line-9" );
			ItemBuilder builder = new SkullBuilder( name, npc.getName(), false, lore );
			pane.addActionItem( new ActionItem( npc.getUniqueId().toString(), "edit npc", ActionItemIntention.CUSTOM, builder.getItem() ) );
		}
	}
	
	public static Type getNextType( Type type ) {
		for ( int i = 0; i < Type.values().length; i++ ) {
			if ( Type.values()[ i ] == type ) return Type.values()[ ( i + 1 ) % Type.values().length ];
		}
		return Type.BLUE_POINTER;
	}
}
