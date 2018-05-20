package io.github.bananapuncher714.inventory.util;

public enum ElementPlacement {
	
	N( 2 ), NE( 3 ), E( 6 ), SE( 9 ), S( 8 ), SW( 7 ), W( 4 ), NW( 1 ), CENTER( 5 ), TOPRIGHT( 11 ), TOPLEFT( 10 ), BOTTOMRIGHT( 13 ), BOTTOMLEFT( 12 );
	
	int direction;
	
	private ElementPlacement( int dir ) {
		direction = dir;
	}
	
	public int getId() {
		return direction;
	}

}
