package com.fedorvlasov.lazylist;

import java.util.ArrayList;

import ru.xponchickx.anarcho_news.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<String> titels;
	private ArrayList<String> images;
	private ArrayList<String> dates;
	private ArrayList<String> views;
	private ArrayList<String> times;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<String> t, ArrayList<String> i, ArrayList<String> d, ArrayList<String> ti, ArrayList<String> v) {
		activity = a;
		titels = t;
		images = i;
		dates = d;
		times = ti;
		views = v;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		return titels.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.news_item_layout, null);
		}

		TextView titelText = (TextView) vi.findViewById(R.id.news_text);
		TextView dateText = (TextView) vi.findViewById(R.id.pub_date_text);
		TextView timeText = (TextView) vi.findViewById(R.id.pub_time_text);
		TextView viewsText = (TextView) vi.findViewById(R.id.views_text);
		ImageView image = (ImageView) vi.findViewById(R.id.news_image);
		ImageView imageBorder = (ImageView) vi.findViewById(R.id.image_border);
		titelText.setText(titels.get(position));
		dateText.setText(dates.get(position));
		viewsText.setText(views.get(position));
		timeText.setText(times.get(position));
		if (position % 2 == 1) {
			imageBorder.setBackgroundResource(android.R.color.black);
		} else {
			imageBorder.setBackgroundResource(android.R.color.white);
		}
		imageLoader.DisplayImage(images.get(position), image);
		return vi;
	}
}