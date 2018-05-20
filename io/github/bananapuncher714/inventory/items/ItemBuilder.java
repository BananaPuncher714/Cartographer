package io.github.bananapuncher714.inventory.items;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
	ItemStack item;
	
	public ItemBuilder( ItemStack i ) {
		item = i;
	}
	
	public ItemBuilder( ItemStack i, String n, String... l ) {
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName( n );
		ArrayList< String > lore = new ArrayList< String >( Arrays.asList( l ) );
		meta.setLore( lore );
		i.setItemMeta( meta );
		item = i;
	}	
	
	public ItemBuilder( ItemStack i, String n, boolean e, String... l ) {
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName( n );
		ArrayList< String > lore = new ArrayList< String >( Arrays.asList( l ) );
		meta.setLore( lore );
		i.setItemMeta( meta );
		if ( e ) i.addUnsafeEnchantment( Enchantment.DURABILITY, 1 );
		item = i;
	}
	
	public ItemBuilder( Material m, int a, byte d, String n, boolean e, String... l ) {
		ItemStack i = new ItemStack( m, a, d );
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName( n );
		ArrayList< String > lore = new ArrayList< String >( Arrays.asList( l ) );
		meta.setLore( lore );
		i.setItemMeta( meta );
		if ( e ) i.addUnsafeEnchantment( Enchantment.DURABILITY, 1 );
		item = i;
	}
	
	public ItemBuilder( Material m, int a, byte d, String n, String... l ) {
		ItemStack i = new ItemStack( m, a, d );
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName( n );
		ArrayList< String > lore = new ArrayList< String >( Arrays.asList( l ) );
		meta.setLore( lore );
		i.setItemMeta( meta );
		item = i;
	}
	
	public ItemStack getItem() {
		return item.clone();
	}
	
	public ItemBuilder setDurability( short d ) {
		item.setDurability( d );
		return this;
	}
	
	public ItemBuilder setAmount( int a ) {
		item.setAmount( a );
		return this;
	}
	
	public ItemBuilder addFlags( ItemFlag... flags ) {
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags( flags );
		item.setItemMeta( meta );
		return this;
	}
	
	public ItemBuilder removeFlags( ItemFlag... flags ) {
		ItemMeta meta = item.getItemMeta();
		meta.removeItemFlags( flags );
		item.setItemMeta( meta );
		return this;
	}
	
	public ItemBuilder setLore( String... l ) {
		ItemMeta meta = item.getItemMeta();
		ArrayList< String > lore = new ArrayList< String >( Arrays.asList( l ) );
		meta.setLore( lore );
		item.setItemMeta( meta );
		return this;
	}
	
	public ItemBuilder setName( String n ) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( n );
		item.setItemMeta( meta );
		return this;
	}

}
