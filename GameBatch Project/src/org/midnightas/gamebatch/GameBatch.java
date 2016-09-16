package org.midnightas.gamebatch;

import java.io.File;
import java.io.FileWriter;

import com.esotericsoftware.kryonet.Client;

public class GameBatch {

	public static final void main(String[] args) {
		try {
			if (args.length == 0) {
				System.exit(0);
			}
			if (args[0].equals("simpleinit")) {
				new Thread(new GameBatchSimpleServer()).start();
			} else if (args[0].equals("print")) {
				System.out.print(args[1]);
			} else if (args[0].equals("fg")) {
				GameBatchConsole.setPrintFG(Integer.parseInt(args[1], 16));
			} else if (args[0].equals("bg")) {
				GameBatchConsole.setPrintBG(Integer.parseInt(args[1], 16));
			} else if (args[0].equals("setpos")) {
				GameBatchConsole.setCursorPos(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			} else if (args[0].equals("termw")) {
				FileWriter writer = new FileWriter(new File(System.getenv("TEMP"), "GAMEBATCH"), false);
				writer.write(GameBatchConsole.getConsoleSize()[0] + "");
				writer.close();
			} else if (args[0].equals("termh")) {
				FileWriter writer = new FileWriter(new File(System.getenv("TEMP"), "GAMEBATCH"), false);
				writer.write(GameBatchConsole.getConsoleSize()[1] + "");
				writer.close();
			} else {
				Client client = new Client(29875, 29875);
				client.start();
				client.connect(5000, "127.0.0.1", 29875);
				String toSend = String.join(" ", args);
				client.sendTCP(toSend);
				client.close();
				System.exit(0);
			}
		} catch (Exception e) {
			System.err.println("GameBatch has crashed with the following message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static double eval(final String str, final GameBatchSimpleServer server) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ')
					nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length())
					throw new RuntimeException("Unexpected: " + (char) ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			// | number | functionName factor | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (;;) {
					if (eat('+'))
						x += parseTerm(); // addition
					else if (eat('-'))
						x -= parseTerm(); // subtraction
					else
						return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;) {
					if (eat('*'))
						x *= parseFactor(); // multiplication
					else if (eat('/'))
						x /= parseFactor(); // division
					else
						return x;
				}
			}

			double parseFactor() {
				if (eat('+'))
					return parseFactor(); // unary plus
				if (eat('-'))
					return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.')
						nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z')
						nextChar();
					String func = str.substring(startPos, this.pos);
					if (server.tempVars.containsKey(func))
						x = server.tempVars.get(func);
					else if (server.vars.containsKey(func))
						x = server.vars.get(func);
					else
						throw new RuntimeException("No variable " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char) ch);
				}

				if (eat('^'))
					x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}

}
