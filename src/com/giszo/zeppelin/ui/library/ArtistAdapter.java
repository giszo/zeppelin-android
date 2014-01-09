package com.giszo.zeppelin.ui.library;

import java.util.ArrayList;
import java.util.List;

import com.giszo.zeppelin.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class ArtistAdapter extends ArrayAdapter<Artist> {
	private List<Artist> artists = new ArrayList<Artist>();

	public ArtistAdapter(Context context) {
		super(context, R.layout.library_artist_item);
	}
	
	public void add(Artist artist) {
		super.add(artist);
		artists.add(artist);
	}
	
	public void clearAll() {
		clear();
		artists.clear();
	}

	@Override
	public Filter getFilter() {
		return new ArtistFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Artist artist = getItem(position);

		View view = LayoutInflater.from(getContext()).inflate(R.layout.library_artist_item, null);

		TextView name = (TextView)view.findViewById(R.id.library_artist_item_name);
		TextView description = (TextView)view.findViewById(R.id.library_artist_item_description);
		
		// set name
		name.setText(artist.getId() == -1 ? getContext().getResources().getString(R.string.unknown_artist) : artist.getName());
		
		// set description
		description.setText(getContext().getResources().getQuantityString(R.plurals.number_of_albums, artist.getAlbums(), artist.getAlbums()));
		
		return view;
	}

	private class ArtistFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			String filter = constraint.toString().toLowerCase();
			FilterResults res = new FilterResults();
			
			if (constraint == null || constraint.length() == 0) {
				res.values = artists;
				res.count = artists.size();
			} else {
				List<Artist> filtered = new ArrayList<Artist>();
				
				for (Artist artist : artists) {
					if (artist.getName().toLowerCase().contains(filter))
						filtered.add(artist);
				}
				
				res.values = filtered;
				res.count = filtered.size();
			}
			
			return res;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			clear();
			addAll((List<Artist>)results.values);
			notifyDataSetChanged();
		}
	}
}
