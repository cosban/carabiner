package net.cosban.carabiner;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.util.*;

public class CarabinerAPI {

	/**
	 * @param username
	 * 		the username to lookup
	 *
	 * @return a list of direct alts that have used the same connection as the user
	 */
	public static Collection<Alt> listAlts(String username) {
		return listAlts(username, Carabiner.getConfig().getDegrees());
	}

	/**
	 * Creates a list of alts based on connected accounts which may not be directly connected. If two alts (an original
	 * and a second) have joined using the same connection, they are said to have a direct connection, if a different
	 * alt connects to a connection that only the second alt has previously used,
	 * that would be one degree of separation
	 * from the original.
	 *
	 * @param username
	 * 		the username to look up
	 * @param degrees
	 * 		the maximum number of degrees of separation to search
	 *
	 * @return a list of alts within the specified degrees of separation to find
	 */
	public static Collection<Alt> listAlts(String username, int degrees) {
		if (checkConnection()) {
			ArrayList<Alt> alts = Carabiner.getReader().getAlts(username);
			if (alts.size() > 0) {
				return listAlts(alts.get(0), new HashMap<String,Alt>(), degrees);
			}
		}
		return new ArrayList<>();
	}

	/**
	 * @param user
	 * 		the account to lookup
	 *
	 * @return a list of direct alts that have used the same connection as the user
	 */
	public static Collection<Alt> listAlts(Alt user) {
		return listAlts(user, new HashMap<String,Alt>(), Carabiner.getConfig().getDegrees());
	}

	/**
	 * Creates a list of alts based on connected accounts which may not be directly connected. If two alts (an original
	 * and a second) have joined using the same connection, they are said to have a direct connection, if a different
	 * alt connects to a connection that only the second alt has previously used,
	 * that would be one degree of separation
	 * from the original.
	 *
	 * @param user
	 * 		the account to look up
	 * @param degrees
	 * 		the maximum number of degrees of separation to search
	 *
	 * @return a list of alts within the specified degrees of separation to find
	 */
	private static Collection<Alt> listAlts(Alt user, HashMap<String,Alt> alts, int degrees) {
		if (checkConnection()) {
			alts.put(user.getUsername(),user);
			if (degrees > 0) {
				for (InetAddress ip : listConnections(user.getUUID())) {
					for (Alt alt : Carabiner.getReader().getAlts(ip)) {
						if (!alts.containsKey(alt.getUsername())) {
							listAlts(alt, alts, degrees - 1);
						}
					}
				}
			}
			return alts.values();
		}
		return new ArrayList<>();
	}

	/**
	 * Creates a list of alts based on connected accounts which may not be directly connected. If two alts (an original
	 * and a second) have joined using the same connection, they are said to have a direct connection, if a different
	 * alt connects to a connection that only the second alt has previously used,
	 * that would be one degree of separation
	 * from the original.
	 *
	 * @param uuid
	 * 		the uuid to look up
	 * @param degrees
	 * 		the maximum number of degrees of separation to search
	 *
	 * @return a list of alts within the specified degrees of separation to find
	 */
	public static List<UUID> listAlts(UUID uuid, ArrayList<UUID> ids, int degrees) {
		if (checkConnection()) {
			ids.add(uuid);
			if (degrees > 0) {
				for (InetAddress ip : listConnections(uuid)) {
					for (UUID u : listUUIDS(ip)) {
						if (!ids.contains(u)) {
							listAlts(u, ids, degrees - 1);
						}
					}
				}
			}
			return ids;
		}
		return new ArrayList<>();
	}

	/**
	 * @param address
	 * 		the ip address of an account
	 *
	 * @return a list of alts that have used this address
	 */
	public static List<String> listNames(InetAddress address) {
		ArrayList<String> names = new ArrayList<>();
		if (checkConnection()) {
			for (Alt a : Carabiner.getReader().getAlts(address)) {
				names.add(a.getUsername());
			}
		}
		return names;
	}

	public static List<UUID> listUUIDS(InetAddress address) {
		ArrayList<UUID> ids = new ArrayList<>();
		if (checkConnection()) {
			for (Alt a : Carabiner.getReader().getAlts(address)) {
				ids.add(a.getUUID());
			}
		}
		return ids;
	}

	/**
	 * Looks up every ip address used by the given username
	 *
	 * @param username
	 * 		the username to lookup
	 *
	 * @return a list of ip addresses used by this specific user
	 */
	public static List<InetAddress> listConnections(String username) {
		ArrayList<InetAddress> addresses = new ArrayList<>();
		if (checkConnection()) {
			for (Alt a : Carabiner.getReader().getAlts(username)) {
				addresses.add(a.getAddress());
			}
		}
		return addresses;
	}

	/**
	 * Looks up every ip address used by the given uuid
	 *
	 * @param uuid
	 * 		the uuid to lookup
	 *
	 * @return a list of ip addresses used by this specific user
	 */
	public static List<InetAddress> listConnections(UUID uuid) {
		ArrayList<InetAddress> addresses = new ArrayList<>();
		if (checkConnection()) {
			for (Alt a : Carabiner.getReader().getAlts(uuid)) {
				addresses.add(a.getAddress());
			}
		}
		return addresses;
	}

	/**
	 * Adds a player to the database
	 *
	 * @param player
	 * 		the player to add to the db
	 */
	public static void addEntry(ProxiedPlayer player) {
		if (checkConnection()) {
			Carabiner.getWriter().queueNewAlt(player);
		}
	}

	/**
	 * @param username
	 * 		the username to lookup
	 * @param address
	 * 		the ipv4 address to verify
	 *
	 * @return true if the user and ipv4 address are already entered into the database
	 */
	public static boolean containsUser(String username, InetAddress address) {
		return checkConnection() && Carabiner.getReader().exists(username, address);
	}

	/**
	 * Sets whether to notify admins of alt accounts when this specific account logs in. If any alts of this account
	 * log
	 * in, it will still notify admins unless they are set to be ignored as well
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
	 * Sets whether to notify admins of alt accounts when this specific account logs in. If any alts of this account
	 * log
	 * in, it will still notify admins unless they are set to be ignored as well
	 *
	 * @param player
	 * 		the player
	 * @param toIgnore
	 * 		whether to ignore them or not
	 */
	public static void setIgnoreState(ProxiedPlayer player, boolean toIgnore) {
		if (checkConnection()) {
			Carabiner.getWriter().queueUpdateAlt(player, toIgnore);
		}
	}

	/**
	 * @param username
	 * 		the username to look up
	 *
	 * @return true if the account is to be ignored
	 */
	public static boolean toIgnore(String username) {
		if (checkConnection()) {
			for (Alt a : Carabiner.getReader().getAlts(username)) {
				if (a.toIgnore()) return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the plugin is connected to a sql database. If it is not, it then checks whether it should attempt
	 * to reconnect. Otherwise it returns false
	 *
	 * @return true if the plugin is connected to a sql database
	 */
	public static boolean checkConnection() {
		if (Carabiner.isConnected()) {
			return true;
		} else if (Carabiner.toStayConnected()) {
			Carabiner.getInstance().connect();
			return Carabiner.isConnected();
		}
		return false;
	}
}
