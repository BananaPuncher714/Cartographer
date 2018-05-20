package io.github.bananapuncher714.cartographer.api.map.addon;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;

public interface MapCursorSelector {
	public abstract List< MapCursor > getCursors( Player player );
}
