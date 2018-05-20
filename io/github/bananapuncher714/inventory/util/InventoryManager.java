package io.github.bananapuncher714.inventory.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.inventory.components.InventoryComponent;
import io.github.bananapuncher714.inventory.components.InventoryPanel;
import io.github.bananapuncher714.inventory.panes.ContentPane;

public class InventoryManager {
	
	public static void orderComponents( int rows, ArrayList< InventoryComponent > items ) {
		ArrayList< InventoryPanel > organize = new ArrayList< InventoryPanel >();
		for ( InventoryComponent ic : items ) {
			// Only add the components that are resizable
			if ( ic instanceof InventoryPanel ) {
				InventoryPanel resizablec = ( InventoryPanel ) ic;
				organize.add( resizablec );
			}
		}
		// Here starts the 4 loops to stretch in the 4 directions
		for ( int stretch = 1; stretch < 5; stretch ++ ) {
			ArrayList< InventoryPanel > remList = new ArrayList< InventoryPanel >( organize );
			for ( Iterator< InventoryPanel > it = remList.iterator(); it.hasNext(); ) {
				InventoryPanel ip = it.next();
				// Break if the component is not Xpandable in a given direction
				switch( stretch ) {
				case 1: if ( !ip.expandX() ) it.remove(); break;
				case 2: if ( !ip.expandY() ) it.remove(); break;
				case 3: if ( !ip.expandX() ) it.remove(); break;
				case 4: if ( !ip.expandY() ) it.remove(); break;
				default: break;
				}
			}
			while ( !remList.isEmpty() ) {
				for ( Iterator< InventoryPanel > it = remList.iterator(); it.hasNext(); ) {
					InventoryPanel rc = it.next();
					int w = 0, h = 0, a = 0, b = 0;
					switch( stretch ) {
					case 1: w = 1; break;
					case 2: h = 1; break;
					case 3: w = 1; a = -1; break;
					case 4: h = 1; b = -1; break;
					default: break;
					}
					int[] rcxy = slotToCoord( rc.getSlot(), 9 );
					boolean overlap = false, same = true;
					for ( InventoryComponent ic : items ) {
						int[] icxy = slotToCoord( ic.getSlot(), 9 );
						if ( ic == rc ) continue;
						same = false;
						if ( overlap( rcxy[ 0 ] + a, rcxy[ 1 ] + b, rc.getWidth() + w, rc.getHeight() + h, icxy[ 0 ], icxy[ 1 ], ic.getWidth(), ic.getHeight() ) ) {
							overlap = true;
							break;
						}
					}
					if ( !overlap && !same ) {
						rcxy[ 0 ] = rcxy[ 0 ] + a;
						rcxy[ 1 ] = rcxy[ 1 ] + b;
						int width = rc.getWidth() + w;
						int height = rc.getHeight() + h;
						if ( rcxy[ 0 ] > -1 && rcxy[ 0 ] + width < 10 && rcxy[ 1 ] > -1 && rcxy[ 1 ] + height - 1 < rows ) {
							rc.setSlot( coordToSlot( rcxy[ 0 ], rcxy[ 1 ], 9, rows ) );
							rc.setWidth( width);
							rc.setHeight( height );
						} else {
							it.remove();
						}
					} else if ( same ) {
						rc.setSlot( 0 );
						rc.setWidth( 9 );
						rc.setHeight( rows );
						it.remove();
						return;
					} else {
						it.remove();
					}
				}
			}
		}
	}
	
	public static void organizePanes( InventoryPanel panel, ArrayList< ContentPane > panes ) {
		for ( int stretch = 1; stretch < 5; stretch ++ ) {
			ArrayList< ContentPane > remList = new ArrayList< ContentPane >( panes );
			while ( !remList.isEmpty() ) {
				for ( Iterator< ContentPane > it = remList.iterator(); it.hasNext(); ) {
					ContentPane rc = it.next();
					if ( rc.isHidden() ) {
						it.remove();
						continue;
					}
					int w = 0, h = 0, a = 0, b = 0;
					switch( stretch ) {
					case 1: w = 1; break;
					case 2: h = 1; break;
					case 3: w = 1; a = -1; break;
					case 4: h = 1; b = -1; break;
					default: break;
					}
					int[] rcxy = slotToCoord( rc.getSlot(), panel.getWidth() );
					boolean overlap = false, same = true;
					for ( ContentPane ic : panes ) {
						if ( ic.isHidden() ) continue; 
						int[] icxy = slotToCoord( ic.getSlot(), panel.getWidth() );
						if ( ic == rc ) continue;
						same = false;
						if ( overlap( rcxy[ 0 ] + a, rcxy[ 1 ] + b, rc.getWidth() + w, rc.getHeight() + h, icxy[ 0 ], icxy[ 1 ], ic.getWidth(), ic.getHeight() ) ) {
							overlap = true;
							break;
						}
					}
					if ( !overlap && !same ) {
						rcxy[ 0 ] = rcxy[ 0 ] + a;
						rcxy[ 1 ] = rcxy[ 1 ] + b;
						int width = rc.getWidth() + w;
						int height = rc.getHeight() + h;
						if ( rcxy[ 0 ] > -1 && rcxy[ 0 ] + width - 1 < panel.getWidth() && rcxy[ 1 ] > -1 && rcxy[ 1 ] + height - 1 < panel.getHeight() ) {
							rc.setSlot( coordToSlot( rcxy[ 0 ], rcxy[ 1 ], panel.getWidth(), panel.getHeight() ) );
							rc.setSize( width, height );
						} else {
							it.remove();
						}
					} else if ( same ) {
						rc.setSlot( 0 );
						rc.setSize( panel.getWidth(), panel.getHeight() );
						it.remove();
						return;
					} else {
						it.remove();
					}
				}
			}
		}
	}
	
	public static boolean overlap( int x, int y, int w, int h, int a, int b, int i, int e  ) {
		return x < a + i && x + w > a && y < b + e && y + h > b;
	}
	
	public static boolean overlap( int a, int ax, int ay, int b, int bx, int by, int r ) {
		int[] axy = slotToCoord( a, r );
		int[] bxy = slotToCoord( b, r );
		return overlap( axy[ 0 ], axy[ 1 ], ax, ay, bxy[ 0 ], bxy[ 1 ], bx, by );
	}
	
	public static boolean overlap( int p, int x, int y, int a, int q ) {
		int[] pxy = slotToCoord( p, q );
		int[] axy = slotToCoord( a, q );
		return axy[ 0 ] >= pxy[ 0 ] && axy[ 0 ] <= pxy[ 0 ] + x - 1 && axy[ 1 ] >= pxy[ 1 ] && axy[ 1 ] <= pxy[ 1 ] + y - 1;
	}
	
	public static int[] slotToCoord( int slot, int width ) {
		int x = slot % width;
		int y = ( slot - x ) / width;
		return new int[] { x, y };
	}
	
	
	// If the coordinate is out of bounds, it returns -1!!
	public static int coordToSlot( int x, int y, int width, int height ) {
		if ( x > width || x < 0 || y > height || y < 0 ) return -1;
		return y * width + x;
	}
	
	public static ArrayList< ItemStack > combine( List< ItemStack > items ) {
		ArrayList< ItemStack > sorted = new ArrayList< ItemStack >();
		for ( ItemStack item : items ) {
			if ( item == null ) continue;
			boolean putInPlace = false;
			for ( ItemStack sitem : sorted ) {
				if ( item.isSimilar( sitem ) ) {
					if ( item.getAmount() + sitem.getAmount() < sitem.getMaxStackSize() ) {
						sitem.setAmount( sitem.getAmount() + item.getAmount() );
						putInPlace = true;
					} else {
						item.setAmount( item.getAmount() - ( sitem.getMaxStackSize() - sitem.getAmount() ) );
						sitem.setAmount( sitem.getMaxStackSize() );
					}
					break;
				}
			}
			if ( !putInPlace ) {
				sorted.add( item );
			}
		}
		return sorted;
	}

}
