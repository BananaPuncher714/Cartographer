package io.github.bananapuncher714.cartographer.api.map.addon;

import java.util.List;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.objects.MapText;

public interface TextSelector {
	public abstract List< MapText > getText( Player player );
}