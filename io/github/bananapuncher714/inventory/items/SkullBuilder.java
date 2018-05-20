package io.github.bananapuncher714.inventory.items;

import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class SkullBuilder extends ItemBuilder {

	public SkullBuilder( String n, String o, int a, boolean g, String... l ) {
		super( Material.SKULL_ITEM, a, ( byte ) 3, n, g, l );
		SkullMeta meta = ( SkullMeta ) item.getItemMeta();
		meta.setOwner( o );
		item.setItemMeta( meta );
	}
	
	public SkullBuilder( String n, String o, int a, String... l ) {
		super( Material.SKULL_ITEM, a, ( byte ) 3, n, false, l );
		SkullMeta meta = ( SkullMeta ) item.getItemMeta();
		meta.setOwner( o );
		item.setItemMeta( meta );
	}
	
	public SkullBuilder( String n, String o, boolean isURL, String... l ) {
		super( Material.SKULL_ITEM, 1, ( byte ) 3, n, false, l );
		if ( isURL ) {
			setSkullSkin( item, o );
		} else {
			SkullMeta meta = ( SkullMeta ) item.getItemMeta();
			meta.setOwner( o );
			item.setItemMeta( meta );
		}
	}
	
	public ItemStack getSkull( String skinURL ) {
		ItemStack skull = new ItemStack( Material.SKULL_ITEM, 1, ( short ) 3 );
		setSkullSkin( skull, skinURL );
		return skull;
	}
	
    private void setSkullSkin( ItemStack head, String skinURL ) {
        if ( skinURL.isEmpty() ) return;
       
        ItemMeta headMeta = head.getItemMeta();
        GameProfile profile = new GameProfile( UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64( String.format( "{textures:{SKIN:{url:\"%s\"}}}", skinURL ).getBytes() );
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
    }
    
    public final static ItemStack getHead( String skinURL ) {
    	ItemStack head = new ItemStack( Material.SKULL_ITEM, 1, ( short ) 3 );
    	if ( skinURL == null || skinURL.isEmpty() ) {
    		return head;
    	}
    	ItemMeta headMeta = head.getItemMeta();
        GameProfile profile = new GameProfile( UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64( String.format( "{textures:{SKIN:{url:\"%s\"}}}", skinURL ).getBytes() );
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

}
