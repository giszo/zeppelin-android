package com.giszo.zeppelin.queue;

public class Album extends ContainerQueueItem {
	private boolean visible = true;

	private com.giszo.zeppelin.ui.library.Album album;
	
	public Album(QueueItem parent, int depth, com.giszo.zeppelin.ui.library.Album album) {
		super(parent, depth);

		this.album = album;
	}
	
	@Override
	public int countItems() {
		if (!visible)
			return 1;
		
		return super.countItems();
	}

	public com.giszo.zeppelin.ui.library.Album getAlbum() {
		return album;
	}

	public int getSongCount() {
		return items.size();
	}
	
	public void toggleVisibility() {
		visible = !visible;
	}
}
