package com.giszo.zeppelin.ui.controls;

import com.giszo.zeppelin.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlaybackIndicator extends LinearLayout {
	private TextView playTime;
	private TextView trackTime;
	
	private ProgressBar progress;

	public PlaybackIndicator(Context context) {
		this(context, null);
	}
	
	public PlaybackIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}
	
	public PlaybackIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		LayoutInflater.from(context).inflate(R.layout.playback_indicator, this);
		
		playTime = (TextView)findViewById(R.id.playback_indicator_playtime);
		trackTime = (TextView)findViewById(R.id.playback_indicator_tracktime);
		progress = (ProgressBar)findViewById(R.id.playback_indicator_progress);

		// initialize default values :)
		set(0, 0);
	}
	
	public void set(int position, int length) {
		playTime.setText(String.format("%02d:%02d", position / 60, position % 60));
		trackTime.setText(String.format("%02d:%02d", length / 60, length % 60));
		
		if (length == 0)
			progress.setProgress(0);
		else
			progress.setProgress(Math.min(position * 100 / length, 100));
	}
}
