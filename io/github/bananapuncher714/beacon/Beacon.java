package io.github.bananapuncher714.beacon;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bananapuncher714.cartographer.Cartographer;

public class Beacon {
	protected Color color;
	protected int radius;
	protected double speed;
	protected Location center;
	protected String name;
	Set< UUID > viewers = new HashSet< UUID >();
	protected boolean visible;
	
	private double range = 0;
	BukkitRunnable incrementer = new BukkitRunnable() {
		@Override
		public void run() {
			range = ( range + speed ) % radius;
		}
	};
	
	public Beacon( String name, Color color, int radius, double speed, Location center, boolean visible ) {
		this.name = name;
		this.color = color;
		this.radius = radius;
		this.speed = speed;
		this.center = center;
		this.visible = visible;
		incrementer.runTaskTimer( Cartographer.getMain(), 0, 1 );
	}
	
	public void destroy() {
		incrementer.cancel();
	}

	public void setColor( Color color ) {
		this.color = color;
	}

	public void setRadius( int radius ) {
		this.radius = radius;
	}

	public void setCenter( Location center ) {
		this.center = center;
	}

	public Color getColor() {
		return color;
	}

	public int getRadius() {
		return radius;
	}
	
	public double getRange() {
		return range;
	}

	public Location getCenter() {
		return center;
	}

	public double getSpeed() {
		return speed;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isViewer( Player player ) {
		return viewers.contains( player.getUniqueId() );
	}
	
	public void addViewer( Player player ) {
		viewers.add( player.getUniqueId() );
	}
	
	public void removeViewer( Player player ) {
		viewers.remove( player.getUniqueId() );
	}
}
