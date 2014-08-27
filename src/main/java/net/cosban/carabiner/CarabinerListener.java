package net.cosban.carabiner;

import net.cosban.carabiner.events.CarabinerEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class CarabinerListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PostLoginEvent event) {
		Alt user = new Alt(event.getPlayer(), CarabinerAPI.toIgnore(event.getPlayer().getName()));
		if (CarabinerAPI.containsUser(user.getUsername(), user.getAddress())) {
			Carabiner.debug().debug(getClass(), user.getUsername() + " is already in the database.");
		} else {
			CarabinerAPI.addEntry(event.getPlayer());
			Carabiner.debug().debug(getClass(), user.getUsername() + " was added to the database.");
		}
		if (!CarabinerAPI.toIgnore(user.getUsername())) {
			ProxyServer.getInstance().getPluginManager().callEvent(new CarabinerEvent(user.getUsername(),
					CarabinerAPI.listAlts(user)));
		}
	}

	@EventHandler
	public void onCarabinerEvent(CarabinerEvent event) {
		if (event.getAlts().size() > 1) {
			String m = "";
			for (Alt alt : event.getAlts()) {
				if (alt.isBanned()) {
					m += ChatColor.RED + alt.getUsername() + ChatColor.WHITE + ", ";
				} else {
					m += ChatColor.GREEN + alt.getUsername() + ChatColor.WHITE + ", ";
				}
			}
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.hasPermission("alts.notify")) {
					player.sendMessage(new TextComponent(ChatColor.GREEN
							+ "The following users are alts of "
							+ event.getPlayerName()));
					player.sendMessage(new TextComponent(ChatColor.GREEN + m.substring(0, m.lastIndexOf(","))));
				}
			}
		}
	}
}
