package io.github.bananapuncher714.inventory.components;

public class BoxPanel extends InventoryPanel {
	protected final String type = "BoxPanel";
	
	public BoxPanel( String n ) {
		name = n;
	}
	
	public BoxPanel( String n, int s ) {
		name = n;
		slot = s;
	}

	@Override
	public String getType() {
		return type;
	}
}
