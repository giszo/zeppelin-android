package com.giszo.zeppelin.ui.library;

import java.util.List;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.utils.TimeFormatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlbumAdapter extends BaseAdapter {
	private Context context;

	private List<Album> albums;
	
	public AlbumAdapter(Context context) {
		this.context = context;
	}

	public void set(List<Album> albums) {
		this.albums = albums;
	}
	
	@Override
	public int getCount() {
		if (albums == null)
			return 0;
		
		return albums.size();
	}

	@Override
	public Object getItem(int index) {
		if (albums == null)
			return null;
		
		return albums.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup group) {
		Album album = albums.get(index);

		View view = LayoutInflater.from(context).inflate(R.layout.library_album_item, null);

		TextView name = (TextView)view.findViewById(R.id.library_album_item_name);
		TextView description = (TextView)view.findViewById(R.id.library_album_item_descriptipn);

		// set name
		name.setText(album.getName());

		// set description
		StringBuilder sb = new StringBuilder();
		sb.append(context.getResources().getQuantityString(R.plurals.number_of_songs, album.getSongs(), album.getSongs()));
		sb.append(", ");
		sb.append(TimeFormatter.format(album.getLength()));
		description.setText(sb.toString());
		
		return view;
	}

}
