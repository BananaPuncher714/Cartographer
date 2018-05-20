package io.github.bananapuncher714.inventory;

import java.util.ArrayList;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.util.ICEResponse;

public class CustomPagedMenu implements CustomInventory {
	protected String name;
	protected int pages;
	protected final String type = "CustomPagedMenu";
	private int currentPage = 0;
	private CustomInventory currentInv;
	
	protected ArrayList< CustomInventory > inventories = new ArrayList< CustomInventory >();
	
	public CustomPagedMenu( ArrayList< CustomInventory > menus, String n ) {
		inventories = menus;
		name = n;
	}
	
	public CustomInventory getCustomInventory( int page ) {
		return inventories.get( page );
	}
	
	public CustomInventory getCustomInventory() {
		return currentInv;
	}
	
	public int getPage() {
		return currentPage;
	}
	
	public int getPages() {
		return inventories.size();
	}
	
	public void setPage( int page ) {
		currentPage = page;
		currentInv = inventories.get( page );
	}
	
	public ArrayList< CustomInventory > getCustomInventories() {
		return inventories;
	}
	
	public void setCustomInventories( ArrayList< CustomInventory > menus ) {
		inventories = menus;
		currentInv = inventories.get( currentPage );
	}
	
	public void addCustomInventory( CustomInventory menu ) {
		inventories.add( menu );
	}
	
	public void removeCustomInventory( CustomInventory menu ) {
		if ( currentInv == menu ) {
			inventories.remove( menu );
			currentPage = 0;
			currentInv = inventories.get( 0 );
		};
		
	}
	
	public void removeCustomInventory( int index ) {
		removeCustomInventory( inventories.get( index ) );
	}

	@Override
	public Inventory getInventory( boolean update ) {
		return currentInv.getInventory( update );
	}
	
	@Override
	public void updateInventory() {
		currentInv.updateInventory();
	}
	
	@Override
	public void updateInventory( Inventory inv ) {
		currentInv.updateInventory( inv );
	}
	
	@Override
	public void saveInventory( Inventory inv ) {
		currentInv.saveInventory( inv );
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String getIdentifier() {
		return name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getSize() {
		return currentInv.getSize();
	}
	
	@Override
	public ArrayList< InventoryComponent > getComponents() {
		return currentInv.getComponents();
	}
	
	@Override
	public InventoryComponent getComponent( String name ) {
		return currentInv.getComponent( name );
	}
	
	@Override
	public void addComponent( InventoryComponent component, int slot ) {
		currentInv.addComponent( component, slot );
	}
	
	@Override
	public void addComponent( InventoryComponent... component ) {
		currentInv.addComponent( component );
	}
	
	@Override
	public void removeComponent( InventoryComponent... component ) {
		currentInv.removeComponent( component );
	}
	
	@Override
	public void removeComponent( String... name ) {
		currentInv.removeComponent( name );
	}
	
	@Override
	public void resize() {
		currentInv.resize();
	}
	
	@Override
	public ICEResponse parseICE( InventoryClickEvent e ) {
		ICEResponse ice = new ICEResponse( "PagedMenuResponse" );
		return ice;
	}
	
	@Override
	public InventoryComponent findComponent( int number ) {
		return currentInv.findComponent( number );
	}
}
