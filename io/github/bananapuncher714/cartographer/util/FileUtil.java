package io.github.bananapuncher714.cartographer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class FileUtil {
	static final int BUFFER_SIZE = 8192;
	
	public static void saveToFile( InputStream stream, File output, boolean force ) {
		if ( force && output.exists() ) output.delete();
		if ( !output.exists() ) {
			output.getParentFile().mkdirs();
			try {
				byte[] buffer = new byte[ stream.available() ];
			 
			    OutputStream outStream = new FileOutputStream( output );
			    int len;
			    while ( ( len = stream.read( buffer ) ) > 0)  {
		          outStream.write( buffer, 0, len );
		        }
		        stream.close();
			    outStream.close();
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		}
	}
	
	public static byte[][] loadFile( File file ) {
		if ( !file.exists() ) {
			return null;
		}
		try {
            FileInputStream fileIn = new FileInputStream( file );
            ObjectInputStream in = new ObjectInputStream( fileIn );
            byte[][] map = ( byte[][] ) in.readObject();
            in.close();
            fileIn.close();
            return map;
        } catch( IOException i ) {
        	i.printStackTrace();
        } catch( ClassNotFoundException c ) {
        	c.printStackTrace();
        }
		return null;
	}
	
	public static boolean saveByteArray( byte[][] map, File file ) {
		file.getParentFile().mkdirs();
		try {
            FileOutputStream fileOut = new FileOutputStream( file );
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject( map );
            out.close();
            fileOut.close();
            return true;
		} catch(IOException i) {
			return false;
		}
	}
	
	public static void updateConfigFromFile( File toUpdate, InputStream toCopy ) {
		updateConfigFromFile( toUpdate, toCopy, false );
	}
	
	public static void updateConfigFromFile( File toUpdate, InputStream toCopy, boolean trim ) {
		FileConfiguration config = YamlConfiguration.loadConfiguration( new InputStreamReader( toCopy ) );
		FileConfiguration old = YamlConfiguration.loadConfiguration( toUpdate );
		
		for ( String key : config.getKeys( true ) ) {
			if ( !old.contains( key ) ) {
				old.set( key, config.get( key ) );
			}
		}
		
		if ( trim ) {
			for ( String key : old.getKeys( true ) ) {
				if ( !config.contains( key ) ) {
					old.set( key, null );
				}
			}
		}
		
		try {
			old.save( toUpdate );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	public static boolean move( File original, File dest, boolean force ) {
		if ( dest.exists() ) {
			if ( !force ) {
				return false;
			} else {
				recursiveDelete( dest );
			}
		}
		dest.getParentFile().mkdirs();
		original.renameTo( dest );
		recursiveDelete( original );
		return true;
	}
	
	public static void recursiveDelete( File file ) {
		if ( !file.exists() ) {
			return;
		}
		if ( file.isDirectory() ) {
			for ( File lower : file.listFiles() ) {
				recursiveDelete( lower );
			}
		}
		file.delete();
	}
}
