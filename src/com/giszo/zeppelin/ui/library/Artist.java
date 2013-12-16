package com.giszo.zeppelin.ui.library;

public class Artist {
	private int id;
	private String name;
	private int albums;
	private int songs;

	public Artist(int id, String name, int albums, int songs) {
		this.id = id;
		this.name = name;
		this.albums = albums;
		this.songs = songs;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAlbums() {
		return albums;
	}
	
	public int getSongs() {
		return songs;
	}
}
