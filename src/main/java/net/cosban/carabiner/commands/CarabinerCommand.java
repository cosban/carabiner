package net.cosban.carabiner.commands;

import net.cosban.utils.commands.CommandBase;
import net.cosban.utils.commands.ParameterBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CarabinerCommand extends Command {
	public Map<String, Method> params = new HashMap<>();

	@CommandBase(
			name = "carabiner",
			params = { "ignore" },
			description = "For plugin info and admin shit",
			aliases = { "iptrace" },
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
			switch (args[0]) {
				case "ignore":
				default:
					sender.sendMessage(new TextComponent(ChatColor.GREEN + "This command is under construction"));
			}
		}
	}

	public boolean handlePerms(CommandSender sender, Command command) {
		if (!checkPerms(sender, command)) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command!"));
		}
		return checkPerms(sender, command);
	}

	public boolean handlePerms(CommandSender sender, String name) {
		return handlePerms(sender, this);
	}

	public boolean checkPerms(CommandSender sender, Command command) {
		return sender.hasPermission(command.getPermission());
	}

	public void registerParams() {
		for (Method method : getClass().getMethods())
			if (method.isAnnotationPresent(ParameterBase.class)) {
				ParameterBase annote = (ParameterBase) method.getAnnotation(ParameterBase.class);
				if (getParams().get(annote.name()) == null) getParams().put(annote.name(), method);
			}
	}

	public Map<String, Method> getParams() {
		return params;
	}

	public ParameterBase getParameterBase(String s) {
		return (ParameterBase) ((Method) getParams().get(s)).getAnnotation(ParameterBase.class);
	}

	public List<Character> getFlags(String[] args) {
		List<Character> ch = new ArrayList<>();
		for (String s : args) {
			if (s.startsWith("-")) {
				for (char c : s.toCharArray()) {
					if (!ch.contains(c)) ch.add(c);
				}
			}
		}
		return ch;
	}

	public List<Character> getFlags(String args) {
		List<Character> ch = new ArrayList<>();
		if (args.startsWith("-")) {
			for (char c : args.toCharArray()) {
				if (!ch.contains(c)) ch.add(c);
			}
			return ch;
		}
		return null;
	}

	public int getInt(String[] args) {
		Pattern reg = Pattern.compile("^[0-9]+$");
		for (String s : args)
			if (reg.matcher(s).matches()) return Integer.parseInt(s);
		return -1;
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
