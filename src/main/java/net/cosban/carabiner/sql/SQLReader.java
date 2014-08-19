package net.cosban.carabiner.sql;

import net.cosban.carabiner.Carabiner;
import net.cosban.carabiner.files.ConfigurationFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

	public ArrayList<String> getNamesFromUUID(String uuid) {
		return runStringQuery("SELECT player FROM `" + altsTable + "` WHERE (playerid='" + uuid + "');", "player");
	}

	public String getUUIDFromName(String name) {
		//TODO: clear DB before launching because uuid's are unique per server
		return runStringQuery("SELECT playerid FROM `"
				+ altsTable
				+ "` WHERE (player='"
				+ name
				+ "');", "playerid").get(0);
	}

	public ArrayList<String> getNamesFromAddress(String address) {
		return runStringQuery("SELECT player FROM `" + altsTable + "` WHERE (address='" + address + "');", "player");
	}

	public ArrayList<String> getAddressesFromName(String name) {
		return runStringQuery("SELECT address FROM `" + altsTable + "` WHERE (player='" + name + "');", "address");
	}

	public boolean exists(String name, String address) {
		// We use 'player' as a column field because anything will work, we just need to see that there are results
		// probably not the best solution, but it will work for now
		return runStringQuery("SELECT * FROM`"
				+ altsTable
				+ "` WHERE (player='"
				+ name
				+ "' AND address='"
				+ address
				+ "');", "player").size() > 0;
	}

	public boolean getToIgnore(String name) {
		return runBooleanQuery("SELECT toignore FROM `" + altsTable + "` WHERE (player='" + name + "');");
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

	public boolean runBooleanQuery(String query) {
		final Connection c = plugin.getConnection();
		try {
			Statement state = c.createStatement();
			ResultSet rs = state.executeQuery(query);
			ArrayList<Boolean> b = new ArrayList<>();
			while (rs.next()) {
				b.add(rs.getBoolean("toignore"));
			}
			state.close();
			c.close();
			return b.contains(true);
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
			return false;
		}
	}
}
