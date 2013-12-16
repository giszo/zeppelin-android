package com.giszo.zeppelin.ui.library;

import java.util.ArrayList;
import java.util.List;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.utils.TimeFormatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {
	private Context context;

	private List<File> files = new ArrayList<File>();

	public FileAdapter(Context context) {
		this.context = context;
	}
	
	public void add(File file) {
		files.add(file);
	}
	
	public void clear() {
		files.clear();
	}

	@Override
	public int getCount() {
		return files.size();
	}

	@Override
	public Object getItem(int index) {
		return files.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		File file = files.get(index);

		View view = LayoutInflater.from(context).inflate(R.layout.library_file_item, null);

		TextView title = (TextView)view.findViewById(R.id.library_file_item_title);
		TextView length = (TextView)view.findViewById(R.id.library_file_item_length);
		
		// set title
		String s = file.getTitle().isEmpty() ? file.getName() : file.getTitle();
		title.setText(s);
		
		// set length
		length.setText(TimeFormatter.format(file.getLength()));
		
		return view;
	}

}
