package net.cosban.carabiner;

import net.cosban.carabiner.commands.CarabinerCommand;
import net.cosban.carabiner.files.ConfigurationFile;
import net.cosban.carabiner.sql.SQLReader;
import net.cosban.carabiner.sql.SQLWriter;
import net.cosban.utils.Debugger;
import net.cosban.utils.ReflectiveClassStruct;
import net.cosban.utils.SQLConnectionPool;
import net.cosban.utils.commands.CommandBase;
import net.cosban.utils.files.FileManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Carabiner extends Plugin {
	public static  Logger            logger;
	private static String            version;
	private static Debugger          debug;
	private static FileManager       files;
	private static SQLReader         reader;
	private static SQLWriter         writer;
	private static boolean           connected;
	private static Carabiner         instance;
	private        SQLConnectionPool pool;

	public static String getVersion() {
		return version;
	}

	public static Carabiner getInstance() {
		return instance;
	}

	public static ConfigurationFile getConfig() {
		return ((ConfigurationFile) files.getFile("configuration"));
	}

	public static Debugger debug() {
		return debug;
	}

	public static boolean isConnected() {
		return connected;
	}

	public static SQLReader getReader() {
		return reader;
	}

	public static SQLWriter getWriter() {
		return writer;
	}

	public void onEnable() {
		instance = this;
		version = getDescription().getVersion();
		files = new FileManager(getClass(), "./plugins/snip/alts/");
		debug = new Debugger(getClass().getName(), getConfig().toUseDebug());
		debug.setLogger(getLogger());
		debug.debug(getClass(), "THE DEBUGGER IS ENABLED AND MAY BE VERY SPAMMY. THIS IS YOUR ONLY WARNING.");
		logger = getLogger();

		try {
			Class.forName("net.cosban.snip.Snip");
		} catch (ClassNotFoundException e) {
			Carabiner.debug().debug(getClass(), "Snip is required but was not found!");
			return;
		}

		registerCommands();
		getProxy().getPluginManager().registerListener(instance, new CarabinerListener());
		debug.debug(getClass(), "Connecting to MySQL... "
				+ getConfig().getUsername()
				+ "@"
				+ getConfig().getURL());
		try {
			pool = new SQLConnectionPool(getConfig().getURL(), getConfig().getUsername(), getConfig().getPassword());
			Connection c = getConnection();
			if (c == null) {
				connected = false;
			} else {
				debug.debug(getClass(), "Connected to MySQL database.");
			}
			ProxyServer.getInstance().getScheduler().runAsync(instance, pool.getCloser());
			reader = SQLReader.getManager(this);
			writer = SQLWriter.getManager(this);
			ProxyServer.getInstance().getScheduler().schedule(instance, writer, 1, 1, TimeUnit.SECONDS);
		} catch (ClassNotFoundException e) {
			connected = false;
			debug.debug(getClass(), e);
		}
		if (!connected) {
			logger.warning("There was an issue connecting to the MySQL server.");
			logger.warning("It is HIGHLY encouraged that you fix your database connection!");
		}
	}

	public void onDisable() {
		ProxyServer.getInstance().getScheduler().cancel(this);
		pool.close();
	}

	public FileManager getFiles() {
		return files;
	}

	public Connection getConnection() {
		try {
			final Connection c = pool.getConnection();
			if (!connected) {
				debug.debug(getClass(), "MySQL connection rebuild");
				connected = true;
			}
			return c;
		} catch (final Exception e) {
			if (connected) {
				debug.debug(getClass(), "SQL connection error");
				connected = false;
			} else {
				debug.debug(getClass(), "SQL connection lost");
			}
			debug.debug(getClass(), e);
			return null;
		}
	}

	private void registerCommands() {
		for (Class<?> c : ReflectiveClassStruct.getClassesForPackage(getClass(), "net.cosban.scct.commands")) {
			try {
				if (ReflectiveClassStruct.containsConstructor(c, String.class)) {
					if (c.getConstructor(String.class).isAnnotationPresent(CommandBase.class)) {
						String name = getCommandStructure(c).name();
						String[] aliases = getCommandStructure(c).aliases();
						String perms = getCommandStructure(c).permission();
						CarabinerCommand com = (CarabinerCommand) c.getConstructor(String.class, String.class, String[].class).newInstance(name, perms, aliases);
						ProxyServer.getInstance().getPluginManager().registerCommand(this, com);
						debug().debug(this.getClass(), "Registered command: "
								+ name);
					}
				}
			} catch (Exception e) {
				debug().debug(getClass(), e);
			}
		}
	}

	private CommandBase getCommandStructure(Class<?> c)
			throws NoSuchMethodException, SecurityException {
		return c.getConstructor(String.class).getAnnotation(CommandBase.class);
	}

	public CommandBase getCommandStructure(CarabinerCommand com)
			throws NoSuchMethodException, SecurityException {
		return getCommandStructure(com.getClass());
	}
}
