package net.cosban.carabiner.events;

import net.cosban.carabiner.Alt;
import net.md_5.bungee.api.plugin.Event;

import java.util.Collection;
import java.util.List;

public class CarabinerEvent extends Event {
	private String    playerName;
	private Collection<Alt> alts;

	public CarabinerEvent(String playerName, Collection<Alt> alts) {
		this.playerName = playerName;
		this.alts = alts;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Collection<Alt> getAlts() {
		return alts;
	}

	public Alt[] getAltsArray() {
		return (Alt[]) alts.toArray();
	}
}
