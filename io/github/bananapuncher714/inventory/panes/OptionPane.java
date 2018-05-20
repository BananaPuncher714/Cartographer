package io.github.bananapuncher714.inventory.panes;

import java.util.ArrayList;

import io.github.bananapuncher714.inventory.actionItem.ActionItem;
import io.github.bananapuncher714.inventory.util.ElementPlacement;

public class OptionPane extends ActionItemPane {
	protected final String type = "OptionPane";
	
	public OptionPane( String n ) {
		this( n, new ArrayList< ActionItem >(), ElementPlacement.CENTER );
	}
	
	public OptionPane( String n, ArrayList< ActionItem > i ) {
		this( n, i, ElementPlacement.CENTER );
	}
	
	public OptionPane( String n, ArrayList< ActionItem > i, ElementPlacement p ) {
		name = n;
		actionItems = i;
		placement = p;
	}
	
	@Override
	public String getType() {
		return name;
	}
}
