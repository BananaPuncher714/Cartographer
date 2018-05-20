package io.github.bananapuncher714.inventory;

import java.util.ArrayList;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.util.CustomObject;
import io.github.bananapuncher714.inventory.util.ICEResponse;

public interface CustomInventory extends CustomObject {
	Inventory getInventory( boolean update );
	void updateInventory();
	void updateInventory( Inventory inv );
	void saveInventory( Inventory inv );
	String getIdentifier();
	int getSize();
	ArrayList< InventoryComponent > getComponents();
	InventoryComponent getComponent( String name );
	void addComponent( InventoryComponent component, int number );
	void addComponent( InventoryComponent... component );
	void removeComponent( InventoryComponent... component );
	void removeComponent( String... name );
	void resize();
	ICEResponse parseICE( InventoryClickEvent e );
	InventoryComponent findComponent( int number );
}
