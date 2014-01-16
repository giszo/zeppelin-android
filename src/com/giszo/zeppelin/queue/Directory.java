package com.giszo.zeppelin.queue;

public class Directory extends ContainerQueueItem {
	private boolean visible = true;

	private String name;
	
	public Directory(QueueItem parent, int depth, String name) {
		super(parent, depth);

		this.name = name;
	}
	
	@Override
	public int countItems() {
		if (!visible)
			return 1;
		
		return super.countItems();
	}

	public String getName() {
		return name;
	}

	public int getSongCount() {
		return items.size();
	}
	
	public void toggleVisibility() {
		visible = !visible;
	}
}
