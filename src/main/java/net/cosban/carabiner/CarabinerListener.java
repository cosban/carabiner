package net.cosban.carabiner;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;

public class CarabinerListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
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
		if (!CarabinerAPI.toIgnore(username)) {
			List<String> users = CarabinerAPI.listAltUsers(username);
			if (users.size() > 1) {
				String m = "";
				for (String s : users) {
					m += s.equals(username) ? "" : s + ", ";
				}
				Carabiner.logger.info("The following users are alts of " + username);
				Carabiner.logger.info(m.substring(0, m.lastIndexOf(",")));
			}
		}
	}
}
