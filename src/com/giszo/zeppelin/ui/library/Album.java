package com.giszo.zeppelin.ui.library;

public class Album {
	private int id;
	private String name;
	private int songs;
	private int length;
	
	public Album(int id, String name, int songs, int length) {
		this.id = id;
		this.name = name;
		this.songs = songs;
		this.length = length;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public int getSongs() {
		return songs;
	}
	
	public int getLength() {
		return length;
	}
}
