package org.metawatch.manager;

public class BiCowidget {
	private static boolean BiCoflag = false;
	
	public static boolean checkBiCo() {
		return BiCoflag;
	}
	
	public static void clearBiCo() {
		BiCoflag=false;
	}

	public static void setBiCo() {
		BiCoflag=true;
	}
	
}
