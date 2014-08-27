package net.cosban.carabiner.commands;

import net.cosban.carabiner.Alt;
import net.cosban.carabiner.CarabinerAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collection;
import java.util.List;

public class LookupCommand extends CarabinerCommand {

	@CommandBase(
			name = "lookup",
			params = { "player" },
			description = "For looking up alts",
			aliases = { "alt" },
			permission = "alts.commands.lookup")
	public LookupCommand(String name) {
		super(name);
	}

	public LookupCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1 || args.length > 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
		} else if (args.length == 1) {
			Collection<Alt> users = CarabinerAPI.listAlts(args[0]);
			if (users.size() > 1) {
				String m = "";
				for (Alt a : users) {
					if (a.isBanned()) {
						m += ChatColor.RED + a.getUsername() + ChatColor.WHITE + ", ";
					} else {
						m += ChatColor.GREEN + a.getUsername() + ChatColor.WHITE + ", ";
					}
				}
				if (m.contains(",")) {
					m = m.substring(0, m.lastIndexOf(","));
				}
				sender.sendMessage(new TextComponent(ChatColor.GREEN
						+ "The following users are alts of "
						+ ChatColor.GOLD
						+ args[0]));
				sender.sendMessage(new TextComponent(ChatColor.GOLD + m));
			} else {
				sender.sendMessage(new TextComponent(ChatColor.GREEN
						+ "No alts found for "
						+ ChatColor.GOLD
						+ args[0]));
			}
		}
	}
}
