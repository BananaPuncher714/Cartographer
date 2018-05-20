package io.github.bananapuncher714.inventory.actionItem;

public enum ActionItemIntention {
	
	NEXT( 1 ), PREVIOUS( 2 ), UP( 3 ), DOWN( 4 ), CUSTOM( 5 ), NONE( 6 );
	 
	int reason;
	
	private ActionItemIntention( int r ) {
		reason = r;
	}

}
