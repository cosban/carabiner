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

	private SQLWriter(Carabiner instance) {
		plugin = instance;
		ConfigurationFile config = Carabiner.getConfig();
		prefix = config.getPrefix() != "" ? config.getPrefix() + "_" : "";

		try {
			final Connection c = plugin.getConnection();
			if (c == null) {
				throw new SQLException("Not connected");
			}
			final DatabaseMetaData dbm = c.getMetaData();
			final Statement state = c.createStatement();
			c.setAutoCommit(true);
			verifyTable(dbm, state, prefix + "alts", "(uid INT UNSIGNED AUTO_INCREMENT NOT NULL,"
					+ " playerid varchar(36) NOT NULL,"
					+ " player varchar(32) NOT NULL,"
					+ " address varchar(255) NOT NULL,"
					+ " toignore TINYINT(1) NOT NULL DEFAULT 0,"
					+ " PRIMARY KEY (uid));");
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

	public void queueNewAlt(ProxiedPlayer player) {
		queue.add(new AccountRow(true, player));
	}

	public void queueUpdateAlt(ProxiedPlayer player, boolean toignore) {
		queue.add(new AccountRow(player, toignore));
	}

	public void queueDeleteAlt(ProxiedPlayer player) {
		queue.add(new AccountRow(false, player));
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
		private boolean       toignore;
		private State         state;

		public AccountRow(boolean toInsert, ProxiedPlayer player) {
			this(player, false, toInsert ? State.INSERT : State.REMOVE);
		}

		public AccountRow(ProxiedPlayer player, boolean toIgnore) {
			this(null, toIgnore, State.UPDATE);
		}

		public AccountRow(ProxiedPlayer player, boolean toignore, State state) {
			this.player = player;
			this.toignore = toignore;
			this.state = state;
		}

		@Override
		public String getInsertStatement() {
			return "INSERT INTO `"
					+ table
					+ "` (playerid, player, address, toignore) VALUES ('"
					+ player.getUniqueId().toString()
					+ "', '"
					+ player.getName()
					+ "', '"
					+ player.getAddress().getAddress().getHostAddress()
					+ "', "
					+ toignore
					+ ");";
		}

		@Override
		public String getUpdateStatement() {
			return "UPDATE `" + table + "` SET toignore=" + toignore + " WHERE player=" + player.getName() + ";";
		}

		@Override
		public String getDeleteStatement() {
			return "DELETE FROM `" + table + "` WHERE (player='" + player.getName() + "');";
		}

		@Override
		public State getState() {
			return state;
		}

		private final String table = prefix + "alts";

	}
}
