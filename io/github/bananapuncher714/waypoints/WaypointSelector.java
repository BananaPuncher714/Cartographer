package io.github.bananapuncher714.waypoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.MapText;
import io.github.bananapuncher714.cartographer.api.objects.PlayerRunnable;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.message.CLogger;
import io.github.bananapuncher714.waypoints.WaypointViewer.DisplayType;
import io.github.bananapuncher714.waypoints.inv.WaypointBananaManager;
import io.github.bananapuncher714.waypoints.inv.WaypointEditor;
import io.github.bananapuncher714.waypoints.inv.WaypointEditor.WType;

public class WaypointSelector implements CursorSelector {
	WaypointAddon addon;
	
	public WaypointSelector( WaypointAddon addon ) {
		this.addon = addon;
	}

	@Override
	public List<RealWorldCursor> getCursors( Player player ) {
		ArrayList< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		WaypointViewer viewer = addon.getManager().getViewer( player );
		
		Map< Waypoint, DisplayType > waypoints = viewer.getWaypoints();
		for ( Waypoint waypoint : waypoints.keySet() ) {
			DisplayType visibility = waypoints.get( waypoint );
			if ( visibility == DisplayType.HIDDEN ) continue;
			RealWorldCursor pCursor = new RealWorldCursor( waypoint.getLocation(), waypoint.getType(), visibility == DisplayType.VISIBLE );
			pCursor.setHoverAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					if ( extra.length != 2 ) {
						return true;
					}
					int x = ( int ) ( double ) extra[ 0 ];
					int y = ( int ) ( double ) extra[ 1 ];

					addon.getTextSelector().getSelections( player ).add( new MapText( CLogger.parse( player, "waypoints.map.cursor.hover", waypoint.getName() ), ( x + 130 ) / 2, ( y + 128 ) / 2, addon.getFont() ) );
					return true;
				}
			} );
			pCursor.setActivateAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					WaypointEditor.setWaypoint( player, waypoint, WType.PRIVATE );
					player.openInventory( WaypointBananaManager.getCustomInventory( player, "WaypointEditor" ).getInventory( false ) );
					return true;
				}
			} );
			cursors.add( pCursor );
		}
		
		Map< Waypoint, DisplayType > shared = viewer.getShared();
		for ( Waypoint waypoint : shared.keySet() ) {
			DisplayType visibility = shared.get( waypoint );
			if ( visibility == DisplayType.HIDDEN ) continue;
			RealWorldCursor sharedWaypoint = new RealWorldCursor( waypoint.getLocation(), waypoint.getType(), visibility == DisplayType.VISIBLE );
			cursors.add( sharedWaypoint );
			sharedWaypoint.setHoverAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					if ( extra.length != 2 ) {
						return true;
					}
					int x = ( int ) ( double ) extra[ 0 ];
					int y = ( int ) ( double ) extra[ 1 ];

					addon.getTextSelector().getSelections( player ).add( new MapText( CLogger.parse( player, "waypoints.map.cursor.hover", waypoint.getName() ), ( x + 130 ) / 2, ( y + 128 ) / 2, addon.getFont() ) );
					return true;
				}
			} );
			sharedWaypoint.setActivateAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					WaypointEditor.setWaypoint( player, waypoint, WType.SHARED );
					player.openInventory( WaypointBananaManager.getCustomInventory( player, "WaypointEditor" ).getInventory( false ) );
					return true;
				}
			} );
		}
		
		for ( Waypoint waypoint : addon.getManager().getPublicWaypoints().keySet() ) {
			if ( addon.getManager().isStaff( waypoint ) && !player.hasPermission( "waypoints.staff" ) ) continue;
			if ( !viewer.getPublic().containsKey( waypoint.getId() ) && !waypoints.containsKey( waypoint ) ) {
				viewer.getPublic().put( waypoint.getId(), addon.getDefaultDisplay() );
			}
			if ( viewer.getPublic().get( waypoint.getId() ) == DisplayType.HIDDEN || waypoints.containsKey( waypoint ) ) continue;
			RealWorldCursor sharedWaypoint = new RealWorldCursor( waypoint.getLocation(), waypoint.getType(), viewer.getPublic().get( waypoint.getId() ) == DisplayType.VISIBLE );
			cursors.add( sharedWaypoint );
			sharedWaypoint.setHoverAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					if ( extra.length != 2 ) {
						return true;
					}
					int x = ( int ) ( double ) extra[ 0 ];
					int y = ( int ) ( double ) extra[ 1 ];

					addon.getTextSelector().getSelections( player ).add( new MapText( CLogger.parse( player, "waypoints.map.cursor.hover", waypoint.getName() ), ( x + 130 ) / 2, ( y + 128 ) / 2, addon.getFont() ) );
					return true;
				}
			} );
			sharedWaypoint.setActivateAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					WaypointEditor.setWaypoint( player, waypoint, WType.PUBLIC );
					player.openInventory( WaypointBananaManager.getCustomInventory( player, "WaypointEditor" ).getInventory( false ) );
					return true;
				}
			} );
		}
		
		for ( UUID uuid : addon.getManager().getDiscoverable() ) {
			Waypoint waypoint = addon.getManager().getWaypoint( uuid );
			if ( waypoint == null ) continue;
			if ( !viewer.getPublic().containsKey( uuid ) || viewer.getPublic().get( waypoint.getId() ) == DisplayType.HIDDEN || waypoints.containsKey( waypoint ) ) continue;
			RealWorldCursor sharedWaypoint = new RealWorldCursor( waypoint.getLocation(), waypoint.getType(), viewer.getPublic().get( waypoint.getId() ) == DisplayType.VISIBLE );
			cursors.add( sharedWaypoint );
			sharedWaypoint.setHoverAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					if ( extra.length != 2 ) {
						return true;
					}
					int x = ( int ) ( double ) extra[ 0 ];
					int y = ( int ) ( double ) extra[ 1 ];

					addon.getTextSelector().getSelections( player ).add( new MapText( CLogger.parse( player, "waypoints.map.cursor.hover", waypoint.getName() ), ( x + 130 ) / 2, ( y + 128 ) / 2, addon.getFont() ) );
					return true;
				}
			} );
			sharedWaypoint.setActivateAction( new PlayerRunnable() {
				@Override
				public boolean run( Player player, Object... extra ) {
					WaypointEditor.setWaypoint( player, waypoint, WType.PUBLIC );
					player.openInventory( WaypointBananaManager.getCustomInventory( player, "WaypointEditor" ).getInventory( false ) );
					return true;
				}
			} );
		}
		return cursors;
	}

}
