package com.giszo.zeppelin.service;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class Service extends IntentService {
	private static final String TAG = "ZeppelinCtrlService";

	public Service() {
		super("zeppelin-ctrl-worker");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!intent.hasExtra("action")) {
			Log.e(TAG, "no action in intent");
			return;
		}
		
		String action = intent.getStringExtra("action");
		
		if (action.equals("library_scan"))
			libraryScan();
		else if (action.equals("library_get_artists"))
			libraryGetArtists();
		else if (action.equals("library_get_albums"))
			libraryGetAlbums();
		else if (action.equals("library_get_files_of_artist"))
			libraryGetFilesOfArtist(intent.getIntExtra("id", -1));
		else if (action.equals("library_get_files_of_album"))
			libraryGetFilesOfAlbum(intent.getIntExtra("id", -1));
		else if (action.equals("player_queue_get"))
			playerQueueGet();
		else if (action.equals("player_queue_file"))
			playerQueueFile(intent.getIntExtra("id", -1));
		else if (action.equals("player_queue_album"))
			playerQueueAlbum(intent.getIntExtra("id", -1));
		else if (action.equals("player_status"))
			playerStatus();
		else if (action.equals("player_play"))
			playerPlay();
		else if (action.equals("player_pause"))
			playerPause();
		else if (action.equals("player_stop"))
			playerStop();
		else if (action.equals("player_prev"))
			playerPrev();
		else if (action.equals("player_next"))
			playerNext();
		else if (action.equals("player_goto"))
			playerGoto(intent.getIntExtra("index", -1));
		else if (action.equals("player_inc_volume"))
			playerIncVolume();
		else if (action.equals("player_dec_volume"))
			playerDecVolume();
	}

	private void libraryScan() {
		execute("library_scan", null);
	}

	private void libraryGetArtists() {
		JSONObject resp = execute("library_get_artists", null);
		
		if (resp == null)
			return;

		try
		{
			Intent intent = new Intent("artist_list_received");
			intent.putExtra("artists", resp.getJSONArray("result").toString());

			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		catch (JSONException e)
		{
		}
	}
	
	private void libraryGetAlbums() {
		JSONObject resp = execute("library_get_albums", null);
		
		if (resp == null)
			return;

		try
		{
			Intent intent = new Intent("album_list_received");
			intent.putExtra("albums", resp.getJSONArray("result").toString());

			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		catch (JSONException e)
		{
		}		
	}
	
	private void libraryGetFilesOfArtist(int id) {
		JSONObject params;
		
		try
		{
			params = new JSONObject();
			params.put("artist_id", id);
		}
		catch (JSONException e)
		{
			return;
		}
		
		JSONObject resp = execute("library_get_files_of_artist", params);
		
		if (resp == null)
			return;
		
		try
		{
			Intent intent = new Intent("files_of_artist_received");
			intent.putExtra("files", resp.getJSONArray("result").toString());
			
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		catch (JSONException e)
		{
		}		
	}

	private void libraryGetFilesOfAlbum(int id) {
		if (id == -1)
			return;
		
		JSONObject params;
		
		try
		{
			params = new JSONObject();
			params.put("album_id", id);
		}
		catch (JSONException e)
		{
			return;
		}
		
		JSONObject resp = execute("library_get_files_of_album", params);
		
		if (resp == null)
			return;
		
		try
		{
			Intent intent = new Intent("files_of_album_received");
			intent.putExtra("files", resp.getJSONArray("result").toString());
			
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		catch (JSONException e)
		{
		}
	}

	private void playerQueueGet() {
		JSONObject resp = execute("player_queue_get", null);
		
		if (resp == null)
			return;

		try
		{
			Intent intent = new Intent("player_queue_downloaded");
			intent.putExtra("queue", resp.getJSONArray("result").toString());

			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		catch (JSONException e)
		{
		}
	}

	private void playerQueueFile(int id) {
		if (id == -1)
			return;
		
		JSONObject params;
		
		try
		{
			params = new JSONObject();
			params.put("id", id);
		}
		catch (JSONException e)
		{
			return;
		}
		
		execute("player_queue_file", params);		
	}

	private void playerQueueAlbum(int id) {
		if (id == -1)
			return;
		
		JSONObject params;
		
		try
		{
			params = new JSONObject();
			params.put("id", id);
		}
		catch (JSONException e)
		{
			return;
		}
		
		execute("player_queue_album", params);
	}

	private void playerStatus() {
		JSONObject resp = execute("player_status", null);
		
		if (resp == null)
			return;
		
		try
		{
			Intent intent = new Intent("player_status_downloaded");
			intent.putExtra("status", resp.getJSONObject("result").toString());
			
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		catch (JSONException e)
		{
		}
	}

	private void playerPlay() {
		execute("player_play", null);
	}

	private void playerPause() {
		execute("player_pause", null);
	}

	private void playerStop() {
		execute("player_stop", null);
	}
	
	private void playerPrev() {
		execute("player_prev", null);
	}
	
	private void playerNext() {
		execute("player_next", null);
	}

	private void playerGoto(int index) {
		if (index == -1)
			return;
		
		
		JSONObject params;
		
		try
		{
			params = new JSONObject();
			params.put("index", index);
		}
		catch (JSONException e)
		{
			return;
		}
		
		execute("player_goto", params);
	}
	
	private void playerIncVolume() {
		execute("player_inc_volume", null);
	}
	
	private void playerDecVolume() {
		execute("player_dec_volume", null);
	}

	private JSONObject execute(String method, Object params) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String server = sharedPreferences.getString("server_address", "");
		
		if (server.isEmpty())
			return null;
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(server);		

		try
		{
			JSONObject req = new JSONObject();
			req.put("jsonrpc", "2.0");
			req.put("id", 1);
			req.put("method", method);
			if (params != null)
				req.put("params", params);
			else
				req.put("params", JSONObject.NULL);

			Log.d(TAG, "JSON request: " + req.toString());
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			post.setEntity(new StringEntity(req.toString()));
			String response = client.execute(post, responseHandler);
			return new JSONObject(response);
		}
		catch (JSONException e)
		{
			return null;
		}
		catch (IOException e)
		{
			return null;
		}
		catch (IllegalStateException e)
		{
			return null;
		}
	}
}
