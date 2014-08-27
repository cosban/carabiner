package net.cosban.carabiner.commands;

import net.cosban.carabiner.CarabinerAPI;
import net.cosban.utils.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CarabinerCommand extends Command {
	public Map<String, Method> params = new HashMap<>();

	@CommandBase(
			name = "carabiner",
			params = { "ignore" },
			description = "For plugin info and admin shit",
			aliases = { "iptrace", "alts" },
			permission = "alts.commands.carabiner")
	public CarabinerCommand(String name) {
		super(name);
	}

	public CarabinerCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public boolean base(CommandSender sender, String[] args) {
		sender.sendMessage(new TextComponent(ChatColor.RED + getSyntax()));
		return false;
	}

	public void execute(CommandSender sender, String[] args) {
		if (!getName().equals("carabiner") || args.length == 0) {
			base(sender, args);
		} else {
			switch (args[0].toLowerCase()) {
				case "ignore":
					setIgnore(sender, true, Arrays.copyOfRange(args, 1, args.length));
					break;
				case "track":
					setIgnore(sender, false, Arrays.copyOfRange(args, 1, args.length));
					break;
				default:
					sender.sendMessage(new TextComponent(ChatColor.GREEN + "This command is under construction"));
			}
		}
	}

	private void setIgnore(CommandSender sender, boolean ignore, String[] args) {
		if (args.length < 1 || args.length > 1) {
			sender.sendMessage(new TextComponent(getSyntax()));
		}
		CarabinerAPI.setIgnoreState(args[0], ignore);
		sender.sendMessage(new TextComponent(ChatColor.GREEN
				+ "Now "
				+ (ignore ? "IGNOR" : "TRACK")
				+ "ING alts of "
				+ args[0]));
	}

	public Map<String, Method> getParams() {
		return params;
	}

	public String getSyntax() {
		String p = "";
		for (String s : getParams().keySet()) {
			p += s + "|";
		}
		if (p.contains("|")) {
			p = p.substring(0, p.lastIndexOf("|"));
		}
		return "/" + getName() + " <" + p + ">";
	}
}
