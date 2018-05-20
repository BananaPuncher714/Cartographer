package io.github.bananapuncher714.cartographer.api.map.addon;

import java.util.List;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.objects.RealWorldCursor;

public interface CursorSelector {
	public abstract List< RealWorldCursor > getCursors( Player player );
}