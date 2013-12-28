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

public class AlbumAdapter extends ArrayAdapter<Album> {
	private List<Album> albums;
	
	public AlbumAdapter(Context context) {
		super(context, R.layout.library_album_item);
	}

	public void set(List<Album> albums) {
		this.albums = albums;
		clear();
		addAll(albums);
	}

	@Override
	public Filter getFilter() {
		return new AlbumFilter();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		Album album = getItem(position);

		View view = LayoutInflater.from(getContext()).inflate(R.layout.library_album_item, null);

		TextView name = (TextView)view.findViewById(R.id.library_album_item_name);
		TextView description = (TextView)view.findViewById(R.id.library_album_item_descriptipn);

		// set name
		name.setText(album.getName());

		// set description
		StringBuilder sb = new StringBuilder();
		sb.append(getContext().getResources().getQuantityString(R.plurals.number_of_songs, album.getSongs(), album.getSongs()));
		sb.append(", ");
		sb.append(TimeFormatter.format(album.getLength()));
		description.setText(sb.toString());
		
		return view;
	}

	private class AlbumFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			String filter = constraint.toString().toLowerCase();
			FilterResults res = new FilterResults();
			
			if (constraint == null || constraint.length() == 0) {
				res.values = albums;
				res.count = albums.size();
			} else {
				List<Album> filtered = new ArrayList<Album>();
				
				for (Album album : albums) {
					if (album.getName().toLowerCase().contains(filter))
						filtered.add(album);
				}
				
				res.values = filtered;
				res.count = filtered.size();
			}
			
			return res;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			clear();
			addAll((List<Album>)results.values);
			notifyDataSetChanged();
		}
	}
}
