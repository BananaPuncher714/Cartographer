package io.github.bananapuncher714.quests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import me.blackvein.quests.Stage;

public class QuesterSelector implements CursorSelector {
	QuestsAddon addon;
	Quests plugin;
	
	public QuesterSelector( QuestsAddon addon ) {
		this.addon = addon;
		plugin = ( Quests ) Bukkit.getPluginManager().getPlugin( "Quests" );
	}

	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		
		ArrayList< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		Quester quester = plugin.getQuester( player.getUniqueId() );
		if ( quester == null ) return cursors;
		for ( Quest quest : quester.currentQuests.keySet() ) {
			int step = quester.currentQuests.get( quest );
			Stage stage = quest.getStage( step );
			
			if ( ( stage.citizensToInteract != null ) && ( stage.citizensToInteract.size() > 0 ) ) {
				for ( int index : stage.citizensToInteract ) {
					cursors.add( new RealWorldCursor( plugin.getNPCLocation( index ), addon.getInteract(), !addon.isHighlight() ) );
				}
			} else if ( ( stage.citizensToKill != null ) && (stage.citizensToKill.size() > 0 ) ) {
				for ( int index : stage.citizensToKill ) {
					cursors.add( new RealWorldCursor( plugin.getNPCLocation( index ), addon.getKill(), !addon.isHighlight() ) );
				}
			} else if ( ( stage.locationsToReach != null ) && ( stage.locationsToReach.size() > 0 ) ) {
				for ( Location location : stage.locationsToReach ) {
					cursors.add( new RealWorldCursor( location, addon.getLoc(), !addon.isHighlight() ) );
				}
			}
		}
		
		return cursors;
	}

}