package org.qmpm.logtrie.tools;

import java.util.concurrent.TimeUnit;

public class TimeTools {
	
	public static String nanoToHourMinSecMilli(long nanoSeconds) {
		
		long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoSeconds);
		int hours = (int) (seconds / 3600);
		int min = (int) ((seconds % 3600) / 60);
		int sec = (int) (seconds) % 60;
		int millis = (int) TimeUnit.NANOSECONDS.toMillis(nanoSeconds) % 1000;
	
		String formatTime = "%02d:%02d:%02d:%03d";
		return String.format(formatTime, hours, min, sec, millis);
	}
}
