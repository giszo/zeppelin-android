package com.giszo.zeppelin.ui.library;

public class Album {
	private int id;
	private int artistId;
	private String name;
	private int songs;
	private int length;
	
	public Album(int id, int artistId, String name, int songs, int length) {
		this.id = id;
		this.artistId = artistId;
		this.name = name;
		this.songs = songs;
		this.length = length;
	}
	
	public int getId() {
		return id;
	}

	public int getArtistId() {
		return artistId;
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
