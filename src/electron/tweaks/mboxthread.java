package electron.tweaks;
import java.awt.Frame;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import electron.utils.Functions;
import electron.utils.logger;

public class mboxthread extends Thread{
	private String text;
	private int type;
	private int msgtype;
	private String title;
	private Socket s;
	private List<Thread> boxesAll;
	private List<Thread> boxes;
	/**
	 * Builder
	 * @param text - text to show
	 * @param type - type to show
	 * @param msgtype - type of messageBox
	 * @param title - title of message
	 * @param s - Socket to send answer
	 */
	public mboxthread(String text, int type,int msgtype,String title,Socket s,List<Thread> boxesAll,List<Thread> boxes) {
		Thread.currentThread().setPriority(MIN_PRIORITY);
		this.type=type;
		this.text=text;
		this.msgtype=msgtype;
		this.title=title;
		this.s=s;
		this.boxes=boxes;
		this.boxesAll=boxesAll;
		boxes.add(Thread.currentThread());
		boxesAll.add(Thread.currentThread());
	}
	//Thread body
	public void run() {
		if(msgtype==0) {
			//Show message
			sendData("[MESSAGES]: showed message dialog.");
			JOptionPane.showMessageDialog(null, text, title, type);
			sendData("[MESSAGES]: message dialog closed.");
		}else if(msgtype==1) {
			//Show dialog with Yes/no/cancel buttons
			sendData("[MESSAGES]: showed message dialog.");
			sendData("[MESSAGES]: messagebox(YES/NO/CANCEL): selected: "+JOptionPane.showConfirmDialog(null, text));
		}else if(msgtype==2) {
			//Show input dialog
			sendData("[MESSAGES]: showed message dialog.");
			sendData("[MESSAGES]: messagebox(INPUT): "+JOptionPane.showInputDialog(null, text, title, type));
		}else {
			sendData("[MESSAGES][ERROR]: incorrect type of message.");
		}
		boxes.remove(Thread.currentThread());
		boxesAll.remove(Thread.currentThread());
	}
	private void sendData(String msg) {
		try {
			Functions.sendData(msg, s);
		} catch (IOException e) {
			logger.log("[MESSAGES_SEND]: error sending response: "+e.getMessage());
		}
	}
}
