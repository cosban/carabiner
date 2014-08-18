package net.cosban.carabiner;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class CarabinerAPI {

	/**
	 * @param username
	 * 		the username to lookup
	 *
	 * @return a list of direct alts that have used the same connection as the
	 * user
	 */
	public static List<String> listAlts(String username) {
		return listAlts(username, new ArrayList<String>(), 1);
	}

	/**
	 * Creates a list of alts based on connected accounts which may not be
	 * directly connected. If two alts (an original and a second) have joined
	 * using the same connection, they are said to have a direct connection, if
	 * a different alt connects to a connection that only the second alt has
	 * previously used, that would be one degree of separation from the
	 * original. //TODO: convert this to use UUID strings instead of usernames
	 *
	 * @param username
	 * 		the username to look up
	 * @param degrees
	 * 		the maximum number of degrees of separation to search
	 *
	 * @return a list of alts within the specified degrees of separation to find
	 */
	public static List<String> listAlts(String username,
			ArrayList<String> users, int degrees) {
		users.add(username);
		if (degrees > 0) {
			for (String ip : listConnections(username)) {
				for (String t : listUsers(ip)) {
					if (!users.contains(t)) {
						listAlts(t, users, degrees - 1);
					}
				}
			}
		}
		return users;
	}

	/**
	 * @param address
	 * 		the string ip address of an account
	 *
	 * @return a list of alts that have used this address
	 */
	public static List<String> listUsers(String address) {
		return Carabiner.getReader().getNamesFromAddress(address);
	}

	/**
	 * @param address
	 * 		the ip address of an account
	 *
	 * @return a list of alts that have used this address
	 */
	public static List<String> listUsers(InetAddress address) {
		return listUsers(address.getHostAddress());
	}

	/**
	 * Looks up every ip address used by
	 *
	 * @param username
	 * 		the username to lookup
	 *
	 * @return a list of ip addresses used by this specific user
	 */
	public static List<String> listConnections(String username) {
		return Carabiner.getReader().getAddressesFromName(username);
	}

	//	public static List<String> listAltsByInetAddress(String username) {
	//		return Carabiner.getReader().getAltIPs(username);
	//	}

	/**
	 * Adds a player after looking them up by name
	 *
	 * @param username
	 * 		the username to lookup
	 */
	public static void addEntry(String username) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(username);
		if (p != null) {
			addEntry(p);
		}
	}

	/**
	 * Adds a player to the database
	 *
	 * @param player
	 * 		the player to add to the db
	 */
	public static void addEntry(ProxiedPlayer player) {
		Carabiner.getWriter().queueNewAlt(player);
	}

	/**
	 * @param username
	 * 		the username to lookup
	 * @param address
	 * 		the ipv4 address to verify
	 *
	 * @return true if the user and ipv4 address are already entered into the
	 * database
	 */
	public static boolean containsUser(String username, String address) {
		return Carabiner.getReader().exists(username, address);
	}

	/**
	 * Sets whether to notify admins of alt accounts when this specific account
	 * logs in. If any alts of this account log in, it will still notify admins
	 * unless they are set to be ignored as well
	 *
	 * @param username
	 * 		the username of the player
	 * @param toIgnore
	 * 		whether to ignore them or not
	 */
	public static void setIgnoreState(String username, boolean toIgnore) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(username);
		if (p != null) {
			setIgnoreState(p, toIgnore);
		}
	}

	/**
	 * Sets whether to notify admins of alt accounts when this specific account
	 * logs in. If any alts of this account log in, it will still notify admins
	 * unless they are set to be ignored as well
	 *
	 * @param player
	 * 		the player
	 * @param toIgnore
	 * 		whether to ignore them or not
	 */
	public static void setIgnoreState(ProxiedPlayer player, boolean toIgnore) {
		Carabiner.getWriter().queueUpdateAlt(player, toIgnore);
	}

	/**
	 * @param username
	 * 		the username to look up
	 *
	 * @return true if the account is to be ignored
	 */
	public static boolean toIgnore(String username) {
		return Carabiner.getReader().getToIgnore(username);
	}

	/**
	 * @return true if the plugin is connected to a sql db
	 */
	public static boolean isConnected() {
		return Carabiner.isConnected();
	}

}
