package net.cosban.carabiner;

import net.cosban.carabiner.events.CarabinerEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.TimeUnit;

public class CarabinerListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		String username = player.getName();
		String address = player.getAddress().getAddress().getHostAddress();
		if (CarabinerAPI.containsUser(username, address)) {
			Carabiner.debug().debug(getClass(), player.getName()
					+ " is already in the database.");
		} else {
			CarabinerAPI.addEntry(player);
			Carabiner.debug().debug(getClass(), player.getName()
					+ " was added to the database.");
		}
		ProxyServer.getInstance().getScheduler().schedule(Carabiner.getInstance(), new DelayedEvent(new CarabinerEvent(username, CarabinerAPI.listAlts(username))), 2, TimeUnit.SECONDS);
	}

	@EventHandler
	public void onCarabinerEvent(CarabinerEvent event) {
		if (event.getAlts().size() > 1) {
			String m = "";
			for (String s : event.getAlts()) {
				m += s.equals(event.getPlayerName()) ? "" : s + ", ";
			}
			Carabiner.logger.info("The following users are alts of "
					+ event.getPlayerName());
			Carabiner.logger.info(m.substring(0, m.lastIndexOf(",")));
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.hasPermission("alts.notify")) {
					player.sendMessage(new TextComponent(ChatColor.GREEN
							+ "The following users are alts of "
							+ event.getPlayerName()));
					player.sendMessage(new TextComponent(ChatColor.GREEN
							+ m.substring(0, m.lastIndexOf(","))));
				}
			}
		}
	}

	private class DelayedEvent implements Runnable {
		private Event event;

		private DelayedEvent(Event event) {
			this.event = event;
		}

		public void run() {
			ProxyServer.getInstance().getPluginManager().callEvent(event);
		}
	}
}
