package io.github.bananapuncher714.cartographer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.bananapuncher714.cartographer.message.CLogger;

public class BlankExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		CLogger.msg( arg0, "header", CLogger.parse( arg0, "main.name" ), CLogger.parse( arg0, "main.command.disabled-module" ) );
		return false;
	}

}
