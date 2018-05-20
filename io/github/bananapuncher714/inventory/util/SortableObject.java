package io.github.bananapuncher714.inventory.util;

import java.util.List;

import io.github.bananapuncher714.inventory.panes.sorters.Sorter;

public interface SortableObject {
	void addSorter( Sorter... sorters );
	Sorter removeSorter( String name );
	Sorter getSorter( String name );
	void setSorter( List< Sorter > sorters );
	List< Sorter > getSorters();
}
