package io.github.bananapuncher714.inventory.util;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.panes.ContentPane;

public class ICEResponse implements CustomObject {
	protected String name;
	protected final String type = "ICEResponse";
	InventoryComponent component;
	ContentPane pane;
	ActionItem aitem;
	ItemStack nitem;
	
	
	public ICEResponse( String n ) {
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}
	
	public InventoryComponent getComponent() {
		return component;
	}
	
	public ContentPane getPane() {
		return pane;
	}
	
	public void setComponent( InventoryComponent c ) {
		component = c;
	}
	
	public void setPane( ContentPane p ) {
		pane = p;
	}
	
	public ItemStack getItemStack() {
		return nitem;
	}
	
	public void setItem( ItemStack item ) {
		nitem = item;
	}
	
	public ActionItem getActionItem() {
		return aitem;
	}
	
	public void setActionItem( ActionItem item ) {
		aitem = item;
	}
}