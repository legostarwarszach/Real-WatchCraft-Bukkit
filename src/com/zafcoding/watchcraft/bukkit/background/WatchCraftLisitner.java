package com.zafcoding.watchcraft.bukkit.background;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;

import com.zafcoding.watchcraft.bukkit.Reward;
import com.zafcoding.watchcraft.bukkit.WatchCraft;

/**
 * Thanks to votifer for this code.
 * 
 * @author Zach Farley
 * @author Blake Beaupain
 * @author Kramer Campbell
 */
public class WatchCraftLisitner extends Thread {

	private WatchCraft plugin;
	Logger logger = WatchCraft.getInstance().getLog();

	private final String host;
	private final int port;
	private final String command = "";
	private ServerSocket server;
	private boolean running = true;

	public WatchCraftLisitner(WatchCraft plugin, String host, int port)
			throws Exception {
		this.plugin = plugin;
		this.host = host;
		this.port = port;

		initialize();
	}

	private void initialize() throws Exception {
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(host, port));
		} catch (Exception ex) {

			throw new Exception(ex);
		}
	}

	public void shutdown() {
		running = false;
		if (server == null)
			return;
		try {
			server.close();
		} catch (Exception ex) {
			logger.log(Level.WARNING,
					"Unable to shut down the lisitner cleanly...");
			logger.log(Level.WARNING,
					"Looks like we have to do this the hard way...");
		}
	}

	@Override
	public void run() {

		while (running) {
			try {
				Socket socket = server.accept();
				socket.setSoTimeout(5000);
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream()));
				InputStream in = socket.getInputStream();

				writer.write("WATCHCRAFT "
						+ WatchCraft.getInstance().getVersion());
				writer.newLine();
				writer.flush();

				byte[] block = new byte[256];
				in.read(block, 0, block.length);

				block = RSA.decrypt(block, WatchCraft.getInstance()
						.getKeyPair().getPrivate());
				int position = 0;

				String aa = readString(block, position);
				position += aa.length() + 1;
				if (!aa.equals("QWERTY101")) {
					throw new Exception("Unable to decode the RSA! Error 103");
				}

				String platform = readString(block, position);
				position += platform.length() + 1;
				String uuid = readString(block, position);
				position += uuid.length() + 1;
				String address = readString(block, position);
				position += address.length() + 1;
				String command = readString(block, position);
				position += command.length() + 1;
				String time = readString(block, position);
				position += time.length() + 1;

				Reward reward = new Reward(plugin.StringtoPlatform(platform),
						UUID.fromString(uuid), address, command, time);
				reward.excuteCommand();
				writer.close();
				in.close();
				socket.close();
			} catch (SocketException ex) {
				logger.log(Level.WARNING,
						"[WatchCraft] Socket Protacol Execption! Error 100",
						ex.getLocalizedMessage());
			} catch (BadPaddingException ex) {
				logger.log(Level.SEVERE,
						"[WatchCraft] Bad Padding Execption! Error 101");
				logger.log(Level.SEVERE,
						"[WatchCraft] Please make sure the Public Key you summited");
				logger.log(Level.SEVERE,
						"[WatchCraft] matches the one in the public.key",
						ex.getLocalizedMessage());
			} catch (Exception ex) {
				logger.log(Level.SEVERE,
						"[WatchCraft] Execption when recieveing new Reward! Error 102");
				logger.log(
						Level.SEVERE,
						"[WatchCraft] If this problem percits please contact us!",
						ex.getLocalizedMessage());
			}
		}
	}

	private String readString(byte[] data, int offset) {
		StringBuilder builder = new StringBuilder();
		for (int i = offset; i < data.length; i++) {
			if (data[i] == '\n')
				break; // Delimiter reached.
			builder.append((char) data[i]);
		}
		return builder.toString();
	}
}
