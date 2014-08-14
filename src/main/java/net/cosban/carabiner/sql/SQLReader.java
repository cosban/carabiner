package net.cosban.carabiner.sql;

import net.cosban.carabiner.Carabiner;
import net.cosban.carabiner.files.ConfigurationFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLReader {
	private final String    prefix;
	private final String    altsTable;
	private       Carabiner plugin;
	private ConfigurationFile config = Carabiner.getConfig();

	private SQLReader(Carabiner instance) {
		plugin = instance;
		prefix = config.getPrefix() != "" ? config.getPrefix() + "_" : "";
		altsTable = prefix + "alts";
	}

	public static SQLReader getManager(Carabiner instance) {
		SQLReader manager = new SQLReader(instance);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			Carabiner.debug().debug(manager.getClass(), e);
		}
		return manager;
	}

	public ArrayList<String> getAltNames(int altid) {
		return runStringQuery("SELECT player FROM `" + altsTable + "` WHERE (altid=" + altid + ");");
	}

	public ArrayList<String> getAltNames(String address) {
		return runStringQuery("SELECT player FROM `" + altsTable + "` WHERE (address='" + address + "');");
	}

	public ArrayList<String> getAltIPs(int altid) {
		return runStringQuery("SELECT address FROM `" + altsTable + "` WHERE (altid=" + altid + ");");
	}

	public ArrayList<String> getAltIPs(String name) {
		return runStringQuery("SELECT address FROM `" + altsTable + "` WHERE (player='" + name + "');");
	}

	public ArrayList<Boolean> getTracking(String name){
		return runBooleanQuery("SELECT track FROM `" + altsTable + "` WHERE (player='" + name + "');");
	}

	public boolean entryExists(String name, String address) {
		return runStringQuery("SELECT * FROM`"
				+ altsTable
				+ "` WHERE (player='"
				+ name
				+ "' AND address='"
				+ address
				+ "');").size() > 0;
	}

	public int getAltID(ProxiedPlayer player) {
		return runIntegerQuery("SELECT altid FROM `"
				+ altsTable
				+ "` WHERE (playerid='"
				+ player.getUniqueId().toString()
				+ "' OR address='"
				+ player.getAddress().getAddress().getHostAddress()
				+ "');");
	}

	public int getAltID(String name) {
		return runIntegerQuery("SELECT altid FROM `" + altsTable + "` WHERE (player='" + name + "');");
	}

	public ArrayList<String> runStringQuery(String query) {
		final Connection c = plugin.getConnection();
		try {
			Statement state = c.createStatement();
			ArrayList<String> s = stringToList(state.executeQuery(query));
			state.close();
			c.close();
			return s;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return null;
		}
	}

	private ArrayList<String> stringToList(ResultSet rs) {
		try {
			ArrayList<String> names = new ArrayList<>();
			while (rs.next()) {
				names.add(rs.getString("player"));
			}
			return names;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return null;
		}
	}

	private int runIntegerQuery(String query) {
		final Connection c = plugin.getConnection();
		try {
			Statement state = c.createStatement();
			int i = lowest(state.executeQuery(query));
			state.close();
			c.close();
			return i;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return -1;
		}
	}

	private int lowest(ResultSet rs) {
		try {
			int i = Integer.MAX_VALUE;
			while (rs.next()) {
				i = (rs.getInt("altid") < i) ? rs.getInt("altid") : i;
			}
			return i;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return -1;
		}
	}

	public ArrayList<Boolean> runBooleanQuery(String query) {
		final Connection c = plugin.getConnection();
		try {
			Statement state = c.createStatement();
			ArrayList<Boolean> b = booleanToList(state.executeQuery(query));
			state.close();
			c.close();
			return b;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return null;
		}
	}

	private ArrayList<Boolean> booleanToList(ResultSet rs) {
		try {
			ArrayList<Boolean> b = new ArrayList<>();
			while (rs.next()) {
				b.add(rs.getBoolean("track"));
			}
			return b;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return null;
		}
	}
}
