package io.github.bananapuncher714.inventory.panes.sorters;

import java.util.ArrayList;

import io.github.bananapuncher714.inventory.panes.ContentPane;
import io.github.bananapuncher714.inventory.util.CustomObject;

public interface Sorter extends CustomObject {
	< T > ArrayList< T > sort( ArrayList< T > elements );
	void setPane( ContentPane pane );
	ContentPane getPane();
}
