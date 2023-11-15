package electron;

import java.awt.Robot;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import electron.cmd.CMDShell;
import electron.tweaks.Keyboard;
import electron.tweaks.MouseTweaks;
import electron.utils.Functions;
import electron.utils.logger;

public class RemoteControl_Server {
	public static boolean debugOn = true;
	
	public static void main(String[] args) {
		//Port range
		int maxport = 50020;
		int minport = 50011;
		//Network ports opener
		logger.log("Starting listeners of RemoteControl_Server...");
		List<Thread> threads = new ArrayList();
		for(int i=0;i<=maxport-minport;i++) {
			int port = minport+i;
			Thread s =new Listener(port);
			s.start();
			threads.add(s);
		}
		logger.debug(" Threads started - "+threads.size());
		//Loading robot
		Robot r = Functions.unitRobot();
		Keyboard.setRobot(r);
		MouseTweaks.setRobot(r);
		logger.log("Loading done. Waiting for connections...");
	}
	
}
class Listener extends Thread{
	private int port;
	public Listener(int port) {
		this.port = port;
	}
	
	public void run() {
		Thread.currentThread().setPriority(MIN_PRIORITY);
		Thread.currentThread().setName(""+port);
		ServerSocket servSocket;
		try {
			servSocket = new ServerSocket(port);
	        while (true) {
	        	LockSupport.parkNanos(100);
	        	logger.debug(Thread.currentThread().getName()+" - opened port "+port);
	            Socket fromClientSocket = servSocket.accept();
	            logger.log("Connected to "+ fromClientSocket.getRemoteSocketAddress());
	            Thread t = new userThread(fromClientSocket);
	            t.start();
	            t.join();
	            fromClientSocket.close();
	            logger.log(Thread.currentThread().getName()+": Disconnected from remote client.");
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
}
