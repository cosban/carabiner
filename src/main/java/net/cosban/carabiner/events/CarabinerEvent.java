package net.cosban.carabiner.events;

import net.md_5.bungee.api.plugin.Event;

import java.util.List;

public class CarabinerEvent extends Event {
	private String       playerName;
	private List<String> alts;

	public CarabinerEvent(String playerName, List<String> alts) {
		this.playerName = playerName;
		this.alts = alts;
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
