package net.cosban.carabiner.commands;

import net.cosban.carabiner.Alt;
import net.cosban.carabiner.CarabinerAPI;
import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BanAlts extends CarabinerCommand {

	@CommandBase(
			name = "banalts",
			params = { "player [message]" },
			description = "Bans all current alts of an account",
			aliases = { "banalt", "ban-alt", "banalts" },
			permission = "alts.commands.banIP")
	public BanAlts(String name) {
		super(name);
	}

	public BanAlts(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
			return;
		}

		if (args.length == 1) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player != null) {
				for (Alt a : CarabinerAPI.listAlts(new Alt(player, false))) {
					SnipAPI.banAlts(a.getUsername(), a.getUUID().toString(), a.getAddress().getHostAddress(),
							"Banned as an alt of "
									+ player.getName(), sender.getName());
				}
			}
		} else if (args.length >= 2) {
			String message = "";
			for (int i = 1; i < args.length; i++)
				message = message + args[i] + " ";
			message = message.trim();
			SnipAPI.banAlts(args[0], message, sender.getName());
		}
	}
}
