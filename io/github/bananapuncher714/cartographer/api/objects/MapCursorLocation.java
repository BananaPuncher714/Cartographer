package io.github.bananapuncher714.cartographer.api.objects;

public class MapCursorLocation {
	protected double x, y;
	protected MapDirection direction;
	
	public MapCursorLocation( double x, double y, MapDirection direction ) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public double getX() {
		return Math.min( 126, Math.max( -128, x ) );
	}

	public double getY() {
		return Math.min( 126, Math.max( -128, y ) );
	}
	
	public double getRawX() {
		return x;
	}
	
	public double getRawY() {
		return y;
	}

	public MapDirection getDirection() {
		return direction;
	}
}