package io.github.bananapuncher714.cartographer.demo;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChunkUnclaimEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Chunk chunk;
    private OfflinePlayer player;

    public ChunkUnclaimEvent(Chunk chunk, OfflinePlayer player) {
        this.chunk = chunk;
        this.player = player;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
