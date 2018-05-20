package io.github.bananapuncher714.inventory.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomHolder implements InventoryHolder {
	String identifier;
	
	public CustomHolder( String identifier ) {
		this.identifier = identifier;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}
	
	public String getIdentifier() {
		return identifier;
	}

}
