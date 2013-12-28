package com.giszo.zeppelin.ui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.queue.File;
import com.giszo.zeppelin.queue.Album;
import com.giszo.zeppelin.queue.ContainerQueueItem;
import com.giszo.zeppelin.service.Service;
import com.giszo.zeppelin.ui.controls.PlaybackIndicator;
import com.giszo.zeppelin.ui.library.LibraryActivity;
import com.giszo.zeppelin.ui.settings.SettingsActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {
	private ListView listView;
	private QueueAdapter adapter = new QueueAdapter(this);
	private Map<Integer, com.giszo.zeppelin.ui.library.File> fileMap = new HashMap<Integer, com.giszo.zeppelin.ui.library.File>();

	private PlaybackIndicator indicator;
	
	private ImageView playPause;
	
	private PollTask refreshTask;
	private Timer refreshTimer;

	private AtomicBoolean playing = new AtomicBoolean(false);
	
	private Receiver receiver = new Receiver();
	
	private enum PlayerState { STOPPED, PLAYING, PAUSED };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// initialize the view
		setContentView(R.layout.activity_main);
		
		listView = (ListView)findViewById(R.id.main_queue);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView);
		
		indicator = (PlaybackIndicator)findViewById(R.id.main_indicator);

		// play/pause button
		playPause = (ImageView)findViewById(R.id.main_play_pause); 
		playPause.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, Service.class);
					if (playing.get())
						intent.putExtra("action", "player_pause");
					else
						intent.putExtra("action", "player_play");
					MainActivity.this.startService(intent);
					
					// negate the playing flag
					playing.set(!playing.get());
					// update the play/pause button
					updatePlayPause();
					
					// perform a poll event immediately to refresh the controls
					doPoll();
				}
			});

		// stop button
		findViewById(R.id.main_stop).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, Service.class);
						intent.putExtra("action", "player_stop");
						MainActivity.this.startService(intent);
						
						// perform a poll event immediately to refresh the controls
						doPoll();
					}
				});

		// prev button
		findViewById(R.id.main_prev).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, Service.class);
						intent.putExtra("action", "player_prev");
						MainActivity.this.startService(intent);
						
						// perform a poll event immediately to refresh the controls
						doPoll();
					}
				});

		// next button
		findViewById(R.id.main_next).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, Service.class);
						intent.putExtra("action", "player_next");
						MainActivity.this.startService(intent);
						
						// perform a poll event immediately to refresh the controls
						doPoll();
					}
				});

		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		lbm.registerReceiver(receiver, new IntentFilter("player_queue_downloaded"));
		lbm.registerReceiver(receiver, new IntentFilter("player_status_downloaded"));

		// ask the service to load the player queue
		refreshQueue();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// continue polling
		refreshTask = new PollTask();
		refreshTimer = new Timer();
		refreshTimer.scheduleAtFixedRate(refreshTask, 0, 1000);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// stop polling while the activity is not active
		refreshTimer.cancel();
		refreshTimer = null;
		refreshTask = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_library : {
			Intent intent = new Intent(this, LibraryActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.action_settings : {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.main_queue, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_main_queue_remove : {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo();
			com.giszo.zeppelin.queue.QueueItem item = (com.giszo.zeppelin.queue.QueueItem)adapter.getItem(info.position);
			
			if (item != null) {
				List<Integer> index = new ArrayList<Integer>();
				item.indexOf(index, null);
				
				Intent intent = new Intent(this, Service.class);
				intent.putExtra("action", "player_queue_remove");
				intent.putExtra("index", new JSONArray(index).toString());
				startService(intent);

				// refresh the queue
				refreshQueue();
				
				// poll immediately to update the current file in the queue
				doPoll();
			}

			return true;
		}
		default :
			return super.onContextItemSelected(menuItem);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP : {
			Intent intent = new Intent(this, Service.class);
			intent.putExtra("action", "player_inc_volume");
			startService(intent);
			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_DOWN : {
			Intent intent = new Intent(this, Service.class);
			intent.putExtra("action", "player_dec_volume");
			startService(intent);
			return true;
		}
		default :
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		com.giszo.zeppelin.queue.QueueItem item = (com.giszo.zeppelin.queue.QueueItem)adapter.getItemAtPosition(pos);
		
		if (item == null)
			return;
		
		if (item instanceof File) {
			List<Integer> idx = new ArrayList<Integer>();
			item.indexOf(idx, null);

			Intent intent = new Intent(this, Service.class);
			intent.putExtra("action", "player_goto");
			intent.putExtra("index", new JSONArray(idx).toString());
			startService(intent);

			// poll immediately to update the current file in the queue
			doPoll();
		} else if (item instanceof Album) {
			((Album)item).toggleVisibility();
			MainActivity.this.adapter.notifyDataSetChanged();
		}
	}

	private class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (action.equals("player_queue_downloaded")) {
				try {
					updateQueue(new JSONArray(intent.getStringExtra("queue")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals("player_status_downloaded")) {
				try {
					JSONObject status = new JSONObject(intent.getStringExtra("status"));
					
					// playing flag
					playing.set(status.getInt("state") == PlayerState.PLAYING.ordinal());
					updatePlayPause();
						
					int currentFile = status.isNull("current") ? -1 : status.getInt("current");
					
					// update queue
					adapter.setCurrent(currentFile);
					adapter.notifyDataSetChanged();

					// update indicator
					if (currentFile == -1)
						indicator.set(0, 0);
					else {
						com.giszo.zeppelin.ui.library.File file = fileMap.get(Integer.valueOf(currentFile));
						indicator.set(status.getInt("position"), file == null ? 0 : file.getLength());
					}
				} catch (JSONException e) {
				}
			}
		}	
	}
	
	private class PollTask extends TimerTask {
		@Override
		public void run() {
			doPoll();
		}
	}

	private void doPoll() {
		Intent intent = new Intent(MainActivity.this, Service.class);
		intent.putExtra("action", "player_status");
		startService(intent);
	}

	private void refreshQueue() {
		Intent intent = new Intent(this, Service.class);
		intent.putExtra("action", "player_queue_get");
		startService(intent);
	}

	private void updateQueueLevel(int depth, Map<Integer, com.giszo.zeppelin.ui.library.File> fileMap, ContainerQueueItem parent, JSONArray items) throws JSONException {
		for (int i = 0; i < items.length(); ++i) {
			JSONObject item = items.getJSONObject(i);

			int type = item.getInt("type");
			
			switch (type) {
			case 0 :
				// playlist
				break;
				
			case 1 :
				// album
				Album album = new Album(parent, depth, new com.giszo.zeppelin.ui.library.Album(
					item.getInt("id"),
					item.getString("name"),
					0,
					0));
				updateQueueLevel(depth + 1, fileMap, album, item.getJSONArray("files"));
				parent.add(album);
				
				break;
				
			case 2 : {
				// file
				com.giszo.zeppelin.ui.library.File file = new com.giszo.zeppelin.ui.library.File(
					item.getInt("id"),
					item.getString("name"),
					item.getString("title"),
					item.getInt("length"));
				fileMap.put(Integer.valueOf(file.getId()), file);
				parent.add(new File(parent, depth, file));

				break;
			}
			}	
		}
	}

	private void updateQueue(JSONArray queue) throws JSONException {
		ContainerQueueItem root = new ContainerQueueItem(null /* no parent */, 0 /* does not really matter */);
		fileMap.clear();

		updateQueueLevel(0, fileMap, root, queue);

		adapter.setRoot(root);
		adapter.notifyDataSetChanged();
	}

	private void updatePlayPause() {
		if (playing.get())
			playPause.setImageResource(R.drawable.pause);
		else
			playPause.setImageResource(R.drawable.play);
	}
}
