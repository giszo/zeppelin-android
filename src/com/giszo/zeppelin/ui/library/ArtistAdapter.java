package com.giszo.zeppelin.ui.library;

import java.util.ArrayList;
import java.util.List;

import com.giszo.zeppelin.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ArtistAdapter extends BaseAdapter {
	private Context context;

	private List<Artist> artists = new ArrayList<Artist>();

	public ArtistAdapter(Context context) {
		this.context = context;
	}
	
	public void add(Artist artist) {
		artists.add(artist);
	}
	
	public void clear() {
		artists.clear();
	}

	@Override
	public int getCount() {
		return artists.size();
	}

	@Override
	public Object getItem(int position) {
		return artists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Artist artist = artists.get(position);

		View view = LayoutInflater.from(context).inflate(R.layout.library_artist_item, null);

		TextView name = (TextView)view.findViewById(R.id.library_artist_item_name);
		TextView description = (TextView)view.findViewById(R.id.library_artist_item_description);
		
		// set name
		name.setText(artist.getId() == -1 ? context.getResources().getString(R.string.unknown_artist) : artist.getName());
		
		// set description
		StringBuilder sb = new StringBuilder();
		sb.append(context.getResources().getQuantityString(R.plurals.number_of_albums, artist.getAlbums(), artist.getAlbums()));
		sb.append(", ");
		sb.append(context.getResources().getQuantityString(R.plurals.number_of_songs, artist.getSongs(), artist.getSongs()));
		description.setText(sb.toString());
		
		return view;
	}

}
