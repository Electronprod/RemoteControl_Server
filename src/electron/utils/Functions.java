package electron.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Functions {

	public static List<Thread> checkAliveThreads(List<Thread> threads) {
		List<Thread> alivethreads = new ArrayList();
		for(int i = 0; i<threads.size();i++) {
			if(threads.get(i).isAlive()) {
				alivethreads.add(threads.get(i));
			}
		}
		return alivethreads;
	}
	public static void sendData(String formire,Socket client) throws IOException{
		OutputStream outToServer = client.getOutputStream();	   
			DataOutputStream out1 = new DataOutputStream(outToServer);
		    out1.writeUTF(formire);
	}
	public static boolean isMultiCommand(String commandname,String command) {
		if(!command.contains(" ")) {return false;}
		if(!command.contains(commandname)) {return false;}
		if(!command.contains(commandname+" ")) {return false;}
		return true;
	}
	public static String[] getCommandArgs(String in) {
		String[] spl = in.split(" ");
		return spl;
	}
	public static boolean isNumber(String s) {
		 try {
		        Integer.parseInt(s);
		        return true;
		    } catch (NumberFormatException e) {
		        return false;
		    }
	}
	public static Robot unitRobot() {
		Robot r;
		try {
			r=new Robot();
			logger.log("[ROBOT]: unit success.");
			return r;
		} catch (AWTException e) {
			logger.log("[ROBOT]: unit error: "+e.getMessage());
		}
		return null;
	}
}
