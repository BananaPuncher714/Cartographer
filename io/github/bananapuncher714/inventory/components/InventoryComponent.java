package io.github.bananapuncher714.inventory.components;

import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.util.CustomObject;

public interface InventoryComponent extends CustomObject {
	void setSlot( int slot );
	int getSlot();
	int getWidth();
	int getHeight();
	CustomInventory getInventory();
	void setInventory( CustomInventory inv );
}