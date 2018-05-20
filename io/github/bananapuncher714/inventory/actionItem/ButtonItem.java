package io.github.bananapuncher714.inventory.actionItem;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.components.ButtonComponent;
import io.github.bananapuncher714.inventory.panes.ActionItemPane;

public class ButtonItem extends ActionItem {
	protected final String type = "ButtonItem";
	protected ButtonComponent button;

	public ButtonItem( String n, String a, ActionItemIntention i ) {
		this( n, a, i, new ItemStack( Material.AIR ) );
	}
	
	public ButtonItem( String n, String a, ActionItemIntention i, ItemStack it ) {
		super( n, a, i, it );
	}
	
	public ButtonItem( String n, ArrayList< String > a, ActionItemIntention i ) {
		this( n, a, i, new ItemStack( Material.AIR ) );
	}
	
	public ButtonItem( String n, ArrayList< String > a, ActionItemIntention i, ItemStack it ) {
		super( n, a, i, it );
	}
	
	public ButtonComponent getButton() {
		return button;
	}
	
	public void setButton( ButtonComponent b ) {
		button = b;
	}
	
	@Override
	public ActionItemPane getPane() {
		return null;
	}
}
