package net.cosban.carabiner.sql;

import net.cosban.carabiner.Alt;
import net.cosban.carabiner.Carabiner;
import net.cosban.carabiner.files.ConfigurationFile;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class SQLReader {
	private final String    altsTable;
	private       Carabiner plugin;

	private SQLReader(Carabiner instance) {
		plugin = instance;
		ConfigurationFile config = Carabiner.getConfig();
		String prefix = config.getPrefix() != "" ? config.getPrefix() + "_" : "";
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

	public ArrayList<Alt> getAlts(UUID uuid) {
		return runAltsQuery("SELECT * FROM `" + altsTable + "` WHERE (playerid='" + uuid.toString() + "');");
	}

	public ArrayList<Alt> getAlts(String name) {
		return runAltsQuery("SELECT * FROM `" + altsTable + "` WHERE (player='" + name + "');");
	}

	public ArrayList<Alt> getAlts(InetAddress address) {
		return runAltsQuery("SELECT * FROM `" + altsTable + "` WHERE (address='" + address.getHostAddress() + "');");
	}

	public String getUUIDFromName(String name) {
		//TODO: clear DB before launching because uuid's are unique per server
		return runStringQuery("SELECT playerid FROM `"
				+ altsTable
				+ "` WHERE (player='"
				+ name
				+ "');", "playerid").get(0);
	}

	public boolean exists(String name, InetAddress address) {
		return runCountQuery("SELECT COUNT(*) FROM`"
				+ altsTable
				+ "` WHERE (player='"
				+ name
				+ "' AND address='"
				+ address.getHostAddress()
				+ "');") > 0;
	}

	public ArrayList<Alt> runAltsQuery(String query) {
		final Connection c = plugin.getConnection();
		ArrayList<Alt> alts = new ArrayList<>();
		try {
			Statement state = c.createStatement();
			ResultSet rs = state.executeQuery(query);
			while (rs.next()) {
				alts.add(new Alt(rs.getString("player"), rs.getString("address"), rs.getString("playerid"),
						rs.getBoolean("toignore")));
			}
			state.close();
			c.close();
			return alts;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return null;
		}
	}

	public ArrayList<String> runStringQuery(String query, String column) {
		final Connection c = plugin.getConnection();
		ArrayList<String> s = new ArrayList<>();
		try {
			Statement state = c.createStatement();
			ResultSet rs = state.executeQuery(query);
			while (rs.next()) {
				if (!s.contains(rs.getString(column))) {
					s.add(rs.getString(column));
				}
			}
			state.close();
			c.close();
			return s;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return null;
		}
	}

	public int runCountQuery(String query){
		final Connection c = plugin.getConnection();
		int i = 0;
		try {
			Statement state = c.createStatement();
			ResultSet rs = state.executeQuery(query);
			if(rs.next()){
				i = rs.getInt(1);
			}
			state.close();
			c.close();
			return i;
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return 0;
		}
	}
}
