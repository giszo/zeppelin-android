package com.giszo.zeppelin.ui.main;

import com.giszo.zeppelin.R;
import com.giszo.zeppelin.ui.library.Album;
import com.giszo.zeppelin.ui.library.File;
import com.giszo.zeppelin.utils.TimeFormatter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QueueItem extends LinearLayout {
	private View container;
	private ImageView image;
	private TextView name;
	private TextView description;

	public QueueItem(Context context) {
		this(context, null);
	}
	
	public QueueItem(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public QueueItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		LayoutInflater.from(context).inflate(R.layout.queue_item, this);

		container = findViewById(R.id.queue_item_container);
		image = (ImageView)findViewById(R.id.queue_item_image);
		name = (TextView)findViewById(R.id.queue_item_name);
		description = (TextView)findViewById(R.id.queue_item_description);
	}
	
	public void fill(com.giszo.zeppelin.queue.QueueItem item, boolean played) {
		// set left padding based on the depth of the current item
		setPadding(item.getDepth() * 40, 0, 0, 0);

		if (item instanceof com.giszo.zeppelin.queue.File) {
			File file = ((com.giszo.zeppelin.queue.File)item).getFile();
			String s = file.getTitle().isEmpty() ? file.getName() : file.getTitle();

			image.setImageResource(R.drawable.song);
			name.setText(s);
			description.setText(TimeFormatter.format(file.getLength()));
		} else if (item instanceof com.giszo.zeppelin.queue.Album) {
			com.giszo.zeppelin.queue.Album qa = (com.giszo.zeppelin.queue.Album)item;
			Album album = qa.getAlbum();

			image.setImageResource(R.drawable.album);
			name.setText(album.getName());
			description.setText(
				getResources().getQuantityString(
					R.plurals.number_of_songs,
					qa.getSongCount(),
					qa.getSongCount()));
		} else if (item instanceof com.giszo.zeppelin.queue.Directory) {
			com.giszo.zeppelin.queue.Directory directory = (com.giszo.zeppelin.queue.Directory)item;

			image.setImageResource(R.drawable.folder);
			name.setText(directory.getName());
			description.setText(
					getResources().getQuantityString(
						R.plurals.number_of_songs,
						directory.getSongCount(),
						directory.getSongCount()));
		}
		
		container.setBackgroundColor(played ? 0x800099cc : 0x00000000);
	}
}
