package org.midnightas.gamebatch;

public class GameBatchConsole {
	
	public static native void clear();

	public static native void setCursorPos(int x, int y);

	public static native int[] getConsoleSize();

	public static native void setPrintFG(int col);

	public static native void setPrintBG(int col);

	static {
		/* Taken from http://stackoverflow.com/a/5940770 */
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
		String realArch = arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64") ? "64" : "32";
		if(realArch.equalsIgnoreCase("64"))
			System.loadLibrary("gamebatch_x64");
		else
			System.loadLibrary("gamebatch");
	}

}
