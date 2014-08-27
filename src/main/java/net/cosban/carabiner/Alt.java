package net.cosban.carabiner;

import net.cosban.snip.api.SnipAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class Alt {
	private String      username;
	private InetAddress address;
	private UUID        uuid;
	private boolean     toIgnore;
	private boolean     isBanned;

	public Alt(String username, String address, String uuid, boolean toIgnore) {
		this.username = username;
		try {
			this.address = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			Carabiner.debug().debug(getClass(), "Seems that we recorded an IP address incorrectly");
			Carabiner.debug().debug(getClass(), e);
			this.address = InetAddress.getLoopbackAddress();
		}
		this.uuid = UUID.fromString(uuid);
		this.toIgnore = toIgnore;
		this.isBanned = (SnipAPI.isBanned(this.uuid) || SnipAPI.isBanned(this.address));
	}

	public Alt(String username, InetAddress address, UUID uuid, boolean toIgnore) {
		this.username = username;
		this.address = address;
		this.uuid = uuid;
		this.toIgnore = toIgnore;
		this.isBanned = (SnipAPI.isBanned(uuid) || SnipAPI.isBanned(address));
	}

	public Alt(ProxiedPlayer p, boolean toIgnore){
		this.username = p.getName();
		this.address = p.getAddress().getAddress();
		this.uuid = p.getUniqueId();
		this.toIgnore = toIgnore;
		this.isBanned = (SnipAPI.isBanned(uuid) || SnipAPI.isBanned(address));
	}

	public String getUsername() {
		return username;
	}

	public InetAddress getAddress() {
		return address;
	}

	public UUID getUUID() {
		return uuid;
	}

	public boolean toIgnore() {
		return toIgnore;
	}

	public boolean isBanned() {
		return isBanned;
	}
}
