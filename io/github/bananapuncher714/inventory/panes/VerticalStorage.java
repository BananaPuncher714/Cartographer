package io.github.bananapuncher714.inventory.panes;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.panes.sorters.VerticalSorter;
import io.github.bananapuncher714.inventory.util.ElementPlacement;

public class VerticalStorage extends StoragePane {
	protected final String type = "VerticalStoragePane";
	
	public VerticalStorage( String n  ) {
		this( n, 1, 1, new ArrayList< ItemStack >(), ElementPlacement.CENTER );
	}
	
	public VerticalStorage( String n, int a, int b ) {
		this( n, a, b, new ArrayList< ItemStack >(), ElementPlacement.CENTER );
	}
	
	public VerticalStorage( String n, int a, int b, ArrayList< ItemStack > i ) {
		this( n, a, b, i, ElementPlacement.CENTER );
	}
	
	public VerticalStorage( String n, int a, int b, ArrayList< ItemStack > i, ElementPlacement p ) {
		name = n;
		contents = i;
		placement = p;
		x = a;
		y = b;
	}
	
	@Override
	public ArrayList< ItemStack > getContents() {
		super.getContents();
		sort();
		return contents;
	}

	@Override
	public void sort() {
		VerticalSorter vs = new VerticalSorter( "Sorter1" );
		contents = vs.sort( contents );
	}

	@Override
	public String getType() {
		return type;
	}
}
