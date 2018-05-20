package io.github.bananapuncher714.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.components.ButtonComponent;
import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.components.InventoryPanel;
import io.github.bananapuncher714.inventory.panes.ActionItemPane;
import io.github.bananapuncher714.inventory.panes.ContentPane;
import io.github.bananapuncher714.inventory.panes.OptionPane;
import io.github.bananapuncher714.inventory.panes.StoragePane;
import io.github.bananapuncher714.inventory.util.CustomHolder;
import io.github.bananapuncher714.inventory.util.ICEResponse;
import io.github.bananapuncher714.inventory.util.InventoryManager;

public class CustomMenu implements CustomInventory {
	protected String name;
	protected String id;
	protected int size, rows;
	protected final String type = "CustomMenu";
	protected Inventory inv = null;
	protected CustomHolder inventoryHolder;
	
	protected HashMap< String, InventoryComponent > components = new HashMap< String, InventoryComponent >();
	
	public CustomMenu( String identifier, int rows ) {
		this( identifier, rows, "Custom Menu" );
	}
	
	public CustomMenu( String identifier, int rows, String n ) {
		id = identifier;
		this.rows = rows;
		size = rows * 9;
		name = n;
	}

	@Override
	public Inventory getInventory( boolean update ) {
		if ( inv != null && !update ) return inv;
		inventoryHolder = new CustomHolder( id );
		inv = Bukkit.createInventory( inventoryHolder, size, name );
		updateInventory( inv );
		return inv;
	}
	
	@Override
	public void updateInventory() {
		if ( inv != null ) updateInventory( inv );
	}
	
	@Override
	public void updateInventory( Inventory inv ) {
		if ( inv.getSize() != size ) return;
		ItemStack[] finalcontents = new ItemStack[ size ];
		ArrayList< ButtonComponent > buttons = new ArrayList< ButtonComponent >();
		for ( InventoryComponent c : components.values() ) {
			if ( c instanceof ButtonComponent ) {
				ButtonComponent button = ( ButtonComponent ) c;
				boolean visible = false;
				for ( ContentPane pane : button.getPanes() ) {
					if ( !pane.isHidden() ) {
						visible = true;
					}
				}
				if ( !visible && button.getPanes().size() != 0 ) button.hide( true );
			}
		}
		resize();
		for ( InventoryComponent c : components.values() ) {
			int topright = c.getSlot();
			int[] componentSlotCoords = InventoryManager.slotToCoord( topright, 9 );
			if ( c instanceof ButtonComponent ) {
				buttons.add( ( ButtonComponent ) c );
				continue;
			} else {
				InventoryPanel bp = ( InventoryPanel ) c;
				bp.loadPanes();
				for ( ContentPane contentpane : bp.getPanes().values() ) {
					if ( contentpane.isHidden() ) continue;
					if ( contentpane instanceof ActionItemPane ) {
						ActionItemPane options = ( ActionItemPane ) contentpane;
						ArrayList< ActionItem > contents = options.getContents();
						int[] panecoords = InventoryManager.slotToCoord( options.getSlot(), bp.getWidth() );
						for ( int i = 0; i < contents.size(); i++ ) {
							int[] actionitemcoords = InventoryManager.slotToCoord( i, options.getWidth() );
							int slot = InventoryManager.coordToSlot( componentSlotCoords[ 0 ] + panecoords[ 0 ] + actionitemcoords[ 0 ], componentSlotCoords[ 1 ] + panecoords[ 1 ] + actionitemcoords[ 1 ], 9, rows );
							finalcontents[ slot ] = contents.get( i ).getItem();
						}
					} else if ( contentpane instanceof StoragePane ) {
						StoragePane storage = ( StoragePane ) contentpane;
						ArrayList< ItemStack > contents = storage.getContents();
						int[] storageCoords = InventoryManager.slotToCoord( storage.getSlot(), bp.getWidth() );
						for ( int i = 0; i < contents.size(); i++ ) {
							int[] itemCoords = InventoryManager.slotToCoord( i, storage.getWidth() );
							int slot = InventoryManager.coordToSlot( componentSlotCoords[ 0 ] + storageCoords[ 0 ] + itemCoords[ 0 ], componentSlotCoords[ 1 ] + storageCoords[ 1 ] + itemCoords[ 1 ], 9, rows );
							finalcontents[ slot ] = contents.get( i );
						}
					}
				}
			}
		}
		for ( InventoryComponent c : components.values() ) {
			int topright = c.getSlot();
			if ( c instanceof ButtonComponent ) {
				ButtonComponent button = ( ButtonComponent ) c;
				if ( button.isHidden() ) continue; 
				finalcontents[ topright ] = button.getItem().getItem();
			}
		}
		inv.setContents( finalcontents );
	}
	
	@Override
	public void saveInventory( Inventory inv ) {
		if ( inv.getSize() != size ) return;
		for ( InventoryComponent c : components.values() ) {
			if ( c instanceof InventoryPanel ) {
				InventoryPanel panel = ( InventoryPanel ) c;
				int[] panelcoords = InventoryManager.slotToCoord( panel.getSlot(), 9 );
				for ( ContentPane pane : panel.getPanes().values() ) {
					if ( pane instanceof StoragePane ) {
						StoragePane storagepane = ( StoragePane ) pane;
						if ( storagepane.isHidden() ) continue;
						ItemStack[] contents = inv.getContents();
						int[] scoord = InventoryManager.slotToCoord( storagepane.getSlot(), panel.getWidth() );
						for ( int i = 0; i < ( storagepane.getHeight() * storagepane.getWidth() ); i++ ) {
							int[] icoord = InventoryManager.slotToCoord( i, storagepane.getWidth() );
							int slot = InventoryManager.coordToSlot( panelcoords[ 0 ] + scoord[ 0 ] + icoord[ 0 ], panelcoords[ 1 ] + scoord[ 1 ] + icoord[ 1 ], 9, rows );
							storagepane.setItem( contents[ slot ], i );
						}
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getSize() {
		return 0;
	}
	
	public int getRows() {
		return size / 9;
	}

	@Override
	public ArrayList< InventoryComponent > getComponents() {
		return new ArrayList< InventoryComponent >( components.values() );
	}
	
	@Override
	public InventoryComponent getComponent( String name ) {
		return components.get( name );
	}

	@Override
	public void addComponent( InventoryComponent... componentss ) {
		for ( InventoryComponent component : componentss ) {
			component.setInventory( this );
			components.put( component.getName(), component );
		}
	}
	
	@Override
	public void addComponent( InventoryComponent component, int slot ) {
		component.setSlot( slot );
		component.setInventory( this );
		components.put( component.getName(), component );
	}

	@Override
	public void removeComponent( InventoryComponent... componentss ) {
		for ( InventoryComponent component : componentss )
			components.remove( component.getName() );
	}
	
	@Override
	public void removeComponent( String... n ) {
		for ( String name : n )
			components.remove( name );
	}
	
	@Override
	public void resize() {
		InventoryManager.orderComponents( rows, new ArrayList< InventoryComponent >( components.values() ) );
	}

	@Override
	public ICEResponse parseICE( InventoryClickEvent e ) {
		ICEResponse response = new ICEResponse( "New Response" );
		InventoryComponent clickedComponent = findComponent( e.getRawSlot() );
		if ( clickedComponent == null ) return response;
		response.setComponent( clickedComponent );
		if ( clickedComponent instanceof InventoryPanel ) {
			InventoryPanel bigComponent = ( InventoryPanel ) clickedComponent;
			int componentSlot = ( int ) ( e.getRawSlot() - clickedComponent.getSlot() - Math.floor( ( e.getRawSlot() - clickedComponent.getSlot() ) / 9.0 ) * ( 9 - clickedComponent.getWidth() ) );
			ContentPane clickedPane = bigComponent.findPane( componentSlot );
			int slot = ( int ) ( componentSlot - clickedPane.getSlot() - Math.floor( ( componentSlot - clickedPane.getSlot() ) / 9.0 ) * ( clickedComponent.getWidth() - clickedPane.getWidth() ) );
			if ( clickedPane instanceof OptionPane ) {
				ActionItemPane clickedOption = ( ActionItemPane ) clickedPane;
				ActionItem aitem = null;
				if ( clickedOption.getContents().size() > slot ) aitem = clickedOption.getContents().get( slot );
				response.setActionItem( aitem );
			} else if ( clickedPane instanceof StoragePane ) {
				StoragePane clickedStoragePane = ( StoragePane ) clickedPane;
				ItemStack item = clickedStoragePane.getContents().get( slot );
				response.setItem( item );
			}
			response.setPane( clickedPane );
		} else if ( clickedComponent instanceof ButtonComponent ) {
			ButtonComponent button = ( ButtonComponent ) clickedComponent;
			response.setActionItem( button.getItem() );
		}
		return response;
	}

	@Override
	public InventoryComponent findComponent( int number ) {
		for ( InventoryComponent component : components.values() ) {
			if ( InventoryManager.overlap( component.getSlot(), component.getWidth(), component.getHeight(), number, 9 ) ) {
				if ( component instanceof ButtonComponent && ( ( ButtonComponent ) component ).isHidden() ) continue;
				return component;
			}
		}
		return null;
	}
}
