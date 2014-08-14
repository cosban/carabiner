package net.cosban.carabiner;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.util.List;

public class CarabinerAPI {
	public static List<String> listAltUsers(String username) {
		return Carabiner.getReader().getAltNames(Carabiner.getReader().getAltID(username));
	}

	public static List<String> listUsers(String address) {
		return Carabiner.getReader().getAltNames(address);
	}

	public static List<String> listUsers(InetAddress address) {
		return listUsers(address.getHostAddress());
	}

	/**
	 * Looks up every ip address used by
	 *
	 * @param username the username to lookup
	 * @return a list of ip addresses used by this specific user
	 */
	public static List<String> listConnections(String username) {
		return Carabiner.getReader().getAltIPs(username);
	}

	//	public static List<String> listAltsByInetAddress(String username) {
	//		return Carabiner.getReader().getAltIPs(username);
	//	}

	public static void addEntry(String username, String address) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(username);
		if (p != null) {
			Carabiner.getWriter().queueNewAlt(p, 1, true);
		}
	}

	public static boolean containsUser(String username, String address) {
		return Carabiner.getReader().entryExists(username, address);
	}

	public static boolean toIgnore(String username) {
		return Carabiner.getReader().getTracking(username).contains(false);
	}

	public static void addEntry(ProxiedPlayer p) {
		Carabiner.getWriter().queueNewAlt(p, 1, true);
	}

	public static boolean isConnected() {
		return Carabiner.isConnected();
	}

}
