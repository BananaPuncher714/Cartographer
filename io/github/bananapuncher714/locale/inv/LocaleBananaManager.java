package io.github.bananapuncher714.locale.inv;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import io.github.bananapuncher714.inventory.CustomInventory;
import io.github.bananapuncher714.inventory.util.CustomHolder;

/**
 * This class manages all custom inventories created with BananaInventoryAPI
 */
public class LocaleBananaManager implements Listener {
    private static HashMap<UUID, HashMap<String, CustomInventory>> inventoryCache = new HashMap<UUID, HashMap<String, CustomInventory>>();

    public static CustomInventory getCustomInventory(Player player, String category) {
        if (inventoryCache.containsKey(player.getUniqueId())) {
            HashMap<String, CustomInventory> cache = inventoryCache.get(player.getUniqueId());
            if ( cache.containsKey(category) ) {
                return cache.get(category);
            } else {
                CustomInventory inventory = invokeGetInventory( player, category );
                cache.put( category, inventory );
                return inventory;
            }
        } else {
            HashMap<String, CustomInventory> cache = new HashMap<String, CustomInventory>();
            CustomInventory inventory = invokeGetInventory( player, category );
            cache.put( category, inventory );
            inventoryCache.put( player.getUniqueId(), cache );
            return inventory;
        }
    }

    public static CustomInventory invokeGetInventory( Player player, String category ) {
        try {
            Class<?> clazz = Class.forName( LocaleBananaManager.class.getPackage().getName() + "." + category);
            Method invMethod = clazz.getMethod("getInventory", Player.class);
            CustomInventory cinv = (CustomInventory) invMethod.invoke(null, player);
            return cinv;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean invokeInventoryClickEvent( InventoryClickEvent event, CustomInventory inventory, String identifier ) {
        try {
            Class<?> clazz = Class.forName( LocaleBananaManager.class.getPackage().getName() + "." + identifier);
            Method invMethod = clazz.getMethod("executeInventoryClickEvent", InventoryClickEvent.class, CustomInventory.class );
            return ( boolean ) invMethod.invoke(null, event, inventory);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public static boolean invokeInventoryCloseEvent(InventoryCloseEvent event, CustomInventory inventory, String identifier) {
        try {
            Class<?> clazz = Class.forName( LocaleBananaManager.class.getPackage().getName() + "." + identifier);
            Method invMethod = clazz.getMethod("executeInventoryCloseEvent", InventoryCloseEvent.class, CustomInventory.class );
            return ( boolean ) invMethod.invoke(null, event, inventory);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if ( !( event.getWhoClicked() instanceof Player )) return;
        InventoryHolder invHolder = event.getInventory().getHolder();
        if ( !( invHolder instanceof CustomHolder)) return;
        CustomHolder holder = ( CustomHolder ) invHolder;
        event.setCancelled( true );
        String category = holder.getIdentifier();
        Player player = ( Player ) event.getWhoClicked();
        if (inventoryCache.containsKey(player.getUniqueId())) {
            HashMap<String, CustomInventory> cache = inventoryCache.get(player.getUniqueId());
            if ( cache.containsKey(category) ) {
                if ( invokeInventoryClickEvent( event, cache.get( category ), category ) ) {
                    cache.remove( category );
                    if ( cache.isEmpty() ) inventoryCache.remove( player.getUniqueId() );
                }
            }
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if ( !( event.getPlayer() instanceof Player ) ) return;
        InventoryHolder invHolder = event.getInventory().getHolder();
        if ( !( invHolder instanceof CustomHolder)) return;
        CustomHolder holder = ( CustomHolder ) invHolder;
        String category = holder.getIdentifier();
        Player player = ( Player ) event.getPlayer();
        if (inventoryCache.containsKey(player.getUniqueId())) {
            HashMap<String, CustomInventory> cache = inventoryCache.get(player.getUniqueId());
            if ( cache.containsKey(category) ) {
                if ( invokeInventoryCloseEvent( event, cache.get( category ), category ) ) {
                    cache.remove( category );
                    if ( cache.isEmpty() ) inventoryCache.remove( player.getUniqueId() );
                }
            }
        }
    }
}