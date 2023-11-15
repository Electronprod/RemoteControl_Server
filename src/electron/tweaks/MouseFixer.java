package electron.tweaks;

import java.util.concurrent.locks.LockSupport;

import electron.utils.Functions;

public class MouseFixer extends Thread{
	public void run() {
		while(true) {
			MouseTweaks.setMouse(0, 0);
			LockSupport.parkNanos(100);
		}
	}
}
