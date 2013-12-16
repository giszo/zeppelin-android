package com.giszo.zeppelin.ui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.giszo.zeppelin.ui.library.File;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class QueueAdapter extends BaseAdapter {
	private Context context;

	private List<File> fileList = new ArrayList<File>();
	private Map<Integer, File> fileMap = new HashMap<Integer, File>();

	/// the ID of the currently played file
	private int currentId = -1;

	public QueueAdapter(Context context) {
		this.context = context;
	}
	
	public void add(File file) {
		fileList.add(file);
		fileMap.put(Integer.valueOf(file.getId()), file);
	}
	
	public File getFile(int id) {
		return fileMap.get(Integer.valueOf(id));
	}
	
	public void setCurrent(int id) {
		if (currentId == id)
			return;
		
		currentId = id;
		notifyDataSetChanged();
	}

	public void clear() {
		fileList.clear();
		fileMap.clear();
	}
	
	@Override
	public int getCount() {
		return fileList.size();
	}

	@Override
	public Object getItem(int index) {
		return fileList.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO
		return 0;
	}

	@Override
	public View getView(int index, View view, ViewGroup group) {
		File file = fileList.get(index);
		
		QueueItem item = new QueueItem(context);
		item.fill(file, file.getId() == currentId);

		return item;
	}

}
