package io.github.bananapuncher714.cartographer.api.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapPalette;

@SuppressWarnings("deprecation")
public final class ImageUtil {
	private static HashSet< Color > colors;
	private static final Color BLACK = new Color( 0, 0, 0 );
	private static final double[] color_multipliers = { 0.4375D, 0.1875D, 0.3125D, 0.0625D };

	static {
		colors = new HashSet< Color >();
		for ( int idx = 0; idx < 128; idx++ ) {
			colors.add( MapPalette.getColor( ( byte ) idx ) );
		}
	}

	public static void dumpColors( File file ) {
		if ( !file.exists() ) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		for ( int i = 0; i < 128; i++ ) {
			Color color = MapPalette.getColor( ( byte ) i );
			config.set( "map-colors." + i, color.getRed() + "," + color.getGreen() + "," + color.getBlue() );
		}
		try {
			config.save( file );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	public static BufferedImage getImage( String url ) throws IOException, MalformedURLException {
		BufferedImage image = null;
		URLConnection connection = null;
		try {
			connection = new URL( url ).openConnection();
		} catch ( MalformedURLException exception ) {
			throw exception;
		}
		try {
			( ( HttpsURLConnection ) connection ).setInstanceFollowRedirects( true );
		} catch ( Exception exception ) {
			( ( HttpURLConnection ) connection ).setInstanceFollowRedirects( true );
		}
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");

		image = ImageIO.read( connection.getInputStream() );
		return image;
	}

	public static File saveImage( BufferedImage image, File saveFolder, String name ) {
		return saveImage( image, saveFolder, name, false );
	}

	public static File saveImage( BufferedImage image, File saveFolder, String name, boolean alpha ) {
		if ( image == null ) return null;
		int width = image.getWidth( null );
		int height = image.getHeight( null );

		byte[][] map = new byte[ width ][ height ];
		for ( int ix = 0; ix < width; ix++ ) {
			for ( int iz = 0; iz < height; iz++ ) {
				Color color = new Color( image.getRGB( ix, iz ), alpha );
				if ( alpha && color.getAlpha() != 255 ) {
					int bColor = 0;
					byte r = ( byte ) ( color.getRed() / 85 );
					byte g = ( byte ) ( color.getGreen() / 85 );
					byte b = ( byte ) ( color.getBlue() / 85 );
					int a = color.getAlpha();
					byte trans = ( byte ) 0;
					if ( a < 43 ) {
						map[ ix ][ iz ] = 0;
						continue;
					}
					else if ( a >= 43 && a <= 129 ) {
						a = 64;
					}
					else if ( a > 129 && a <= 215 ) {
						a = 172;
						trans = ( byte ) 1;
					}
					else if ( a > 215 ) {
						Color opaque = new Color( color.getRGB(), false );
						map[ ix ][ iz ] = MapPalette.matchColor( opaque );
						continue;
					}
					bColor = -( ( r << 5 ) + ( g << 3 ) + ( b << 1 ) + trans ) ;
					map[ ix ][ iz ] = ( byte ) bColor;
				} else {
					Color opaque = new Color( color.getRGB(), false );
					map[ ix ][ iz ] = MapPalette.matchColor( opaque );
				}
			}
		}

		try {
			File mf = new File( saveFolder, name + ".ser" );
			saveFolder.mkdirs();
			FileOutputStream fileOut = new FileOutputStream( mf );
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject( map );
			out.close();
			fileOut.close();
			return mf;
		} catch(IOException i) {
			i.printStackTrace();
		}
		return null;
	}

	public static BufferedImage toBufferedImage( Image img ) {
		if (img instanceof BufferedImage) return ( BufferedImage ) img;

		BufferedImage bimage = new BufferedImage( img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB );

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}

	public static Color getBestMapColor( Color color ) {
		Color best_color = BLACK;
		double best_distance = -1.0D;
		for (Color check_color : colors) {
			double distance = getDistance(color, check_color);
			if ((distance < best_distance) || (best_distance == -1.0D)) {
				best_distance = distance;
				best_color = check_color;
			}
		}
		return best_color;
	}

	private static double getDistance(Color color_1, Color color_2) {
		double red_avg = (color_1.getRed() + color_2.getRed()) / 2.0D;
		double r = color_1.getRed() - color_2.getRed();
		double g = color_1.getGreen() - color_2.getGreen();
		int b = color_1.getBlue() - color_2.getBlue();
		double weight_red = 2.0D + red_avg / 256.0D;
		double weight_green = 4.0D;
		double weight_blue = 2.0D + (255.0D - red_avg) / 256.0D;
		return weight_red * r * r + weight_green * g * g + weight_blue * b * b;
	}

	public static void dither(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] dither_buffer = new int[2][Math.max(width, height) * 3];

		int[] y_temps = { 0, 1, 1, 1 };
		for (int x = 0; x < width; x++) {
			dither_buffer[0] = dither_buffer[1];
			dither_buffer[1] = new int[Math.max(width, height) * 3];
			for (int y = 0; y < height; y++) {
				int rgb = image.getRGB(x, y);
				Color color = new Color(rgb);
				if (color.getAlpha() != 0) {
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();
					red = Math.max(0, Math.min(red + dither_buffer[0][(y * 3)], 255));
					green = Math.max(0, Math.min(green + dither_buffer[0][(y * 3 + 1)], 255));
					blue = Math.max(0, Math.min(blue + dither_buffer[0][(y * 3 + 2)], 255));
					Color matched_color = getBestMapColor(new Color(red, green, blue));
					int delta_r = red - matched_color.getRed();
					int delta_g = green - matched_color.getGreen();
					int delta_b = blue - matched_color.getBlue();
					int[] x_temps = { y + 1, y - 1, y, y + 1 };
					for (int i = 0; i < x_temps.length; i++) {
						int temp_y = y_temps[i];
						int temp_x = x_temps[i];
						if ((temp_y < height) && (temp_x < width) && (temp_x > 0)) {
							dither_buffer[temp_y][(temp_x * 3)] = ((int)(dither_buffer[temp_y][(temp_x * 3)] + color_multipliers[i] * delta_r));

							dither_buffer[temp_y][(temp_x * 3 + 1)] = ((int)(dither_buffer[temp_y][(temp_x * 3 + 1)] + color_multipliers[i] * delta_g));

							dither_buffer[temp_y][(temp_x * 3 + 2)] = ((int)(dither_buffer[temp_y][(temp_x * 3 + 2)] + color_multipliers[i] * delta_b));
						}
					}
					image.setRGB(x, y, matched_color.getRGB());
				} else {
					image.setRGB(x, y, 0);
				}
			}
		}
	}
	
	public static byte[][] convertImage( BufferedImage image, int width, int height, boolean dither ) {
		BufferedImage step2 = ImageUtil.toBufferedImage( image.getScaledInstance( width, height, Image.SCALE_SMOOTH ) );
		
		if ( dither ) {
			ImageUtil.dither( step2 );
		}
		
		byte[][] map = new byte[ width ][ height ];
		for ( int ix = 0; ix < width; ix++ ) {
			for ( int iz = 0; iz < height; iz++ ) {
				Color opaque = new Color( step2.getRGB( ix, iz ), false );
				map[ ix ][ iz ] = MapPalette.matchColor( opaque );
			}
		}
		
		return map;
	}
}
