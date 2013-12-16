package com.giszo.zeppelin.ui.library;

public class File {
	private int id;
	private String name;
	private String title;
	private int length;
	
	public File(int id, String name, String title, int length) {
		this.id = id;
		this.name = name;
		this.title = title;
		this.length = length;
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
}
