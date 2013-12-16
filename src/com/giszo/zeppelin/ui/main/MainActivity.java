package com.giszo.zeppelin.ui.main;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.service.Service;
import com.giszo.zeppelin.ui.controls.PlaybackIndicator;
import com.giszo.zeppelin.ui.library.File;
import com.giszo.zeppelin.ui.library.LibraryActivity;
import com.giszo.zeppelin.ui.settings.SettingsActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {
	private ListView listView;
	private QueueAdapter adapter = new QueueAdapter(this);

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
		Intent intent = new Intent(this, Service.class);
		intent.putExtra("action", "player_queue_get");
		startService(intent);
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
		Intent intent = new Intent(this, Service.class);
		intent.putExtra("action", "player_goto");
		intent.putExtra("index", pos);
		startService(intent);
	}

	private class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (action.equals("player_queue_downloaded")) {
				adapter.clear();
				
				try {
					JSONArray list = new JSONArray(intent.getStringExtra("queue"));

					for (int i = 0; i < list.length(); ++i) {
						JSONObject item = list.getJSONObject(i);
						adapter.add(new File(
							item.getInt("id"),
							item.getString("name"),
							item.getString("title"),
							item.getInt("length")));
					}
				} catch (JSONException e) {
					return;
				}

				adapter.notifyDataSetChanged();
			} else if (action.equals("player_status_downloaded")) {
				try {
					JSONObject status = new JSONObject(intent.getStringExtra("status"));
					
					// playing flag
					playing.set(status.getInt("state") == PlayerState.PLAYING.ordinal());
					updatePlayPause();
						
					int currentFile = status.isNull("current") ? -1 : status.getInt("current");
					
					adapter.setCurrent(currentFile);
					
					if (currentFile == -1)
						indicator.set(0, 0);
					else
						indicator.set(status.getInt("position"), adapter.getFile(currentFile).getLength());
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
	
	private void updatePlayPause() {
		if (playing.get())
			playPause.setImageResource(R.drawable.pause);
		else
			playPause.setImageResource(R.drawable.play);
	}
}
