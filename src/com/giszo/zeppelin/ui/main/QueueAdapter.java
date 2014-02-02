package com.giszo.zeppelin.ui.main;

import com.giszo.zeppelin.queue.ContainerQueueItem;
import com.giszo.zeppelin.queue.File;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class QueueAdapter extends BaseAdapter {
	private Context context;

	private ContainerQueueItem root;

	/// the ID of the currently played file
	private int currentId = -1;

	public QueueAdapter(Context context) {
		this.context = context;
	}
	
	public void setRoot(ContainerQueueItem root) {
		this.root = root;
	}

	public void setCurrent(int id) {
		currentId = id;
	}

	@Override
	public int getCount() {
		if (root == null)
			return 0;

		return root.countItems() - 1 /* the root node does not count */;
	}

	@Override
	public Object getItem(int index) {
		return root.get(index + 1 /* skip the root */);
	}

	@Override
	public long getItemId(int index) {
		// TODO
		return 0;
	}

	@Override
	public View getView(int index, View view, ViewGroup group) {
		QueueItem item;
		
		// try to reuse a previously created view
		if (view != null)
			item = (QueueItem)view;
		else
			item = new QueueItem(context);

		com.giszo.zeppelin.queue.QueueItem qi = (com.giszo.zeppelin.queue.QueueItem)getItem(index);
		
		boolean played = false;
		
		if (qi instanceof File) {
			played = ((File)qi).getFile().getId() == currentId;
		}

		item.fill(qi, played);

		return item;
	}

}
