package io.github.bananapuncher714.citizens;

import org.bukkit.map.MapCursor.Type;

public class CitizenCursor {
	private Type type;
	private boolean hide;
	private boolean visible;
	private double range;
	
	public CitizenCursor( Type type, boolean visible, boolean hide, double range ) {
		this.type = type;
		this.hide = hide;
		this.range = range;
		this.visible = visible;
	}

	public Type getType() {
		return type;
	}
	
	public void setType( Type type ) {
		this.type = type;
	}

	public boolean isHidden() {
		return hide;
	}
	
	public void setHidden( boolean hidden ) {
		hide = hidden;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible( boolean visible ) {
		this.visible = visible;
	}

	public double getRange() {
		return range;
	}
	
	public void setRange( double range ) {
		this.range = range;
	}
}