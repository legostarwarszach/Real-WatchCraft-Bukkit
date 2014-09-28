package com.zafcoding.watchcraft.bukkit;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;

public class Reward {

	private Platform pform = null;
	private UUID uid = null;
	private String addres = null;
	private String comand = null;
	private String tie = null;

	public enum Platform {
		ANDROID, IOS, WEBSITE
	}

	public Reward(Platform platform, UUID uuid, String address, String command,
			String time) {
		pform = platform;
		uid = uuid;
		addres = address;
		comand = command;
		tie = time;
	}

	public void excuteCommand() {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comand);
		logConfig();
	}

	public void logConfig() {
		WatchCraft wcraft = WatchCraft.instance;
		if (wcraft.getConfig().getBoolean("Log") == false) {
			return;
		}
		try {
			wcraft.write("Log." + tie + ": " + 
					"Platform:" + pform + ", UUID:" + uid + ", Command:" + comand
							+ ";");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Platform getPlatform() {
		return pform;
	}

	public UUID getPlayerUUID() {
		return uid;
	}

	public String getAddress() {
		return addres;
	}

	public String getCommand() {
		return comand;
	}

	public String getTime() {
		return tie;
	}
	
}
