package net.cosban.carabiner.commands;

import net.cosban.carabiner.CarabinerAPI;
import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class KickAlts extends CarabinerCommand {

	@CommandBase(
			name = "kick-alts",
			params = { },
			description = "Kicks all current alts of an account",
			aliases = { "kickalt" },
			permission = "alts.commands.kick")
	public KickAlts(String name) {
		super(name);
	}

	public KickAlts(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
		} else if (args.length == 1) {
			for (String username : CarabinerAPI.listAlts(args[0])) {
				SnipAPI.kickPlayer(username, "Alt of" + args[0], sender);
			}
			for (String ip : CarabinerAPI.listConnections(args[0])) {
				SnipAPI.kickPlayer(ip, "Alt of" + args[0], sender);
			}
		} else if (args.length >= 2) {
			String message = "";
			for (int i = 1; i < args.length; i++)
				message = message + args[i] + " ";
			message = message.trim();
			for (String username : CarabinerAPI.listAlts(args[0])) {
				SnipAPI.kickPlayer(username, message, sender);
			}
			for (String ip : CarabinerAPI.listConnections(args[0])) {
				SnipAPI.kickPlayer(ip, message, sender);
			}
		}
	}

}
