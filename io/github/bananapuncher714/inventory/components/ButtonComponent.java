package io.github.bananapuncher714.inventory.components;

import java.util.ArrayList;

import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.actionItem.ButtonItem;
import io.github.bananapuncher714.inventory.panes.ContentPane;

public abstract class ButtonComponent implements InventoryComponent {
	protected String name;
	ButtonItem item;
	boolean hidden = false;
	protected CustomInventory inventory;
	int slot;
	
	ArrayList< ContentPane > panes = new ArrayList< ContentPane >();
	
	public ButtonItem getItem() {
		return item;
	}
	
	public void setItem( ButtonItem i ) {
		i.setButton( this );
		item = i;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void hide( boolean hide ) {
		hidden = hide;
	}
	
	public ArrayList< ContentPane > getPanes() {
		return panes;
	}
	
	public void addPane( ContentPane pane ) {
		panes.add( pane );
	}
	
	@Override
	public abstract String getType();
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setSlot( int s ) {
		slot = s;
	}

	@Override
	public int getSlot() {
		return slot;
	}
	
	@Override
	public CustomInventory getInventory() {
		return inventory;
	}
	
	@Override
	public void setInventory( CustomInventory inv ) {
		inventory = inv;
	}
}
