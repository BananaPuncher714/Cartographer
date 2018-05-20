package io.github.bananapuncher714.cartographer.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.map.MapView;

public final class ReflectionUtils {
	private static Class< ? > minecraftServer;
	private static Method getServer;
	private static Field recentTps;
	private static String version;
	
	static {
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			minecraftServer = Class.forName( "net.minecraft.server." + version + ".MinecraftServer" );
			getServer = minecraftServer.getMethod( "getServer" );
			recentTps = minecraftServer.getField( "recentTps" );
		} catch ( Exception exception ) {
			
		}
	}
	
	public static double[] getTps() {
		try {
			return ( double[] ) recentTps.get( getServer.invoke( null ) );
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isMainHand( PlayerInteractEvent e ) {
		Method getHand;
		try {
			getHand = e.getClass().getMethod( "getHand" );
			return EquipmentSlot.HAND.equals( getHand.invoke( e ) );
		} catch ( NoSuchMethodException |
				SecurityException |
				IllegalAccessException |
				IllegalArgumentException |
				InvocationTargetException e1 ) {
			return true;
		}
	}
	
	public static boolean isExplorerMap( MapView view ) {
		try {
			Field mapField = view.getClass().getDeclaredField( "worldMap" );
			mapField.setAccessible( true );
			Object worldMap = mapField.get( view );
			Field iconField = worldMap.getClass().getDeclaredField( "decorations" );
			iconField.setAccessible( true );
			Map< Object, Object > mapIcons = ( Map< Object, Object > ) iconField.get( worldMap );
			for ( Object object : mapIcons.values() ) {
				Field typeField = object.getClass().getDeclaredField( "type" );
				typeField.setAccessible( true );
				Object type = typeField.get( object );
				if ( type.toString().equalsIgnoreCase( "MANSION" ) || type.toString().equalsIgnoreCase( "MONUMENT" ) ) {
					return true;
				}
			}
		} catch ( Exception exception ) {
			return false;
		}
		return false;
	}
}
