package electron.tweaks;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import electron.userThread;
import electron.utils.Functions;

public class MusicManager{
	private static List<Thread> threadsallsessions = new ArrayList();
	private List<Thread> threads = new ArrayList();
	private Socket s;
	public MusicManager(Socket s) {
		this.s=s;
	}
	public String play(String fname) {
		Thread player = new MusicPlayer(new File(fname),s);
		player.setName(fname);
		threads.add(player);
		threadsallsessions.add(player);
		player.start();
		return "[PLAYER]: playing "+fname;
	}
	public String stopName(String name) {
		threads=Functions.checkAliveThreads(threads);
		for(int i = 0;i<threads.size();i++) {
			if(threads.get(i).getName().equalsIgnoreCase(name)) {
				threads.get(i).interrupt();
				threads.remove(i);
				return "[PLAYER]: Done.";
			}
		}
		return "[PLAYER]: Music not found.";
	}
	public String stopLast() {
		threads=Functions.checkAliveThreads(threads);
		threads.get(threads.size()-1).interrupt();
		threads.remove(threads.size()-1);
		return "[PLAYER]: Done.";
	}
	public static String stopAll() {
		threadsallsessions=Functions.checkAliveThreads(threadsallsessions);
		for(int i = 0;i<threadsallsessions.size();i++) {
			threadsallsessions.get(i).interrupt();
		}
		threadsallsessions.clear();
		return "[PLAYER]: stopped all sounds played.";
	}
	public void showPlayers() throws IOException {
		threads=Functions.checkAliveThreads(threads);
		Functions.sendData("[PLAYER]: list of players:", s);
		for(int i = 0;i<threads.size();i++) {
			Functions.sendData(threads.get(i).getName(), s);
		}
		Functions.sendData("[PLAYER]: Done.", s);
	}
	
}
