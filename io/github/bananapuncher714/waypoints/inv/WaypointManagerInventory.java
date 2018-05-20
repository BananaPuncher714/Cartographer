package io.github.bananapuncher714.waypoints.inv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapCursor.Type;

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
import io.github.bananapuncher714.waypoints.WPerms;
import io.github.bananapuncher714.waypoints.Waypoint;
import io.github.bananapuncher714.waypoints.WaypointAddon;
import io.github.bananapuncher714.waypoints.WaypointManager;
import io.github.bananapuncher714.waypoints.WaypointViewer;
import io.github.bananapuncher714.waypoints.WaypointViewer.DisplayType;
import io.github.bananapuncher714.waypoints.inv.WaypointEditor.WType;

public class WaypointManagerInventory {

	public static CustomInventory getInventory( Player player ) {
		CustomMenu menu = new CustomMenu( "WaypointManagerInventory", 5, CLogger.parse( player, "waypoints.inventory.manager.title" ) );
		BoxPanel panel = new BoxPanel( "CategoryPanel", 0 );
		OptionPane options = new OptionPane( "options" );
		panel.addPane( options );
		boolean hasStaff = WPerms.hasStaff( player );
		for ( int i = 0; i < 9; i++ ) {
			if ( i == ( hasStaff ? 0 : 1 ) ) {
				ItemBuilder builder = new ItemBuilder( Material.EMERALD, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.buttons.your-waypoints" ), true ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "private", "change category", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == ( hasStaff ? 2 : 3 ) ) {
				ItemBuilder builder = new ItemBuilder( Material.DIAMOND, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.buttons.shared-waypoints" ), false ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "shared", "change category", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == ( hasStaff ? 4 : 5 ) ) {
				ItemBuilder builder = new ItemBuilder( Material.COMPASS, 1, ( byte ) 0, CLogger.parse( player , "waypoints.inventory.manager.buttons.discover-waypoints" ), false ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "discovered", "change category", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == ( hasStaff ? 6 : 7 ) ) {
				ItemBuilder builder = new ItemBuilder( Material.SLIME_BALL, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.buttons.public-waypoints" ), false ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "public", "change category", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( i == 8 && hasStaff ) {
				ItemBuilder builder = new ItemBuilder( Material.BARRIER, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.buttons.staff-waypoints" ), false ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "staff", "change category", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else {
				ItemBuilder builder = new ItemBuilder( Material.STAINED_GLASS_PANE, 1, ( byte ) 15, " ", false ).addFlags( ItemFlag.HIDE_ENCHANTS );
				options.addActionItem( new ActionItem( "NOTHING", "NOTHING", ActionItemIntention.NONE, builder.getItem() ) );
			}
		}
		RevolvingPanel content = new RevolvingPanel( "ContentPanel", 9 );
		PagedOptionPane contentPane1 = new PagedOptionPane( "private" );
		PagedOptionPane contentPane2 = new PagedOptionPane( "shared" );
		PagedOptionPane discoverPane = new PagedOptionPane( "discovered" );
		PagedOptionPane contentPane3 = new PagedOptionPane( "public" );
		PagedOptionPane contentPane4 = new PagedOptionPane( "staff" );
		content.addPane( contentPane1, contentPane2, contentPane3, contentPane4, discoverPane );
		content.unhidePane( contentPane1 );
		
		BananaButton np = new BananaButton( "next page", 44 );
		np.setItem( new ButtonItem( "next", "change page", ActionItemIntention.NEXT, new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.buttons.next-page" ) ).getItem() ) );
		BananaButton pp = new BananaButton( "prev page", 36 );
		pp.setItem( new ButtonItem( "prev", "change page", ActionItemIntention.PREVIOUS, new ItemBuilder( Material.ARROW, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.buttons.previous-page" ) ).getItem() ) );
		BananaButton ua = new BananaButton( "unhide all", 40 );
		ua.setItem( new ButtonItem( "none", "unhide all", ActionItemIntention.CUSTOM, new ItemBuilder( Material.EYE_OF_ENDER, 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.buttons.unhide-all" ) ).getItem() ) );

		Minimap map = MapManager.getInstance().getPlayerMap( player );
		if ( map == null ) {
				return null;
		}
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			return null;
		}
		
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		WaypointManager manager = addon.getManager();
		contentPane1.addButtons( np, pp, ua );
		contentPane2.addButtons( np, pp, ua );
		contentPane3.addButtons( np, pp, ua );
		contentPane4.addButtons( np, pp, ua );
		discoverPane.addButtons( np, pp, ua );
		fillContentPanes( manager, player, contentPane1, contentPane2, contentPane3, contentPane4, discoverPane );
		menu.addComponent( panel, content, np, pp, ua );
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
		if ( !map.getModules().containsKey( "waypoints" ) ) {
			return false;
		}
		WaypointAddon addon = ( WaypointAddon ) map.getModules().get( "waypoints" );
		WaypointManager manager = addon.getManager();
		
        RevolvingPanel rpanel = ( RevolvingPanel ) inventory.getComponent( "ContentPanel" );
        if ( actions.contains( "change page" ) ) {
        	PagedOptionPane mainPane = ( PagedOptionPane ) rpanel.getMainPane();
        	if ( item.getName().equalsIgnoreCase( "next" ) ) {
        		mainPane.setPage( mainPane.getPage() + 1 );
        	} else if ( item.getName().equalsIgnoreCase( "prev" ) ) {
        		mainPane.setPage( mainPane.getPage() - 1 );
        	}
        }
        if ( actions.contains( "change category" ) ) {
        	rpanel.unhidePane( item.getName() );
        	BoxPanel catPanel = ( BoxPanel ) inventory.getComponent( "CategoryPanel" );
        	OptionPane optionPane = ( OptionPane ) catPanel.getPanes().get( "options" );
        	for ( ActionItem aitem : optionPane.getContents() ) {
				if ( aitem.getItem().containsEnchantment( Enchantment.DURABILITY ) ) aitem.getItem().removeEnchantment( Enchantment.DURABILITY );
			}
        	item.getItem().addUnsafeEnchantment( Enchantment.DURABILITY, 1 );
        }
        if ( actions.contains( "unhide all" ) ) {
        	WaypointViewer viewer = manager.getViewer( player );
        	String name = rpanel.getMainPane().getName();
        	if ( name.equalsIgnoreCase( "private" ) ) {
        		for ( Waypoint waypoints : viewer.getWaypoints().keySet() ) {
        			viewer.getWaypoints().put( waypoints, DisplayType.HIGHLIGHTED );
        		}
        	} else if ( name.equalsIgnoreCase( "shared" ) ) {
        		for ( Waypoint key : viewer.getShared().keySet() ) {
        			if ( !key.isUnToggleable() || WPerms.canLock( player ) ) {
        				viewer.getShared().put( key, DisplayType.HIGHLIGHTED );
        			}
        		}
        	} else if ( name.equalsIgnoreCase( "public" ) ) {
        		for ( UUID key : viewer.getPublic().keySet() ) {
        			if ( !manager.isStaff( manager.getWaypoint( key ) ) && ( !manager.getWaypoint( key ).isUnToggleable() || WPerms.canLock( player ) ) ) {
        				viewer.getPublic().put( key, DisplayType.HIGHLIGHTED );
        			}
        		}
        	} else if ( name.equalsIgnoreCase( "staff" ) ) {
        		for ( Waypoint waypoint : manager.getPublicWaypoints().keySet() ) {
        			if ( manager.isStaff( waypoint ) && ( !waypoint.isUnToggleable() || WPerms.canLock( player ) ) ) {
        				viewer.getPublic().put( waypoint.getId(), DisplayType.HIGHLIGHTED );
        			}
        		}
        	} else if ( name.equalsIgnoreCase( "discovered" ) ) {
        		for ( UUID key : viewer.getPublic().keySet() ) {
        			if ( manager.isDiscoverable( manager.getWaypoint( key ) ) && ( !manager.getWaypoint( key ).isUnToggleable() || WPerms.canLock( player ) ) ) {
        				viewer.getPublic().put( key, DisplayType.HIGHLIGHTED );
        			}
        		}
        	}
        }
        if ( actions.contains( "manage private waypoint" ) ) {
        	UUID waypointUUID = UUID.fromString( item.getName() );
        	WaypointViewer viewer = manager.getViewer( player );
        	for ( Iterator< Waypoint > it = viewer.getWaypoints().keySet().iterator(); it.hasNext(); ) {
        		Waypoint waypoint = it.next();
        		if ( waypoint.getId().equals( waypointUUID ) ) {
        			ClickType click = event.getClick();
        			if ( click == ClickType.SHIFT_RIGHT ) {
        				
        			} else if ( click == ClickType.SHIFT_LEFT ) {
        				WaypointEditor.setWaypoint( player, waypoint, WType.PRIVATE );
        				CustomInventory cinv = WaypointBananaManager.getCustomInventory( player, "WaypointEditor" );
        				if ( cinv == null ) {
        					player.closeInventory();
        					CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.command.disabled-module" ) );
        					return false;
        				}
        				player.openInventory( cinv.getInventory( false ) );
        			} else if ( click == ClickType.RIGHT ) {
        				viewer.getWaypoints().put( waypoint, getNextDisplay( viewer.getWaypoints().get( waypoint ) ) );
        			} else if ( click == ClickType.LEFT ) {
	        			waypoint.setType( getNextType( waypoint.getType() ) );
        			} else if ( click == ClickType.CONTROL_DROP ) {

        			} else if ( click == ClickType.DROP ) {

        			} else if ( click == ClickType.MIDDLE ) {
        				if ( waypoint.teleportable() && WPerms.canTeleport( player ) ) {
                			player.teleport( waypoint.getLocation() );
                		}
        			}
        			break;
        		}
        	}
        }
        if ( actions.contains( "manage shared waypoint" ) ) {
        	UUID waypointUUID = UUID.fromString( item.getName() );
        	WaypointViewer viewer = manager.getViewer( player );
        	Waypoint waypoint = manager.getWaypoint( waypointUUID );
        	ClickType click = event.getClick();
        	if ( click == ClickType.SHIFT_RIGHT ) {

        	} else if ( click == ClickType.SHIFT_LEFT ) {
        		WaypointEditor.setWaypoint( player, waypoint, WType.SHARED );
				CustomInventory cinv = WaypointBananaManager.getCustomInventory( player, "WaypointEditor" );
				if ( cinv == null ) {
					player.closeInventory();
					CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.command.disabled-module" ) );
					return false;
				}
				player.openInventory( cinv.getInventory( false ) );
        	} else if ( click == ClickType.RIGHT ) {
        		if ( waypoint.teleportable() && WPerms.canTeleport( player ) ) {
        			player.teleport( waypoint.getLocation() );
        		}
        	} else if ( click == ClickType.LEFT ) {
        		if ( !waypoint.isUnToggleable() || WPerms.canLock( player ) ) {
        			viewer.getShared().put( waypoint, getNextDisplay( viewer.getShared().get( waypoint ) ) );
        		}
        	} else if ( click == ClickType.CONTROL_DROP ) {

        	} else if ( click == ClickType.DROP ) {

        	} else if ( click == ClickType.MIDDLE ) {
        	}
        }
        if ( actions.contains( "manage public waypoint" ) ) {
        	UUID waypointUUID = UUID.fromString( item.getName() );
        	WaypointViewer viewer = manager.getViewer( player );
        	Waypoint waypoint = manager.getWaypoint( waypointUUID );
        	ClickType click = event.getClick();
        	if ( click == ClickType.SHIFT_RIGHT ) {
        		
        	} else if ( click == ClickType.SHIFT_LEFT ) {
        		WaypointEditor.setWaypoint( player, waypoint, WType.PUBLIC );
				CustomInventory cinv = WaypointBananaManager.getCustomInventory( player, "WaypointEditor" );
				if ( cinv == null ) {
					player.closeInventory();
					CLogger.msg( player, "header", CLogger.parse( player, "main.name" ), CLogger.parse( player, "main.command.disabled-module" ) );
					return false;
				}
				player.openInventory( cinv.getInventory( false ) );
        	} else if ( click == ClickType.RIGHT ) {
        		if ( waypoint.teleportable() && WPerms.canTeleport( player ) ) {
        			player.teleport( waypoint.getLocation() );
        		}
        	} else if ( click == ClickType.LEFT ) {
        		if ( !waypoint.isUnToggleable() || WPerms.canLock( player ) ) {
        			viewer.getPublic().put( waypoint.getId(), getNextDisplay( viewer.getPublic().get( waypoint.getId() ) ) );
        		}
        	} else if ( click == ClickType.CONTROL_DROP ) {

        	} else if ( click == ClickType.DROP ) {

        	} else if ( click == ClickType.MIDDLE ) {
        		
        	}
        }

        PagedOptionPane p1 = ( PagedOptionPane ) rpanel.getPanes().get( "private" );
        PagedOptionPane p2 = ( PagedOptionPane ) rpanel.getPanes().get( "shared" );
        PagedOptionPane p3 = ( PagedOptionPane ) rpanel.getPanes().get( "public" );
        PagedOptionPane p4 = ( PagedOptionPane ) rpanel.getPanes().get( "staff" );
        PagedOptionPane discover = ( PagedOptionPane ) rpanel.getPanes().get( "discovered" );
        fillContentPanes( manager, player, p1, p2, p3, p4, discover );
    	inventory.updateInventory();
		return false;
	}
	
	public static boolean executeInventoryCloseEvent( InventoryCloseEvent event, CustomInventory inventory ) {
		return true;
	}
	
	private static void fillContentPanes( WaypointManager manager, Player player, PagedOptionPane p1, PagedOptionPane p2, PagedOptionPane p3, PagedOptionPane p4, PagedOptionPane discoverable ) {
		WaypointViewer viewer = manager.getViewer( player );
		p1.getAllContents().clear();
		for ( Waypoint waypoint : viewer.getWaypoints().keySet() ) {
			String hidden = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-1", viewer.getWaypoints().get( waypoint ).getDisplayName() ); 
			String symbol = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-2", waypoint.getType().name().replaceAll( "_", " " ) );
			String everyone = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-3", manager.isPublic( waypoint ) );
			String staff = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-4", manager.isStaff( waypoint ) );
			String discover = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-5", manager.isDiscoverable( waypoint ) );
			String ins1 = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-6" );
			String ins2 = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-7" );
			String ins5 = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-8" );
			String ins6 = CLogger.parse( player, "waypoints.inventory.manager.items.private.line-9" );
			ItemBuilder builder = new ItemBuilder( getTypeMaterial( waypoint.getType() ), 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.items.private.name", waypoint.getName() ), false, hidden, symbol, everyone, staff, discover, "", ins1, ins2 );
			ItemStack item = builder.getItem();
			ItemMeta meta = item.getItemMeta();
			List< String > lore = meta.getLore();
			lore.add( ins6 );
			if ( WPerms.canTeleport( player ) ) lore.add( ins5 );
			meta.setLore( lore );
			item.setItemMeta( meta );
			p1.addActionItem( new ActionItem( waypoint.getId().toString(), "manage private waypoint", ActionItemIntention.CUSTOM, item ) );
		}
		
		p2.getAllContents().clear();
		for ( Waypoint waypoint : viewer.getShared().keySet() ) {
			if ( manager.isPublic( waypoint ) ) continue;
			List< String > lore = new ArrayList< String >();
			String creator = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-1", waypoint.getCreatorName() );
			String symbol = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-2", waypoint.getType().name().replaceAll( "_", " " ) );
			String x = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-3", waypoint.getLocation().getBlockX() );
			String z = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-4", waypoint.getLocation().getBlockZ() );
			String hidden = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-5", viewer.getShared().get( waypoint ).getDisplayName() );
			String ins2 = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-6" );
			String teleport = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-7" );
			String edit = CLogger.parse( player, "waypoints.inventory.manager.items.shared.line-8" );
			ItemBuilder builder = new ItemBuilder( getTypeMaterial( waypoint.getType() ), 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.items.shared.name", waypoint.getName() ) );
			
			lore.add( creator );
			lore.add( symbol );
			lore.add( x );
			lore.add( z );
			lore.add( hidden );
			lore.add( " " );
			if ( WPerms.canLock( player ) || !waypoint.isUnToggleable() ) {
				lore.add( ins2 );
			}
			if ( WPerms.canTeleport( player ) ) {
				lore.add( teleport );
			}
			lore.add( edit );
			builder.setLore( lore.toArray( new String[ lore.size() ] ) );
			
			p2.addActionItem( new ActionItem( waypoint.getId().toString(), "manage shared waypoint", ActionItemIntention.CUSTOM, builder.getItem() ) );
		}
		
		p3.getAllContents().clear();
		p4.getAllContents().clear();
		discoverable.getAllContents().clear();
		Set< Waypoint > allSet = new HashSet< Waypoint >();
		for ( UUID uuid : manager.getDiscoverable() ) {
			Waypoint addition = manager.getWaypoint( uuid );
			if ( addition != null ) allSet.add( addition );
		}
		allSet.addAll( manager.getPublicWaypoints().keySet() );
		for ( Waypoint waypoint : allSet ) {
			if ( viewer.getWaypoints().containsKey( waypoint ) ) continue;
			if ( !viewer.getPublic().containsKey( waypoint.getId() ) ) continue;
			
			List< String > lore = new ArrayList< String >();
			String creator = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-1", waypoint.getCreatorName() );
			String symbol = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-2", waypoint.getType().name().replaceAll( "_", " " ) );
			String x = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-3", waypoint.getLocation().getBlockX() );
			String z = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-4", waypoint.getLocation().getBlockZ() );
			String vis = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-5", viewer.getPublic().get( waypoint.getId() ).getDisplayName() );
			String ins2 = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-6" );
			String teleport = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-7" );
			String edit = CLogger.parse( player, "waypoints.inventory.manager.items.public.line-8" );
			ItemBuilder builder = new ItemBuilder( getTypeMaterial( waypoint.getType() ), 1, ( byte ) 0, CLogger.parse( player, "waypoints.inventory.manager.items.public.name", waypoint.getName() ), false );
			
			lore.add( creator );
			lore.add( symbol );
			lore.add( x );
			lore.add( z );
			lore.add( vis );
			lore.add( " " );
			
			if ( WPerms.canLock( player ) || !waypoint.isUnToggleable() ) {
				lore.add( ins2 );
			}
			if ( WPerms.canTeleport( player ) ) {
				lore.add( teleport );
			}
			lore.add( edit );
			builder.setLore( lore.toArray( new String[ lore.size() ] ) );
			
			if ( manager.isDiscoverable( waypoint ) ) {
				discoverable.addActionItem( new ActionItem( waypoint.getId().toString(), "manage public waypoint", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else if ( manager.isStaff( waypoint ) ) {
				p4.addActionItem( new ActionItem( waypoint.getId().toString(), "manage public waypoint", ActionItemIntention.CUSTOM, builder.getItem() ) );
			} else {
				p3.addActionItem( new ActionItem( waypoint.getId().toString(), "manage public waypoint", ActionItemIntention.CUSTOM, builder.getItem() ) );
			}
		}
	}
	
	public static Type getNextType( Type type ) {
		for ( int i = 0; i < Type.values().length; i++ ) {
			if ( Type.values()[ i ] == type ) return Type.values()[ ( i + 1 ) % Type.values().length ];
		}
		return Type.WHITE_CROSS;
	}
	
	public static Material getTypeMaterial( Type type ) {
		if ( type.name().equalsIgnoreCase( "WHITE_POINTER" ) ) return Material.FEATHER;
		if ( type.name().equalsIgnoreCase( "WHITE_CIRCLE" ) ) return Material.SUGAR;
		if ( type.name().equalsIgnoreCase( "WHITE_CROSS" ) ) return Material.BONE;
		if ( type.name().equalsIgnoreCase( "RED_POINTER" ) ) return Material.RED_ROSE;
		if ( type.name().equalsIgnoreCase( "GREEN_POINTER" ) ) return Material.GREEN_RECORD;
		if ( type.name().equalsIgnoreCase( "BLUE_POINTER" ) ) return Material.WATER_BUCKET;
		if ( type.name().equalsIgnoreCase( "RED_MARKER" ) ) return Material.REDSTONE;
		if ( type.name().equalsIgnoreCase( "MANSION" ) ) return Material.WOOD_DOOR;
		if ( type.name().equalsIgnoreCase( "TEMPLE" ) ) return Material.PRISMARINE;
		if ( type.name().equalsIgnoreCase( "SMALL_WHITE_CIRCLE" ) ) return Material.GHAST_TEAR;
		else return Material.APPLE;
	}
	
	public static DisplayType getNextDisplay( DisplayType type ) {
		for ( int i = 0; i < DisplayType.values().length; i++ ) {
			if ( DisplayType.values()[ i ] == type ) return DisplayType.values()[ ( i + 1 ) % DisplayType.values().length ];
		}
		return DisplayType.HIDDEN;
	}
}
