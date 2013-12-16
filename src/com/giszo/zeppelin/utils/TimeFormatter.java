package com.giszo.zeppelin.utils;

public class TimeFormatter {
	public static String format(int secs) {
		int hours = secs / 3600;
		int mins  = (secs / 60) % 60;
		secs = secs % 60;
		
		if (hours > 0)
			return String.format("%d:%02d:%02d", hours, mins, secs);
		else
			return String.format("%02d:%02d", mins, secs);
	}
}
