package io.github.bananapuncher714.inventory.panes;

import java.util.ArrayList;

import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.actionItem.ActionItemIntention;
import io.github.bananapuncher714.inventory.components.ButtonComponent;
import io.github.bananapuncher714.inventory.util.PagedObject;

public class PagedOptionPane extends OptionPane implements PagedObject {
	protected final String type = "PagedOptionPane";
	
	int page = 0;
	boolean round = false;
	
	ArrayList< ButtonComponent > buttons = new ArrayList< ButtonComponent >();

	public PagedOptionPane( String n ) {
		super( n );
	}
	
	public int getPage() {
		return page;
	}
	
	public void setPage( int p ) {
		page = p;
		loadButtons();
	}
	
	@Override
	public ArrayList< ActionItem > getContents() {
		int area = x * y;
		int pages = getMaxPages();
		if ( page >= pages ) page = pages - 1;
		if ( page < 0 ) page = 0;
		ArrayList< ActionItem > contents = new ArrayList< ActionItem >();
		for ( int i = 0; i < area; i++ ) {
			int index = ( page * area ) + i;
			if ( actionItems.size() > index ) contents.add( actionItems.get( index ) );
			else break;
		}
		return contents;
	}
	
	public ArrayList< ActionItem > getAllContents() {
		return actionItems;
	}
	
	public int getMaxPages() {
		int area = x * y;
		int size = actionItems.size();
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
			} else if ( intent.equals( ActionItemIntention.NONE ) ) {
				if ( hidden ) button.hide( true );
				else button.hide( false );
			}
		}
	}

}
