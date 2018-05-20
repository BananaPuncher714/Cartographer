package io.github.bananapuncher714.inventory.panes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.panes.sorters.Sorter;
import io.github.bananapuncher714.inventory.util.ElementPlacement;

public abstract class StoragePane implements ContentPane {
	protected ElementPlacement placement;
	protected String name;
	protected ItemStack placeholder;
	protected InventoryComponent component;
	protected boolean hidden = false;
	protected int x = 1, y = 1, slot;
	
	protected List< Sorter > sorters = new ArrayList< Sorter >();
	protected ArrayList< ItemStack > contents = new ArrayList< ItemStack >();
	
	@Override
	public void sort() {
		for ( Sorter s : sorters ) {
			contents = s.sort( contents );
		}
	}

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

	public ArrayList< ItemStack > getContents() {
		int area = x * y;
		for ( Iterator< ItemStack > it = contents.iterator(); it.hasNext(); ) {
			ItemStack item = it.next();
			if ( item == null ) it.remove();
		}
		while( contents.size() < area ) contents.add( null );
		return contents;
	}
	
	public void setContents( ArrayList< ItemStack > c ) {
		contents = c;
	}

	public void setItem( ItemStack item, int index ) {
		contents.set( index, item );
	}

	public void addItem( ItemStack item ) {
		contents.add( item );
	}

	public void removeItem( ItemStack item ) {
		contents.remove( item );
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
