package io.github.bananapuncher714.inventory.panes;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.actionItem.ActionItemIntention;
import io.github.bananapuncher714.inventory.components.ButtonComponent;
import io.github.bananapuncher714.inventory.util.ElementPlacement;
import io.github.bananapuncher714.inventory.util.PagedObject;

public class PagedStoragePane extends StoragePane implements PagedObject {
	private final String type = "PagedStoragePane";
	int page = 0;
	int maxPages;
	boolean round = false;
	
	ArrayList< ButtonComponent > buttons = new ArrayList< ButtonComponent >();
	
	public PagedStoragePane( String n  ) {
		this( n, 1, 1, new ArrayList< ItemStack >(), ElementPlacement.CENTER );
	}
	
	public PagedStoragePane( String n, int a, int b ) {
		this( n, a, b, new ArrayList< ItemStack >(), ElementPlacement.CENTER );
	}
	
	public PagedStoragePane( String n, int a, int b, ArrayList< ItemStack > i ) {
		this( n, a, b, i, ElementPlacement.CENTER );
	}
	
	public PagedStoragePane( String n, int a, int b, ArrayList< ItemStack > i, ElementPlacement p ) {
		name = n;
		contents = i;
		placement = p;
		x = a;
		y = b;
	}
	
	@Override
	public void sort() {
		return;
	}
	
	@Override
	public ArrayList< ItemStack > getContents() {
		contents = super.getContents();
		int area = x * y;
		int pages = getMaxPages();
		if ( page >= pages ) page = pages - 1;
		if ( page < 0 ) page = 0;
		ArrayList< ItemStack > pageContents = new ArrayList< ItemStack >();
		for ( int i = 0; i < area; i++ ) {
			int index = page * area + i;
			while ( index >= contents.size() ) contents.add( null );
			pageContents.add( contents.get( index ) );
		}
		return pageContents;
		
	}

	public ArrayList< ItemStack > getAllContents() {
		return super.getContents();
	}
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage( int p ) {
		page = p;
		loadButtons();
	}

	public int getMaxPages() {
		int area = x * y;
		int size = contents.size();
		int excess = size % area;
		int pageCount = ( size - excess ) / area;
		if ( excess > 0 || pageCount == 0 ) pageCount++;
		return pageCount;
	}
	
	@Override
	public boolean lastPage() {
		return page == getMaxPages() - 1;
	}

	@Override
	public boolean isRound() {
		return round;
	}

	@Override
	public Object getPageObject() {
		return null;
	}
	
	@Override
	public void setItem( ItemStack item, int index ) {
		slot = page * x * y + index;
		contents.set( slot, item );
	}
	
	@Override
	public ArrayList< ButtonComponent > getButtons() {
		return buttons;
	}
	
	@Override
	public void addButtons( ButtonComponent... buttonss ) {
		for ( ButtonComponent button : buttonss ) {
			buttons.add( button );
			button.addPane( this );
		}
	}
	
	@Override
	public void loadButtons() {
		for ( ButtonComponent button : buttons ) {
			ActionItemIntention intent = button.getItem().getIntent();
			if ( intent.equals( ActionItemIntention.NEXT ) ) {
				if ( lastPage() ) button.hide( true );
				else button.hide( false );
			} else if ( intent.equals( ActionItemIntention.PREVIOUS ) ) {
				if ( page == 0 ) button.hide( true );
				else button.hide( false );
			}
		}
	}
}
