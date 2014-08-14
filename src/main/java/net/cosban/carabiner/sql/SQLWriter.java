package net.cosban.carabiner.sql;

import net.cosban.carabiner.Carabiner;
import net.cosban.carabiner.files.ConfigurationFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLWriter extends TimerTask {
	private final Queue<Row> queue = new LinkedList<Row>();
	private final Lock       lock  = new ReentrantLock();
	private final String    prefix;
	private       Carabiner plugin;
	private ConfigurationFile config = Carabiner.getConfig();

	private SQLWriter(Carabiner instance) {
		plugin = instance;
		prefix = config.getPrefix() != "" ? config.getPrefix() + "_" : "";

		try {
			final Connection c = plugin.getConnection();
			if (c == null) {
				throw new SQLException("Not connected");
			}
			final DatabaseMetaData dbm = c.getMetaData();
			final Statement state = c.createStatement();
			c.setAutoCommit(true);
			//TODO: rename track column to ignore
			verifyTable(dbm, state, prefix + "alts", "(uid INT UNSIGNED AUTO_INCREMENT NOT NULL,"
					+ " playerid varchar(36) NOT NULL,"
					+ " player varchar(32) NOT NULL,"
					+ " address varchar(255) NOT NULL,"
					+ " altid INT UNSIGNED NOT NULL,"
					+ " track TINYINT(1) NOT NULL DEFAULT 0,"
					+ " PRIMARY KEY (uid))");
			state.close();
			c.close();
		} catch (SQLException e) {
			Carabiner.debug().debug(getClass(), e);
		}
	}

	public static SQLWriter getManager(Carabiner instance) {
		SQLWriter manager = new SQLWriter(instance);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			Carabiner.debug().debug(manager.getClass(), e);
		}
		return manager;
	}

	private void verifyTable(DatabaseMetaData dbm, Statement state, String table, String query) throws SQLException {
		if (!dbm.getTables(null, null, table, null).next()) {
			Carabiner.debug().debug(getClass(), "Creating " + table + " table");
			state.execute("CREATE TABLE `" + table + "` " + query);
			if (!dbm.getTables(null, null, table, null).next())
				throw new SQLException("Table " + table + " not found and failed to create");
		} else {
			Carabiner.debug().debug(getClass(), "Verified " + table + " table, no need to create");
		}
	}

	public void queueNewAlt(ProxiedPlayer player, int id, boolean track) {
		queue.add(new AccountRow(player, id, track));
	}

	public void queueUpdateAlt(int id, boolean track) {
		queue.add(new AccountRow(id, track));
	}

	public void queueDeleteAlt(int id) {
		queue.add(new AccountRow(id));
	}

	@Override
	public void run() {
		if (queue.isEmpty() || !lock.tryLock()) return;
		final Connection c = plugin.getConnection();
		Statement state = null;
		if (queue.size() >= 10000) {
			Carabiner.debug().debug(getClass(), "Queue overloaded. Size: " + queue.size());
		}
		try {
			if (c == null) return;
			c.setAutoCommit(false);
			state = c.createStatement();
			final long start = System.currentTimeMillis();
			while (!queue.isEmpty() && (System.currentTimeMillis() - start < 1000)) {
				final Row r = queue.poll();
				if (r == null) continue;
				try {
					switch (r.getState()) {
						case REMOVE:
							state.execute(r.getDeleteStatement());
							break;
						case UPDATE:
							state.execute(r.getUpdateStatement());
							break;
						case INSERT:
						default:
							state.execute(r.getInsertStatement());
							break;
					}
				} catch (final SQLException e) {
					Carabiner.debug().debug(getClass(), e);
					break;
				}
			}
			c.commit();
		} catch (final SQLException e) {
			Carabiner.debug().debug(getClass(), e);
		} finally {
			try {
				if (state != null) {
					state.close();
				}
				if (c != null) {
					c.close();
				}
			} catch (final SQLException e) {
				Carabiner.debug().debug(getClass(), e);
			}
			lock.unlock();
		}
	}

	private static interface Row {
		public String getInsertStatement();

		public String getUpdateStatement();

		public String getDeleteStatement();

		public State getState();

		public enum State {
			INSERT,
			UPDATE,
			REMOVE
		}
	}

	private class AccountRow implements Row {
		private ProxiedPlayer player;
		private int           id;
		private boolean       track;
		private State         state;

		public AccountRow(ProxiedPlayer player, int id, boolean track) {
			this(player, id, track, State.INSERT);
		}

		public AccountRow(int id, boolean track) {
			this(null, id, track, State.UPDATE);
		}

		public AccountRow(int id) {
			this(null, id, false, State.REMOVE);
		}

		public AccountRow(ProxiedPlayer player, int id, boolean track, State state) {
			this.player = player;
			this.id = id;
			this.track = track;
			this.state = state;
		}

		@Override
		public String getInsertStatement() {
			return "INSERT INTO `"
					+ table
					+ "` (playerid, player, address, altid, track) VALUES ('"
					+ player.getUniqueId().toString() + "', '"
					+ player.getName() + "', '"
					+ player.getAddress().getAddress().getHostAddress() + "', "
					+ id + ", "
					+ track + ");";
		}

		@Override
		public String getUpdateStatement() {
			return "UPDATE `" + table + "` SET tracked=" + track + " WHERE altid=" + id + ";";
		}

		@Override
		public String getDeleteStatement() {
			return "DELETE FROM `" + table + "` WHERE ();";
		}

		@Override
		public State getState() {
			return state;
		}

		private final String table = prefix + "alts";

	}
}
