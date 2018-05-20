package io.github.bananapuncher714.inventory.components;

import io.github.bananapuncher714.inventory.actionItem.ButtonItem;

public class BananaButton extends ButtonComponent {
	protected final String type = "BananaButton";
	
	public BananaButton( String n ) {
		this( n, 0, null );
	}
	
	public BananaButton( String n, int s ) {
		this( n, s, null );
	}
	
	public BananaButton( String n, int s, ButtonItem i ) {
		name = n;
		slot = s;
		item = i;
	}

	@Override
	public String getType() {
		return type;
	}
	
	public int getWidth() {
		return 1;
	}
	
	public int getHeight() {
		return 1;
	}
}
