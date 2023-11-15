package electron.tweaks;

import java.awt.AWTException;
import java.awt.Robot;

public class MouseTweaks {
	private static boolean mouseFixed = false;
	private static Thread mousefixer = new MouseFixer();
	private static Robot r;
	public static void setRobot(Robot ra) {
		r=ra;
	}
	public static void setMouse(int x,int y) {
		r.mouseMove(x, y);
	}
	public static void clickMouse(int btn) {
	        r.mousePress(btn);
	        r.mouseRelease(btn);
	}
	public static String fixMouse() {
		mouseFixed=!mouseFixed;
		if(mouseFixed==true) {
			mousefixer = new MouseFixer();
			mousefixer.start();
			return "[MOUSE_FIXER]: fixer enabled.";
		}else {
			mousefixer.stop();
			return "[MOUSE_FIXER]: fixer disabled.";
		}
	}
}
