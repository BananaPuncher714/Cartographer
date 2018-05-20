package io.github.bananapuncher714.inventory.util;

import java.util.ArrayList;

import io.github.bananapuncher714.inventory.components.ButtonComponent;

public interface PagedObject {
	int getPage();
	void setPage( int page );
	ArrayList< ButtonComponent > getButtons();
	void addButtons( ButtonComponent... button );
	void loadButtons();
	boolean lastPage();
	boolean isRound();
	Object getPageObject();
}
