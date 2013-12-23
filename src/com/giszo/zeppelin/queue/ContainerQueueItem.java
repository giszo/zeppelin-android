package com.giszo.zeppelin.queue;

import java.util.ArrayList;
import java.util.List;

public class ContainerQueueItem extends QueueItem {
	protected List<QueueItem> items = new ArrayList<QueueItem>();
	
	public ContainerQueueItem(QueueItem parent, int depth) {
		super(parent, depth);
	}
	
	public void add(QueueItem item) {
		items.add(item);
	}
	
	@Override
	public int countItems() {
		int count = 1 /* this one */;
		
		for (QueueItem item : items) {
			count += item.countItems();
		}
		
		return count;
	}

	@Override
	public QueueItem get(int index) {
		if (index == 0)
			return this;

		// skip this item
		index -= 1;

		for (QueueItem item : items) {
			int count = item.countItems();
			
			if (index < count)
				return item.get(index);
			
			index -= count;
		}
		
		// this should not happen in an ideal world :)
		return null;
	}
	
	@Override
	public void indexOf(List<Integer> index, QueueItem item) {
		index.add(0, Integer.valueOf(items.indexOf(item)));
		
		if (parent != null)
			parent.indexOf(index, this);
	}
}
