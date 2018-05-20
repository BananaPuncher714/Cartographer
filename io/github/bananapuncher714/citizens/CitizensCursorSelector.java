package io.github.bananapuncher714.citizens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.MapManager;
import io.github.bananapuncher714.cartographer.api.map.Minimap;
import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.MapText;
import io.github.bananapuncher714.cartographer.api.objects.PlayerRunnable;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.github.bananapuncher714.cartographer.message.CLogger;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class CitizensCursorSelector implements CursorSelector {
	boolean stop = false;
	
	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		List< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		if ( stop ) {
			return cursors;
		}
		Minimap map = MapManager.getInstance().getPlayerMap( player );
		CitizensAddon addon = ( CitizensAddon ) map.getModules().get( "citizens" );
		for ( NPC npc : CitizensAPI.getNPCRegistry().sorted() ) {
			Entity entity = npc.getEntity();
			if ( entity != null ) {
				if ( entity.getWorld() != player.getWorld() ) continue;
				CitizenCursor cursor = addon.getManager().getCursor( npc.getUniqueId() );
				if ( !cursor.isVisible() ) {
					continue;
				}
				if ( player.hasPermission( "cartographer.citizens.bypass.range" ) || cursor.getRange() <= 0 || player.getLocation().distanceSquared( entity.getLocation() ) <= cursor.getRange() * cursor.getRange() ) {
					RealWorldCursor citiCursor = new RealWorldCursor( entity.getLocation(), cursor.getType(), cursor.isHidden() );
					cursors.add( citiCursor );
					citiCursor.setHoverAction( new PlayerRunnable() {
						@Override
						public boolean run( Player player, Object... extra ) {
							if ( extra.length != 2 ) {
								return true;
							}
							int x = ( int ) ( double ) extra[ 0 ];
							int y = ( int ) ( double ) extra[ 1 ];
							
							CitizensTextSelector.getSelections( player ).add( new MapText( CLogger.parse( player, "citizens.map.cursor.hover", ChatColor.stripColor( npc.getFullName() ) ), ( x + 130 ) / 2, ( y + 128 ) / 2, addon.getFont() ) );
							return true;
						}
					} );
				}
			}
		}
		return cursors;
	}
	
	protected void stop() {
		stop = true;
	}

}
