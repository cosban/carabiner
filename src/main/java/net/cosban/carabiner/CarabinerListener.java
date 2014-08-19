package net.cosban.carabiner;

import net.cosban.carabiner.events.CarabinerEvent;
import net.cosban.snip.events.PlayerJoinEvent;
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
		ProxiedPlayer player = event.getPlayer();
		String username = player.getName();
		String address = player.getAddress().getAddress().getHostAddress();
		if (CarabinerAPI.containsUser(username, address)) {
			Carabiner.debug().debug(getClass(), player.getName() + " is already in the database.");
		} else {
			CarabinerAPI.addEntry(player);
			Carabiner.debug().debug(getClass(), player.getName() + " was added to the database.");
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		ProxiedPlayer player = event.getPlayer();
		String username = player.getName();
		if (!CarabinerAPI.toIgnore(username) && event.getPlayer().equals(PlayerJoinEvent.Result.ALLOWED)) {
			ProxyServer.getInstance().getPluginManager().callEvent(new CarabinerEvent(username,
					CarabinerAPI.listAlts(username)));
		}
	}

	@EventHandler
	public void onCarabinerEvent(CarabinerEvent event) {
		if (event.getAlts().size() > 1) {
			String m = "";
			for (String s : event.getAlts()) {
				m += s.equals(event.getPlayerName()) ? "" : s + ", ";
			}
			Carabiner.logger.info("The following users are alts of " + event.getPlayerName());
			Carabiner.logger.info(m.substring(0, m.lastIndexOf(",")));
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
