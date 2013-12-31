package com.giszo.zeppelin.ui.library;

public class File {
	public enum Type { UNKNOWN, MP3, FLAC };
	
	private int id;
	private String name;
	private String title;
	private int length;
	private int year;
	private int trackIndex;
	private Type type;
	private int samplingRate;
	
	public File(int id, String name, String title, int length, int year, int trackIndex, Type type, int samplingRate) {
		this.id = id;
		this.name = name;
		this.title = title;
		this.length = length;
		this.year = year;
		this.trackIndex = trackIndex;
		this.type = type;
		this.samplingRate = samplingRate;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return title;
	}

	public int getLength() {
		return length;
	}
	
	public int getYear() {
		return year;
	}
	
	public int getTrackIndex() {
		return trackIndex;
	}

	public Type getType() {
		return type;
	}
	
	public int getSamplingRate() {
		return samplingRate;
	}
}
