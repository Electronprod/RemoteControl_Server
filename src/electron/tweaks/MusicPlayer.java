package electron.tweaks;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicPlayer extends Thread{
	File filename;
	Socket s;
	public MusicPlayer(File f,Socket s) {
		filename=f;
		this.s=s;
	}
	public void run() {
	      try {
			      Clip clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(filename));
			      clip.start();
			      try {
					Thread.sleep(clip.getMicrosecondLength() / 1000L);
				} catch (InterruptedException e) {
					//If we stopping track
				}
			      clip.stop();
			      clip.close();
			      return;
					} catch (LineUnavailableException e) {
						sendData("[PLAYER][ERROR]: "+e.getMessage());
					} catch (IOException e) {
						sendData("[PLAYER][ERROR]: "+e.getMessage());
					} catch (UnsupportedAudioFileException e) {
						sendData("[PLAYER][ERROR]: format error: "+e.getMessage());
					}
			    
	}
	private void sendData(String formire){
		OutputStream outToServer;
		try {
			outToServer = s.getOutputStream();			
			DataOutputStream out1 = new DataOutputStream(outToServer);
		    out1.writeUTF(formire);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
	}
}
