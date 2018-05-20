package io.github.bananapuncher714.cartographer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.dependencies.ClipsPlaceholder;
import io.github.bananapuncher714.cartographer.dependencies.MvDWPlaceholder;
import io.github.bananapuncher714.cartographer.dependencies.WorldBorderAPI;

public final class DependencyManager {
	private static final boolean placeholderAPI = Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null;
	private static final boolean mvdwPlaceholderAPI = Bukkit.getPluginManager().getPlugin( "MVdWPlaceholderAPI" ) != null;
	private static final boolean kingdoms = Bukkit.getPluginManager().getPlugin( "Kingdoms" ) != null;
	private static final boolean factionsuuid = Bukkit.getPluginManager().getPlugin( "Factions" ) != null;
	private static final boolean towny = Bukkit.getPluginManager().getPlugin( "Towny" ) != null;
	private static final boolean worldguard = Bukkit.getPluginManager().getPlugin( "WorldGuard" ) != null;
	private static final boolean citizens = Bukkit.getPluginManager().getPlugin( "Citizens" ) != null;
	private static final boolean quests = Bukkit.getPluginManager().getPlugin( "Quests" ) != null;
	private static final boolean worldborderAPI = Bukkit.getPluginManager().getPlugin( "WorldBorder" ) != null;
	private static final boolean gangsPlus = Bukkit.getPluginManager().getPlugin( "GangsPlus" ) != null;
	private static final boolean griefPrevention = Bukkit.getPluginManager().getPlugin( "GriefPrevention" ) != null;
	private static final boolean mythicmobs = Bukkit.getPluginManager().getPlugin( "MythicMobs" ) != null;
	
	public static String parse( Player player, String input ) {
		String result = input;
		if ( placeholderAPI ) result = ClipsPlaceholder.parse( player, result );
		if ( mvdwPlaceholderAPI ) result = MvDWPlaceholder.parse( player, result );
		return result;
	}
	
	public static boolean doesChunkExist( World world, int x, int z ) {
		return worldborderAPI ? WorldBorderAPI.isLoaded( world, x, z ) : true;
	}
	
	public static boolean isKingdomsEnabled() {
		return kingdoms;
	}
	
	public static boolean isFactionsUUIDEnabled() {
		return factionsuuid;
	}
	
	public static boolean isTownyEnabled() {
		return towny;
	}
	
	public static boolean isWorldGuardEnabled() {
		return worldguard;
	}
	
	public static boolean isCitizensEnabled() {
		return citizens;
	}
	
	public static boolean isQuestsEnabled() {
		return quests;
	}
	
	public static boolean isWorldBorderAPIEnabled() {
		return worldborderAPI;
	}
	
	public static boolean isGangsPlusEnabled() {
		return gangsPlus;
	}
	
	public static boolean isGriefPreventionEnabled() {
		return griefPrevention;
	}
	
	public static boolean isMythicMobsEnabled() {
		return mythicmobs;
	}
}
