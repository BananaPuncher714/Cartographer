package io.github.bananapuncher714.cartographer.api.map.addon;

import java.util.List;

import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.api.objects.MapPixel;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;

public interface PixelShader {
	public abstract List< MapPixel > getPixels( Player player, double centerX, double centerY, ZoomScale scale );
	public int getPriority();
}
