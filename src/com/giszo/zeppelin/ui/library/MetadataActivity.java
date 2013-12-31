package com.giszo.zeppelin.ui.library;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.service.Service;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MetadataActivity extends Activity {
	private int id;

	private TextView filename;
	private EditText artist;
	private EditText album;
	private EditText title;
	private EditText year;
	private EditText trackIndex;
	private TextView samplingRate;
	private TextView audioCodec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_metadata);
		
		filename = (TextView)findViewById(R.id.metadata_filename);
		artist = (EditText)findViewById(R.id.metadata_artist);
		album = (EditText)findViewById(R.id.metadata_album);
		title = (EditText)findViewById(R.id.metadata_title);
		year = (EditText)findViewById(R.id.metadata_year);
		trackIndex = (EditText)findViewById(R.id.metadata_track_index);
		samplingRate = (TextView)findViewById(R.id.metadata_sampling_rate);
		audioCodec = (TextView)findViewById(R.id.metadata_audio_codec);

		Intent intent = getIntent();
		
		id = intent.getIntExtra("id", -1);
		filename.setText(intent.getStringExtra("name"));
		if (intent.hasExtra("artist"))
			artist.setText(intent.getStringExtra("artist"));
		if (intent.hasExtra("album"))
			album.setText(intent.getStringExtra("album"));
		title.setText(intent.getStringExtra("title"));
		year.setText(Integer.toString(intent.getIntExtra("year", 0)));
		trackIndex.setText(Integer.toString(intent.getIntExtra("trackIndex", -1)));
		samplingRate.setText(Integer.valueOf(intent.getIntExtra("samplingRate", 0)) + "Hz");
		audioCodec.setText(formatAudioCodec(File.Type.values()[intent.getIntExtra("codec", 0)]));
	}

	@Override
	protected void onDestroy() {
		// update the metadata before destroying the activity
		Intent intent = new Intent(this, Service.class);
		intent.putExtra("action", "library_update_metadata");
		intent.putExtra("id", id);
		intent.putExtra("artist", artist.getText().toString());
		intent.putExtra("album", album.getText().toString());
		intent.putExtra("title", title.getText().toString());
		intent.putExtra("year", Integer.parseInt(year.getText().toString()));
		intent.putExtra("trackIndex", Integer.parseInt(trackIndex.getText().toString()));
		startService(intent);

		super.onDestroy();
	}

	private String formatAudioCodec(File.Type codec) {
		switch (codec) {
		case MP3 : return getResources().getString(R.string.type_mp3);
		case FLAC : return getResources().getString(R.string.type_flac);
		default : return getResources().getString(R.string.type_unknown);
		}
	}
}
