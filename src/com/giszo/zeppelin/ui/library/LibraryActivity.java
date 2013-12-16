package com.giszo.zeppelin.ui.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.service.Service;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LibraryActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "LibraryActivity";

	@SuppressLint("UseSparseArrays")
	private Map<Integer, List<Album>> albumMap = new HashMap<Integer, List<Album>>();
	
	private enum State { ARTISTS, ALBUMS, FILES, FILES_UNKNOWN };
	private State state = State.ARTISTS;
	
	private ListView listView;

	private ArtistAdapter artistAdapter = new ArtistAdapter(this);
	private AlbumAdapter albumAdapter = new AlbumAdapter(this);
	private FileAdapter fileAdapter = new FileAdapter(this);

	private Receiver receiver = new Receiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// initialize the view
		setContentView(R.layout.activity_library);
		
		listView = (ListView)findViewById(R.id.library_list);
		listView.setAdapter(artistAdapter);
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView);

		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		lbm.registerReceiver(receiver, new IntentFilter("artist_list_received"));
		lbm.registerReceiver(receiver, new IntentFilter("album_list_received"));
		lbm.registerReceiver(receiver, new IntentFilter("files_of_artist_received"));
		lbm.registerReceiver(receiver, new IntentFilter("files_of_album_received"));
		
		// ask the service to load the list of artists
		Intent intent = new Intent(this, Service.class);
		intent.putExtra("action", "library_get_artists");
		startService(intent);
		
		// ... and load the list of albums as well
		intent = new Intent(this, Service.class);
		intent.putExtra("action", "library_get_albums");
		startService(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.library, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_library_scan : {
			Intent intent = new Intent(this, Service.class);
			intent.putExtra("action", "library_scan");
			startService(intent);
			return true;
		}

		default :
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		switch (state) {
		case ARTISTS :
			break;
			
		case ALBUMS : {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.album, menu);
			break;
		}
			
		case FILES :
		case FILES_UNKNOWN : {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.file, menu);
			break;
		}

		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_queue_album : {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			Album album = (Album)albumAdapter.getItem(info.position);
			
			// queue the album
			Intent intent = new Intent(this, Service.class);
			intent.putExtra("action", "player_queue_album");
			intent.putExtra("id", album.getId());
			startService(intent);
			
			// refresh the main queue
			refreshQueue();
			
			return true;
		}

		case R.id.action_queue_file : {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			File file = (File)fileAdapter.getItem(info.position);
			
			// queue the file
			Intent intent = new Intent(this, Service.class);
			intent.putExtra("action", "player_queue_file");
			intent.putExtra("id", file.getId());
			startService(intent);
			
			// refresh the main queue
			refreshQueue();

			return true;
		}

		default :
			return super.onContextItemSelected(item);
		}
	}

	private class Receiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (action.equals("artist_list_received")) {
				try {
					artistAdapter.clear();

					JSONArray list = new JSONArray(intent.getStringExtra("artists"));
					
					for (int i = 0; i < list.length(); ++i) {
						JSONObject item = list.getJSONObject(i);
						artistAdapter.add(new Artist(
							item.getInt("id"),
							item.getString("name"),
							item.getInt("albums"),
							item.getInt("songs")));
					}
					
					Log.d(TAG, "Got " + artistAdapter.getCount() + " artists");
				} catch (JSONException e) {
				}
				
				artistAdapter.notifyDataSetChanged();
			} else if (action.equals("album_list_received")) {
				try {
					albumMap.clear();
					
					JSONArray list = new JSONArray(intent.getStringExtra("albums"));
					
					for (int i = 0; i < list.length(); ++i) {
						JSONObject item = list.getJSONObject(i);
						Integer artist = Integer.valueOf(item.getInt("artist"));
						
						List<Album> albums = albumMap.get(artist);
						
						if (albums == null) {
							albums = new ArrayList<Album>();
							albumMap.put(artist, albums);
						}
						
						albums.add(new Album(
							item.getInt("id"),
							item.getString("name"),
							item.getInt("songs"),
							item.getInt("length")));
					}
					
					Log.d(TAG, "Got " + list.length() + " albums");
				} catch (JSONException e) {
				}
			} else if (action.equals("files_of_artist_received")) {
				try {
					buildFileList(new JSONArray(intent.getStringExtra("files")));
				} catch (JSONException e) {
					return;
				}

				listView.setAdapter(fileAdapter);

				state = State.FILES_UNKNOWN;
			} else if (action.equals("files_of_album_received")) {
				try {
					buildFileList(new JSONArray(intent.getStringExtra("files")));
				} catch (JSONException e) {
					return;
				}

				listView.setAdapter(fileAdapter);

				state = State.FILES;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		switch (state) {
		case ARTISTS : {
			Artist artist = (Artist)artistAdapter.getItem(pos);
			
			// handle unknown artist differently
			if (artist.getId() == -1) {
				Intent intent = new Intent(this, Service.class);
				intent.putExtra("action", "library_get_files_of_artist");
				intent.putExtra("id", -1);
				startService(intent);
				return;
			}
			
			List<Album> albums = albumMap.get(Integer.valueOf(artist.getId()));
			
			if (albums == null)
				return;
			
			albumAdapter.set(albums);
			listView.setAdapter(albumAdapter);
			
			state = State.ALBUMS;

			break;
		}
			
		case ALBUMS : {
			Album album = (Album)albumAdapter.getItem(pos);
			
			Intent intent = new Intent(this, Service.class);
			intent.putExtra("action", "library_get_files_of_album");
			intent.putExtra("id", album.getId());
			startService(intent);
			
			break;
		}
			
		case FILES :
		case FILES_UNKNOWN :
			break;
		}
	}

	@Override
	public void onBackPressed() {
		switch (state) {
		case ARTISTS :
			super.onBackPressed();
			break;

		case ALBUMS :
			listView.setAdapter(artistAdapter);
			state = State.ARTISTS;
			break;
			
		case FILES :
			listView.setAdapter(albumAdapter);
			state = State.ALBUMS;
			break;
			
		case FILES_UNKNOWN :
			listView.setAdapter(artistAdapter);
			state = State.ARTISTS;
			break;
		}
	}
	
	private void refreshQueue() {
		Intent intent = new Intent(this, Service.class);
		intent.putExtra("action", "player_queue_get");
		startService(intent);
	}
	
	private void buildFileList(JSONArray list) throws JSONException {
		fileAdapter.clear();
		
		for (int i = 0; i < list.length(); ++i) {
			JSONObject item = list.getJSONObject(i);

			fileAdapter.add(new File(
				item.getInt("id"),
				item.getString("name"),
				item.getString("title"),
				item.getInt("length")));
		}
	}
}
