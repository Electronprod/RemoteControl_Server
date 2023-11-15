package electron.utils;

import electron.RemoteControl_Server;

public class logger {
	public static void error(String msg) {
		System.err.println(msg);
	}
	public static void log(String msg) {
		System.out.println(msg);
	}
	public static void debug(String msg) {
		if(RemoteControl_Server.debugOn==false) {return;}
		System.out.println("[DEBUG]"+msg);
	}
}