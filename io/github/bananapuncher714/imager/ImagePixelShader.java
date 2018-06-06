package io.github.bananapuncher714.imager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import io.github.bananapuncher714.cartographer.api.map.addon.PixelShader;
import io.github.bananapuncher714.cartographer.api.objects.MapCursorLocation;
import io.github.bananapuncher714.cartographer.api.objects.MapDirection;
import io.github.bananapuncher714.cartographer.api.objects.MapPixel;
import io.github.bananapuncher714.cartographer.api.objects.ZoomScale;
import io.github.bananapuncher714.cartographer.api.util.MapUtil;

public class ImagePixelShader implements PixelShader {
	static Map< UUID, Color[][] > faceImages = new HashMap< UUID, Color[][] >();
	ImagerAddon addon;
	
	public ImagePixelShader( ImagerAddon addon ) {
		this.addon = addon;
	}
	
	@Override
	public List< MapPixel > getPixels( Player receiver, double mapX, double mapZ, ZoomScale scale ) {
		List< MapPixel > pixels = new ArrayList< MapPixel >();
		if ( ( !addon.isOnByDefault() && !addon.isEnabledFor( receiver ) ) || ( addon.isOnByDefault() && addon.isEnabledFor( receiver ) )  ) {
			return pixels;
		}
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			if ( player.isSneaking() ) {
				continue;
			}
			if ( player != receiver && player.hasPermission( "cartographer.playerheads.vanish" ) ) {
				continue;
			}
			if ( player.hasPotionEffect( PotionEffectType.INVISIBILITY ) ) {
				continue;
			}
			if ( player.getLocation().getWorld() != receiver.getLocation().getWorld() ) {
				continue;
			}
			Color[][] image = faceImages.get( player.getUniqueId() );
			if ( image == null ) {
				File fileImage = new File( addon.getDataFolder() + "/heads/" + player.getUniqueId() );
				if ( fileImage.exists() ) {
					try {
						BufferedImage playerFace = ImageIO.read( fileImage );
						image = new Color[ playerFace.getWidth() ][ playerFace.getHeight() ];
						for ( int x = 0; x < playerFace.getWidth(); x++ ) {
							for ( int z = 0; z < playerFace.getHeight(); z++ ) {
								image[ x ][ z ] = new Color( playerFace.getRGB( x, z ), true );
							}
						}
					} catch ( IOException e ) {
						e.printStackTrace();
					}
					faceImages.put( player.getUniqueId(), image );
				} else {
					try {
						BufferedImage playerFace = ImageIO.read( new URL( "https://minotar.net/avatar/" + player.getName() + "/8.png" ) );
						fileImage.getParentFile().mkdirs();
						ImageIO.write( playerFace, "png", fileImage );
						image = new Color[ playerFace.getWidth() ][ playerFace.getHeight() ];
						for ( int x = 0; x < playerFace.getWidth(); x++ ) {
							for ( int z = 0; z < playerFace.getHeight(); z++ ) {
								image[ x ][ z ] = new Color( playerFace.getRGB( x, z ), true );
							}
						}
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
				if ( image == null ) {
					return pixels;
				}
			}

			Location ploc = player.getLocation();

			MapCursorLocation location = MapUtil.findRelCursorPosition( ploc, mapX, mapZ, scale );
			if ( location.getDirection() != MapDirection.CENTER ) {
				continue;
			}

			for ( int x = 0; x < image.length; x++ ) {
				for ( int z = 0; z < image[ x ].length; z++ ) {
					MapPixel pixel = new MapPixel( ( int ) ( ( location.getX() + 128 ) / 2.0 + x ), ( int ) ( ( location.getY() + 128 ) / 2.0 + z ), image[ x ][ z ] );
					if ( pixel.getX() < 128 && pixel.getZ() < 128 ) {
						pixels.add( pixel );
					}
				}
			}
		}
		return pixels;
	}

	@Override
	public int getPriority() {
		return 10;
	}

}
