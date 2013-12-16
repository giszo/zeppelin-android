package com.giszo.zeppelin.ui.settings;

import com.giszo.zeppelin.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	private EditText serverAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create the view
		setContentView(R.layout.activity_settings);
		
		serverAddress = (EditText)findViewById(R.id.settings_server_address);
		
		load();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		save();
	}
	
	private void load() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		serverAddress.setText(sharedPreferences.getString("server_address", ""));
	}
	
	private void save() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString("server_address", serverAddress.getText().toString());
		editor.commit();
	}
}
