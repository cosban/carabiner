package net.cosban.carabiner.commands;

import net.cosban.carabiner.CarabinerAPI;
import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class BanAlts extends CarabinerCommand {

	@CommandBase(
			name = "ban-alts",
			params = { "player [message]" },
			description = "Bans all current alts of an account",
			aliases = { "banalt", "ban-alt", "banalts" },
			permission = "alts.commands.ban")
	public BanAlts(String name) {
		super(name);
	}

	public BanAlts(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
		} else if (args.length == 1) {
			for (String username : CarabinerAPI.listAltUsers(args[0])) {
				SnipAPI.ban(username, "Alt of" + args[0], sender);
			}
			for (String ip : CarabinerAPI.listConnections(args[0])) {
				SnipAPI.ban(ip, "Address used by banned user" + args[0], sender);
			}
		} else if (args.length >= 2) {
			String message = "";
			for (int i = 1; i < args.length; i++)
				message = message + args[i] + " ";
			message = message.trim();
			for (String username : CarabinerAPI.listAltUsers(args[0])) {
				SnipAPI.ban(username, message, sender);
			}
			for (String ip : CarabinerAPI.listConnections(args[0])) {
				SnipAPI.ban(ip, message, sender);
			}
		}
	}
}
