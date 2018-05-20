package io.github.bananapuncher714.inventory.panes.sorters;

import java.util.ArrayList;

import io.github.bananapuncher714.inventory.panes.ContentPane;
import io.github.bananapuncher714.inventory.util.InventoryManager;

public class VerticalSorter implements Sorter {
	protected String name;
	protected final String type = "VerticalSorter";
	protected ContentPane pane;
	
	protected int x, y;
	
	public VerticalSorter( String n ) {
		name = n;
	}
	
	@Override
	public void setPane( ContentPane p ) {
		pane = p;
		x = pane.getWidth();
		y = pane.getHeight();
	}
	
	@Override
	public ContentPane getPane() {
		return pane;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public < T > ArrayList< T > sort( ArrayList< T > elements ) {
		ArrayList< T > farray = new ArrayList< T >();
		while ( farray.size() < elements.size() ) farray.add( null );
		for ( int i = 0; i < elements.size(); i++ ) {
			int[] coord = InventoryManager.slotToCoord( i, y );
			farray.set( InventoryManager.coordToSlot( coord[ 1 ], coord[ 0 ], x, y ), elements.get( i ) );
		}
		return farray;
	}

}
