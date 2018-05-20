package io.github.bananapuncher714.cartographer.util.command;

import java.util.Map;

public class OptionParser {
	
	public static Map< String, String > parseArguments( Map< String, String > options, Map< String, String > output, int startIndex, boolean caseSensitive, String... arguments ) {
		boolean isOption = false;
		String option = "";
		for ( int index = startIndex; index < arguments.length; index++ ) {
			String arg = arguments[ index ];
			if ( !caseSensitive && !isOption ) {
				arg = arg.toLowerCase();
			}
			if ( options.containsKey( arg ) ) {
				isOption = true;
				option = arg;
			} else if ( isOption ) {
				output.put( options.get( option ), arg );
				isOption = false;
			}
		}
		return output;
	}
}
