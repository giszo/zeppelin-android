package com.giszo.zeppelin.ui.library;

import java.util.ArrayList;
import java.util.List;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.utils.TimeFormatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<File> {
	private List<File> files = new ArrayList<File>();

	public FileAdapter(Context context) {
		super(context, R.layout.library_file_item);
	}
	
	public void add(File file) {
		super.add(file);
		files.add(file);
	}
	
	public void clearAll() {
		clear();
		files.clear();
	}

	@Override
	public Filter getFilter() {
		return new FileFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		File file = getItem(position);

		View view = LayoutInflater.from(getContext()).inflate(R.layout.library_file_item, null);

		TextView title = (TextView)view.findViewById(R.id.library_file_item_title);
		TextView length = (TextView)view.findViewById(R.id.library_file_item_length);
		
		// set title
		String s = file.getTitle().isEmpty() ? file.getName() : file.getTitle();
		title.setText(s);
		
		// set length
		length.setText(TimeFormatter.format(file.getLength()));
		
		return view;
	}


	private class FileFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			String filter = constraint.toString().toLowerCase();
			FilterResults res = new FilterResults();
			
			if (constraint == null || constraint.length() == 0) {
				res.values = files;
				res.count = files.size();
			} else {
				List<File> filtered = new ArrayList<File>();
				
				for (File file : files) {
					if (file.getTitle().toLowerCase().contains(filter))
						filtered.add(file);
				}
				
				res.values = filtered;
				res.count = filtered.size();
			}
			
			return res;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			clear();
			addAll((List<File>)results.values);
			notifyDataSetChanged();
		}
	}
}
