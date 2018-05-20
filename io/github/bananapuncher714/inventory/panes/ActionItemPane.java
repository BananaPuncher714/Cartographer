package io.github.bananapuncher714.inventory.panes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.panes.sorters.Sorter;
import io.github.bananapuncher714.inventory.util.ElementPlacement;

public abstract class ActionItemPane implements ContentPane {
	protected ElementPlacement placement;
	protected String name;
	protected ItemStack placeholder;
	protected InventoryComponent component;
	protected boolean hidden = false;
	protected int x = 1, y = 1, slot;
	
	protected List< Sorter > sorters = new ArrayList< Sorter >();
	protected ArrayList< ActionItem > actionItems = new ArrayList< ActionItem >();

	@Override
	public ElementPlacement getPlacement() {
		return placement;
	}

	@Override
	public void setPlacement( ElementPlacement p ) {
		placement = p;
	}

	@Override
	public Boolean isHidden() {
		return hidden;
	}

	@Override
	public void hide( boolean hide ) {
		hidden = hide;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public abstract String getType();
	
	@Override
	public void sort() {
		for ( Sorter s : sorters ) {
			actionItems = s.sort( actionItems );
		}
	}

	public ArrayList< ActionItem > getContents() {
		return actionItems;
	}

	public ActionItem getActionItem( String name ) {
		for ( ActionItem item : actionItems ) {
			if ( item.getName().equalsIgnoreCase( name ) ) return item;
		}
		return null;
	}
	
	public void setActionItems( ArrayList< ActionItem > c ) {
		actionItems = c;
	}
	
	public void setActionItem( ActionItem item, int index ) {
		actionItems.set( index, item );
		item.setPane( this );
	}

	public void addActionItem( ActionItem item ) {
		actionItems.add( item );
		item.setPane( this );
	}

	public void removeActionItem( ActionItem item ) {
		actionItems.remove( item );
		item.setPane( null );
	}

	public ItemStack getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder( ItemStack item ) {
		placeholder = item;
	}

	@Override
	public void setSize( int a, int b ) {
		x = a;
		y = b;
	}

	@Override
	public int getWidth() {
		return x;
	}

	@Override
	public int getHeight() {
		return y;
	}
	
	@Override
	public int getSlot() {
		return slot;
	}
	
	@Override
	public void setSlot( int s ) {
		slot = s;
	}
	
	@Override
	public InventoryComponent getComponent() {
		return component;
	}
	
	@Override
	public void setComponent( InventoryComponent comp ) {
		component = comp;
	}
	
	@Override
	public void addSorter( Sorter...sorters ) {
		for ( Sorter s : sorters ) {
			this.sorters.add( s );
			s.setPane( this );
		}
	}
	
	@Override
	public Sorter removeSorter( String name ) {
		for ( Sorter s : sorters ) {
			if ( s.getName().equalsIgnoreCase( name ) ) {
				s.setPane( null );
				sorters.remove( s );
				return s;
			}
		}
		return null;
	}
	
	@Override
	public void setSorter( List< Sorter > sorters ) {
		this.sorters = sorters;
		for ( Sorter s : sorters ) {
			s.setPane( this );
		}
	}
	
	@Override
	public List< Sorter > getSorters() {
		return sorters;
	}
	
	@Override
	public Sorter getSorter( String name ) {
		for ( Sorter s : sorters ) {
			if ( s.getName().equalsIgnoreCase( name ) ) return s;
		}
		return null;
	}
}
