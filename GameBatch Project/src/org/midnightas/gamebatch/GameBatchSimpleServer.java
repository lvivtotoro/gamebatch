package org.midnightas.gamebatch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameBatchSimpleServer implements Runnable {

	public Server server;

	public HashMap<String, Double> vars = new HashMap<String, Double>();

	public File gboutputf;
	public FileWriter gboutput;

	public HashMap<String, Double> tempVars = new HashMap<String, Double>();

	public GameBatchSimpleServer() throws IOException {
		server = new Server();
		server.getKryo().register(String.class);
		gboutputf = new File(System.getenv().get("TEMP"), "GAMEBATCH");
		gboutput = new FileWriter(gboutputf, false);
		server.addListener(new Listener() {
			public void received(Connection c, Object o) {
				if (o instanceof String) {
					if (((String) o).contains(";"))
						for (String cmd : ((String) o).split("\\;")) {
							command(cmd);
						}
					else
						command(o.toString());
					tempVars.clear();
				}
			}
		});
		server.start();
		server.bind(29875);
	}

	public void command(String str) {
		String[] s = str.toString().split(" ");
		if (str.contains("=")) {
			String[] s0 = str.split("=", 2);
			tempVars.put(s0[0], GameBatch.eval(s0[1], this));
		} else if (s[0].equalsIgnoreCase("setvar")) {
			String eq = "";
			for (int i = 2; i < s.length; i++) {
				eq = eq + s[i];
			}
			vars.put(s[1], GameBatch.eval(eq, this));
		} else if (s[0].equalsIgnoreCase("variable")) {
			try {
				gboutput.write(vars.get(s[1]) + "");
				gboutput.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (s[0].equalsIgnoreCase("math")) {
			try {
				String eq = "";
				for (int i = 1; i < s.length; i++) {
					eq = eq + s[i];
				}
				System.out.println(eq);
				gboutput.write(GameBatch.eval(eq, this) + "");
				gboutput.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (s[0].equalsIgnoreCase("exit")) {
			try {
				server.stop();
				server.close();
				gboutput.close();
				gboutputf.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
	}

}
