package io.github.bananapuncher714.inventory.panes;

import java.util.ArrayList;

import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.actionItem.ActionItemIntention;
import io.github.bananapuncher714.inventory.components.ButtonComponent;
import io.github.bananapuncher714.inventory.util.PagedObject;

public class ScrollingOptionPane extends PagedOptionPane implements PagedObject {

	public ScrollingOptionPane( String n ) {
		super( n );
	}
	
	@Override
	public ArrayList< ActionItem > getContents() {
		int area = x * y;
		int pages = getMaxPages();
		if ( page >= pages ) page = pages - 1;
		if ( page < 0 ) page = 0;
		ArrayList< ActionItem > contents = new ArrayList< ActionItem >();
		for ( int i = 0; i < area; i++ ) {
			int index = ( page * x ) + i;
			if ( actionItems.size() > index ) contents.add( actionItems.get( index ) );
			else break;
		}
		return contents;
	}
	
	@Override
	public int getMaxPages() {
		int area = x * y;
		int size = actionItems.size();
		int excess = size % area;
		int totalPages = ( int ) Math.ceil( excess / ( double ) x );
		return totalPages;
	}
	
	@Override
	public void loadButtons() {
		for ( ButtonComponent button : buttons ) {
			ActionItemIntention intent = button.getItem().getIntent();
			if ( intent.equals( ActionItemIntention.DOWN ) ) {
				if ( lastPage() ) button.hide( true );
				else button.hide( false );
			} else if ( intent.equals( ActionItemIntention.UP ) ) {
				if ( page == 0 ) button.hide( true );
				else button.hide( false );
			} else if ( intent.equals( ActionItemIntention.NONE ) ) {
				if ( hidden ) button.hide( true );
				else button.hide( false );
			}
		}
	}

}
