package electron;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import electron.cmd.ServerCommands;
import electron.tweaks.MessageBoxTweaks;
import electron.tweaks.MouseTweaks;
import electron.tweaks.MusicManager;
import electron.utils.Functions;
import electron.utils.logger;
//Class for connected user actions
public class userThread extends Thread{
	public Socket client;
	public MusicManager musicmgr;
	public MessageBoxTweaks mbox;
	public static boolean mouseEn = false;
	public boolean cmdEn = false;
	private static Robot r = null;
	
	public userThread(Socket client) {
		this.client=client;
		musicmgr = new MusicManager(client);
		mbox = new MessageBoxTweaks(client);
	}
	
	public void run() {
		try {
			ServerCommands cmdExecutor = new ServerCommands(this);
			while(true) {
				LockSupport.parkNanos(100);
				//Connection status checker
				if(!client.isConnected()) {
					client.close();
	            	logger.log("Disconnected from "+client);
	            	Thread.currentThread().stop();
				}
				//Input from client listener
	           DataInputStream in = new DataInputStream(client.getInputStream());
	           String str = in.readUTF();
	           //Checker: received empty?
	           if(str.isEmpty()) {
	        	   sendData("ERROR: Your message is empty. Try again.");
	        	   continue;
	           }
	           JSONObject received = (JSONObject) ParseJs(str);
	           //If mouse mode enabled
	           if(mouseEn) {
	        	   int x = Integer.parseInt(String.valueOf(received.get("x")));
	        	   int y = Integer.parseInt(String.valueOf(received.get("y")));
	        	   MouseTweaks.setMouse(x, y);
	           }
	           String command = String.valueOf(received.get("command"));
	           //Commands needed all time
	           if(command.equalsIgnoreCase("/ping")) {
	        	   sendData("Electron's RemoteControl_Server is here!");
	        	   continue;
	           }
	           //Disconnect from server
	           if(command.equalsIgnoreCase("/exit")) {
	        	   client.close();
	        	   return;
	           }
	           if(cmdExecutor.executeCommand(command)) {
	        	   continue;
	           }
	           sendData("Unknown command. For help use /help command.");
			}
		} catch (IOException e) {
			logger.error("[NETWORK]: error: "+e.getMessage());
		}
	}
	//send data method
	public void sendData(String formire) throws IOException{
		Functions.sendData(formire, client);
	}
	//JSON parser
	private static Object ParseJs(String d) {
		if(d==null) {return null;}
		try {
			Object obj = new JSONParser().parse(d);
			return obj;
		} catch (ParseException e) {
			return null;
		}
	}
}