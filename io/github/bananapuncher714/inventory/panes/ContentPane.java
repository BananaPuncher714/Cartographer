package io.github.bananapuncher714.inventory.panes;

import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.util.CustomObject;
import io.github.bananapuncher714.inventory.util.ElementPlacement;
import io.github.bananapuncher714.inventory.util.SortableObject;

public interface ContentPane extends CustomObject, SortableObject {
	void sort();
	int getSlot();
	void setSlot( int slot );
	void setSize( int x, int y );
	int getWidth();
	int getHeight();
	ElementPlacement getPlacement();
	void setPlacement( ElementPlacement placement );
	Boolean isHidden();
	void hide( boolean hide );
	InventoryComponent getComponent();
	void setComponent( InventoryComponent comp );
}
