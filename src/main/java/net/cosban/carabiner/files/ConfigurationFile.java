package net.cosban.carabiner.files;

import net.cosban.carabiner.Carabiner;
import net.cosban.utils.files.FileManager;
import net.cosban.utils.files.UtilFile;

import java.io.IOException;

public class ConfigurationFile extends UtilFile {

	public ConfigurationFile(FileManager instance) throws IOException {
		super(instance, "configuration");
		if (!ini.hasSection("config")) {
			ini.addSection("config");
			ini.addComment("config",
					"This is the configuration file, read the instructions located at cosban.net for more " +
							"information\n"
							+ "You were running Carabiner v"
							+ Carabiner.getVersion()
							+ " when this file was generated");
			ini.set("config", "debug", "false");
			ini.set("config", "degrees", "3");
		}
		if (!ini.hasSection("mysql")) {
			ini.addSection("mysql");
			ini.set("mysql", "hostname", "localhost");
			ini.set("mysql", "port", "3306");
			ini.set("mysql", "username", "root");
			ini.set("mysql", "database", "minecraft");
			ini.set("mysql", "table-prefix", "snip");
			ini.set("mysql", "password", "yesHorseBatteryStaple!");
		}
		files.save(this);
	}

	public boolean toUseDebug() {
		return Boolean.valueOf(ini.get("config", "debug"));
	}

	public int getDegrees() {
		return Integer.valueOf(ini.get("config", "degrees"));
	}

	public String getHostname() {
		return ini.get("mysql", "hostname");
	}

	public void setHostName(String hostname) {
		ini.set("mysql", "hostname", hostname);
		files.save(this);
	}

	public String getURL() {
		return "jdbc:mysql://"
				+ getHostname()
				+ ":"
				+ getPort()
				+ "/"
				+ getDatabase()
				+ "?useUnicode=true&characterEncoding=utf-8";
	}

	public int getPort() {
		return Integer.valueOf(ini.get("mysql", "port"));
	}

	public void setPort(int i) {
		ini.set("mysql", "port", String.valueOf(i));
		files.save(this);
	}

	public String getUsername() {
		return ini.get("mysql", "username");
	}

	public void setUsername(String s) {
		ini.set("mysql", "username", s);
	}

	public String getDatabase() {
		return ini.get("mysql", "database");
	}

	public void setDatabase(String s) {
		ini.set("mysql", "database", s);
	}

	public String getPrefix() {
		return ini.get("mysql", "table-prefix");
	}

	public void setPrefix(String s) {
		ini.set("mysql", "table-prefix", s);
	}

	public String getPassword() {
		return ini.get("mysql", "password");
	}

	public void setPassword(String password) {
		ini.set("mysql", "password", password);
		files.save(this);
	}
}
