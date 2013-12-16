package com.giszo.zeppelin.ui.main;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.ui.library.File;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QueueItem extends LinearLayout {
	private TextView name;

	public QueueItem(Context context) {
		this(context, null);
	}
	
	public QueueItem(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public QueueItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		LayoutInflater.from(context).inflate(R.layout.queue_item, this);
		
		name = (TextView)findViewById(R.id.queue_item_name);
	}
	
	public void fill(File file, boolean played) {
		String s = file.getTitle().isEmpty() ? file.getName() : file.getTitle();

		name.setText(s);
		name.setTypeface(null, played ? Typeface.BOLD : Typeface.NORMAL);
	}
}
