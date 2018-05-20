package io.github.bananapuncher714.inventory.components;

import io.github.bananapuncher714.inventory.panes.ContentPane;

public class RevolvingPanel extends InventoryPanel {
	private final String type = "RevolvingPanel";
	
	public RevolvingPanel( String n, int s ) {
		name = n;
		slot = s;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public void unhidePane( ContentPane c ) {
		for ( ContentPane pane : panes.values() ) {
			if ( c == pane ) c.hide( false );
			else pane.hide( true );
		}
	}
	
	public void unhidePane( String n ) {
		for ( ContentPane pane : panes.values() ) {
			if ( n.equalsIgnoreCase( pane.getName() ) ) pane.hide( false );
			else pane.hide( true );
		}
	}
	
	public ContentPane getMainPane() {
		for ( ContentPane pane : panes.values() ) {
			if ( !pane.isHidden() ) return pane;
		}
		return null;
	}
}
