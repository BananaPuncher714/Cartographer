package io.github.bananapuncher714.mythicmobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.api.map.addon.CursorSelector;
import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;

public class MythicMobCursorSelector implements CursorSelector {
	private MobManager mm;
	private Type type;
	
	public MythicMobCursorSelector( Type type ) {
		mm = ( ( MythicMobs ) Bukkit.getPluginManager().getPlugin( "MythicMobs" ) ).getMobManager();
		this.type = type;
	}

	@Override
	public List< RealWorldCursor > getCursors( Player player ) {
		List< RealWorldCursor > cursors = new ArrayList< RealWorldCursor >();
		for ( LivingEntity entity : mm.getAllMythicEntities() ) {
			cursors.add( new RealWorldCursor( entity.getLocation(), type, true ) );
		}
		return cursors;
	}

}
