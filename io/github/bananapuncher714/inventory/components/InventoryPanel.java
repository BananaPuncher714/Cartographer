package io.github.bananapuncher714.inventory.components;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.panes.ContentPane;
import io.github.bananapuncher714.inventory.panes.StoragePane;
import io.github.bananapuncher714.inventory.util.InventoryManager;
import io.github.bananapuncher714.inventory.util.PagedObject;

public abstract class InventoryPanel implements InventoryComponent {
	protected String name;
	protected boolean xpandx = true, xpandy= true;
	protected int slot;
	protected HashMap< String, ContentPane > panes = new HashMap< String, ContentPane >();
	protected int width = 1, height = 1;
	protected CustomInventory inventory;
	
	@Override
	public abstract String getType();
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public CustomInventory getInventory() {
		return inventory;
	}
	
	@Override
	public void setInventory( CustomInventory inv ) {
		inventory = inv;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public void setSlot( int s ) {
		slot = s;
	}

	@Override
	public int getSlot() {
		return slot;
	}
	
	public HashMap< String, ContentPane > getPanes() {
		return panes;
	}

	public void addPane( ContentPane... p ) {
		for ( ContentPane pane : p ) {
			pane.setComponent( this );
			panes.put( pane.getName(), pane );
		}
	}

	public void removePane( ContentPane... p ) {
		for ( ContentPane pane : p )
			panes.remove( pane.getName() );
	}

	public void setPanes( ArrayList< ContentPane > p ) {
		panes = new HashMap< String, ContentPane >();
		for ( ContentPane pane : p ) {
			panes.put( pane.getName(), pane );
		}
	}
	
	public void loadPanes() {
		for ( ContentPane pane : panes.values() ) {
			pane.getPlacement();
			if ( pane.isHidden() ) continue;
			pane.setSize( 1, 1 );
			switch ( pane.getPlacement().getId() ) {
			case 1:
				pane.setSlot( 0 );
				break;
			case 2:
				pane.setSlot( width / 3 );
				break;
			case 3:
				pane.setSlot( ( 2 * width ) / 3 );
				break;
			case 4:
				pane.setSlot( InventoryManager.coordToSlot( 0, height / 3, width, height) );
				break;
			case 5:
				pane.setSlot( InventoryManager.coordToSlot( width / 3, height / 3, width, height) );
				break;
			case 6:
				pane.setSlot( InventoryManager.coordToSlot( ( 2 * width ) / 3, height / 3, width, height) );
				break;
			case 7:
				pane.setSlot( InventoryManager.coordToSlot( 0, ( 2 * height ) / 3, width, height) );
				break;
			case 8:
				pane.setSlot( InventoryManager.coordToSlot( width / 3, ( 2 * height ) / 3, width, height) );
				break;
			case 9:
				pane.setSlot( InventoryManager.coordToSlot( ( 2 * width ) / 3, ( 2 * height ) / 3, width, height) );
				break;
			case 10:
				pane.setSlot( 0 );
				break;
			case 11:
				pane.setSlot( InventoryManager.coordToSlot( width / 2, 0, width, height) );
				break;
			case 12:
				pane.setSlot( InventoryManager.coordToSlot( 0, height / 2, width, height) );
				break;
			case 13:
				pane.setSlot( InventoryManager.coordToSlot( width / 2, height / 2, width, height) );
				break;
			}
		}
		InventoryManager.organizePanes( this, new ArrayList< ContentPane >( panes.values() ) );
		for ( ContentPane pane : panes.values() ) {
			if ( pane instanceof StoragePane ) ( ( StoragePane ) pane ).getContents();
			else pane.sort();
			if ( pane instanceof PagedObject && !pane.isHidden() ) ( ( PagedObject ) pane ).loadButtons();
		}
	}

	public ContentPane findPane( int slot ) {
		for ( ContentPane pane : panes.values() ) {
			if ( pane.isHidden() ) continue;
			if ( InventoryManager.overlap( pane.getSlot(), pane.getWidth(), pane.getHeight(), slot, width ) ) {
				return pane;
			}
		}
		return null;
	}

	public void setWidth( int w ) {
		width = w;
	}

	public void setHeight( int h ) {
		height = h;
	}
	
	public void setXpandX( boolean xpand ) {
		xpandx = xpand;
	}
	
	public void setXpandY( boolean xpand ) {
		xpandy = xpand;
	}
	
	public boolean expandX() {
		return xpandx;
	}
	
	public boolean expandY() {
		return xpandy;
	}
}
