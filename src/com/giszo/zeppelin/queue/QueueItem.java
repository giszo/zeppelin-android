package com.giszo.zeppelin.queue;

import java.util.List;

public abstract class QueueItem {
	protected QueueItem parent;
	protected int depth;
	
	public QueueItem(QueueItem parent, int depth) {
		this.parent = parent;
		this.depth = depth;
	}

	public int countItems() {
		return 1;
	}
	
	public int getDepth() {
		return depth;
	}

	public abstract QueueItem get(int index);
	
	public abstract void indexOf(List<Integer> index, QueueItem item);
}
