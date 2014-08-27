package net.cosban.carabiner.commands;

import net.cosban.snip.api.SnipAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class WhoIsCommand extends CarabinerCommand {

	@CommandBase(
			name = "whois",
			params = { "player" },
			description = "Gives the latest player info",
			aliases = { "whowas" },
			permission = "alts.commands.lookup")
	public WhoIsCommand(String name) {
		super(name);
	}

	public WhoIsCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1 || args.length > 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
		} else if (args.length == 1) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player != null) {
				sender.sendMessage(new TextComponent(ChatColor.GOLD + player.getName() + " IS ONLINE"));
				sender.sendMessage(new TextComponent(ChatColor.GOLD
						+ "IP Address    : "
						+ player.getAddress().getAddress().getHostAddress()));
				sender.sendMessage(new TextComponent(ChatColor.GOLD
						+ "Current Server: "
						+ player.getServer().getInfo().getName()));
				sender.sendMessage(new TextComponent(ChatColor.GOLD
						+ "UUID          : "
						+ player.getUniqueId().toString()));
				sender.sendMessage(new TextComponent(ChatColor.GOLD + "Current Ping  : " + player.getPing()));
				sender.sendMessage(new TextComponent(ChatColor.GOLD + "Sorry... this command sucks ass right now "));
			} else {
				sender.sendMessage(new TextComponent(ChatColor.GOLD
						+ args[0]
						+ " IS OFFLINE (or has never logged in)"));
				sender.sendMessage(new TextComponent(ChatColor.GOLD + "Ban Status    : " + SnipAPI.isBanned(args[0])));
				sender.sendMessage(new TextComponent(ChatColor.GOLD + "Sorry... this command sucks ass right now "));
			}
		}
	}
}
