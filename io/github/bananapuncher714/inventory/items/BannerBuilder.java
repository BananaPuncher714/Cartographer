package io.github.bananapuncher714.inventory.items;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerBuilder extends ItemBuilder {
	
	public BannerBuilder( String n, int a, byte c, boolean g, String... l ) {
		super( Material.BANNER, a, c, n, g, l );
	}
	
	public BannerBuilder( String n, int a, byte c, String... l ) {
		super( Material.BANNER, a, c, n, false, l );
	}
	
	public BannerBuilder( String n, byte c, String... l ) {
		super( Material.BANNER, 1, c, n, false, l );
	}
	
	public BannerBuilder addPattern( Pattern p ) {
		BannerMeta meta = ( BannerMeta ) item.getItemMeta();
		meta.addPattern( p );
		item.setItemMeta( meta );
		return this;
	}
	
	public BannerBuilder setPattern( Pattern... p ) {
		BannerMeta meta = ( BannerMeta ) item.getItemMeta();
		meta.setPatterns( new ArrayList< Pattern >( Arrays.asList( p ) ) );
		item.setItemMeta( meta );
		return this;
	}
	
	public int numberOfPatters() {
		return ( ( BannerMeta ) item.getItemMeta() ).numberOfPatterns();
	}

}
