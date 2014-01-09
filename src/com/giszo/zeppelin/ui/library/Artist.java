package com.giszo.zeppelin.ui.library;

public class Artist {
	private int id;
	private String name;
	private int albums;

	public Artist(int id, String name, int albums) {
		this.id = id;
		this.name = name;
		this.albums = albums;
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
}
