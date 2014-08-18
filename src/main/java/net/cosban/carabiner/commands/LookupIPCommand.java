package net.cosban.carabiner.commands;

import net.cosban.carabiner.CarabinerAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class LookupIPCommand extends CarabinerCommand {
	@CommandBase(
			name = "lookupip",
			params = { "player" },
			description = "For looking up user IPs",
			aliases = { "altip", "ip", "iplookup" },
			permission = "alts.commands.lookup")
	public LookupIPCommand(String name) {
		super(name);
	}

	public LookupIPCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1 || args.length > 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
		} else if (args.length == 1) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player != null) {
				sender.sendMessage(new TextComponent(ChatColor.GREEN
						+ player.getName()
						+ " is online and connected with: "
						+ player.getAddress().getAddress().getHostAddress()));
			}
			List<String> ip = CarabinerAPI.listConnections(args[0]);
			if (ip.size() > 1) {
				String m = "";
				for (String s : ip) {
					m += s + ", ";
				}
				sender.sendMessage(new TextComponent(ChatColor.GREEN
						+ "The following addresses have been used by "
						+ ChatColor.GOLD
						+ args[0]));
				sender.sendMessage(new TextComponent(ChatColor.GOLD
						+ m.substring(m.lastIndexOf(","), m.length())));
			} else {
				sender.sendMessage(new TextComponent(ChatColor.GREEN
						+ "No addresses were found for "
						+ ChatColor.GOLD
						+ args[0]));
			}
		}
	}
}
