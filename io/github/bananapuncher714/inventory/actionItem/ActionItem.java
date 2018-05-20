package io.github.bananapuncher714.inventory.actionItem;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.panes.ActionItemPane;
import io.github.bananapuncher714.inventory.util.CustomObject;

public class ActionItem implements CustomObject {
	protected String name;
	protected ArrayList< String > action;
	protected final String type = "ActionItem";
	protected ItemStack item;
	protected ActionItemIntention intent;
	protected ActionItemPane pane;
	
	public ActionItem( String n, String a, ActionItemIntention i ) {
		this( n, a, i, new ItemStack( Material.AIR ) );
	}
	
	public ActionItem( String n, String a, ActionItemIntention i, ItemStack it ) {
		action = new ArrayList< String >();
		name = n;
		action.add( a );
		intent = i;
		item = it;
	}
	
	public ActionItem( String n, ArrayList< String > a, ActionItemIntention i ) {
		this( n, a, i, new ItemStack( Material.AIR ) );
	}
	
	public ActionItem( String n, ArrayList< String > a, ActionItemIntention i, ItemStack it ) {
		name = n;
		action = a;
		intent = i;
		item = it;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	public ArrayList< String > getActions() {
		return action;
	}
	
	public void addAction( String a ) {
		action.add( a );
	}
	
	public void removeAction( String a ) {
		action.remove( a );
	}
	
	public ActionItemIntention getIntent() {
		return intent;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem( ItemStack i ) {
		item = i;
	}
	
	public ActionItemPane getPane() {
		return pane;
	}
	
	public void setPane( ActionItemPane p ) {
		pane = p;
	}

}