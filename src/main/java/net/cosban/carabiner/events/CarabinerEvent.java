package net.cosban.carabiner.events;

import net.cosban.carabiner.Carabiner;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;

public class CarabinerEvent extends Event {
	private Carabiner    plugin;
	private String       playerName;
	private List<String> alts;

	public CarabinerEvent(Carabiner plugin, String playerName, List<String> alts) {
		this.plugin = plugin;
		this.playerName = playerName;
		this.alts = alts;
	}

	public Carabiner getPlugin() {
		return plugin;
	}

	public String getPlayerName() {
		return playerName;
	}

	public List<String> getAlts() {
		return alts;
	}

	public String getAltsArray() {
		return alts.toString();
	}
}
