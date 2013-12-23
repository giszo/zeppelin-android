package com.giszo.zeppelin.queue;

import java.util.List;

public class File extends QueueItem {
	private com.giszo.zeppelin.ui.library.File file;
	
	public File(QueueItem parent, int depth, com.giszo.zeppelin.ui.library.File file) {
		super(parent, depth);

		this.file = file;
	}

	@Override
	public QueueItem get(int index) {
		if (index != 0)
			throw new RuntimeException("wtf?!");
		
		return this;
	}
	
	@Override
	public void indexOf(List<Integer> index, QueueItem item) {
		parent.indexOf(index, this);
	}

	public com.giszo.zeppelin.ui.library.File getFile() {
		return file;
	}
}
