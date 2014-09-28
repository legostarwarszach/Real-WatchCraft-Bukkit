package com.zafcoding.watchcraft.bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.zafcoding.watchcraft.bukkit.Reward.Platform;
import com.zafcoding.watchcraft.bukkit.background.RSAIO;
import com.zafcoding.watchcraft.bukkit.background.RSAKeygen;
import com.zafcoding.watchcraft.bukkit.background.WatchCraftLisitner;

public class WatchCraft extends JavaPlugin {

	double version = 1.5;
	static WatchCraft instance;
	static KeyPair keypair;
	String addres = Bukkit.getIp();
	int port = 3144;
	Logger logger = Logger.getLogger("WatchCraft");

	@Override
	public void onEnable() {
		logger.log(Level.INFO, "[WatchCraft] WatchCraft v." + version
				+ " enableing....");
		instance = this;
		File rsaDirectory = new File(getDataFolder() + "/rsa");
		File logfile = new File(getDataFolder() + "/log.txt");
		if (configExsits()) {
			addres = getConfig().getString("IP");
			port = getConfig().getInt("Port");
			this.saveConfig();
			this.reloadConfig();
		} else {
			logger.log(Level.INFO, "Loaded Config!");
			this.getConfig().set("Config.exsits", true);
			this.getConfig().set("StoreCommand", false);
			this.getConfig().set("IP", "0.0.0.0");
			this.getConfig().set("Port", 3144);
			this.getConfig().set("Command", "watchstore");
			this.getConfig().set("Log", true);
			this.saveConfig();
			this.reloadConfig();
		}
		if (!rsaDirectory.exists()) {

			logger.log(Level.INFO,
					"[WatchCraft] Loading WatchCraft for the first time....");
			rsaDirectory.mkdir();

			if (addres == null || addres.length() == 0) {
				addres = "0.0.0.0";
			}

			getConfig().set("IP", addres);
			getConfig().set("Port", port);
			saveConfig();
			reloadConfig();
			logger.info("###############################################################");
			logger.info("WatchCraft is automatly assigning the listier to port 3144! If");
			logger.info("you are running on an external host, you may need to check with");
			logger.info("your host to make sure that port is avablile! :)");
			logger.info("###############################################################");

			try {
				keypair = RSAKeygen.generate(2048);
				RSAIO.save(rsaDirectory, keypair);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				keypair = RSAIO.load(rsaDirectory);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			WatchCraftLisitner wcl = new WatchCraftLisitner(this, addres, port);
			wcl.start();
			logger.info("Starting the listiner on " + addres + ":" + port);
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"------------------------------------------------------------");
			logger.log(Level.SEVERE, "Failed to bind to port! The port " + port
					+ " may already be in use!");
			logger.log(Level.SEVERE,
					"------------------------------------------------------------");
		}
		logger.log(Level.INFO, "[WatchCraft] WatchCraft v." + version
				+ " enabled!");

		if (getConfig().getBoolean("Log") == true) {
			if (!logfile.exists()) {
				//TODO: ERROR: making folder instead of .txt file
				logfile.mkdir();
			}
		}
	}

	@Override
	public void onDisable() {
		logger.log(Level.INFO, "[WatchCraft] WatchCraft v." + version
				+ " disableing....");
		logger.log(Level.INFO, "[WatchCraft] WatchCraft v." + version
				+ " disabled!");
	}

	public boolean configExsits() {
		return getConfig().getBoolean("Config.exsits");
	}

	public static WatchCraft getInstance() {
		return instance;
	}

	public double getVersion() {
		return version;
	}

	public KeyPair getKeyPair() {
		return keypair;
	}

	public Logger getLog() {
		return logger;
	}

	public Platform StringtoPlatform(String platform) {
		if (platform.equalsIgnoreCase("android")) {
			return Platform.ANDROID;
		}
		if (platform.equalsIgnoreCase("ios")) {
			return Platform.IOS;
		}
		if (platform.equalsIgnoreCase("website")) {
			return Platform.WEBSITE;
		}
		return null;
	}
	public void write(String string) throws IOException{
		FileOutputStream output = new FileOutputStream(getDataFolder() + "/log.txt");
		output.write(string.getBytes());
		output.close();
	}
}