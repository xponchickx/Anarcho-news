package ru.xponchickx.anarcho_news;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

public class LiteratureAdapter extends ArrayAdapter<String> {
	public ImageLoader imageLoader;
	Activity context;
	ArrayList<String> data;
	ArrayList<String> images;
	private int mResource;
	private int mFieldId = 0;
	private LayoutInflater mInflater;

	public LiteratureAdapter(Context context, int resource,	int textViewResourceId, ArrayList<String> data, ArrayList<String> images) {
		super(context, resource, textViewResourceId, data);
		this.context = (Activity) context;
		this.data = data;
		this.images = images;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;
		mFieldId = textViewResourceId;
		imageLoader = new ImageLoader(context.getApplicationContext());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		TextView text;

		if (convertView == null) {
			view = mInflater.inflate(mResource, parent, false);
		} else {
			view = convertView;
		}

		try {
			if (mFieldId == 0) {
				text = (TextView) view;
			} else {
				text = (TextView) view.findViewById(mFieldId);
			}
		} catch (ClassCastException e) {
			Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
		}

		ImageView image = (ImageView) view.findViewById(R.id.literature_image);
		new ImageLoader(context).DisplayImage(images.get(position), image);
		String item = getItem(position);
		if (item instanceof CharSequence) {
			text.setText(item);
		} else {
			text.setText(item.toString());
		}

		return view;
	}
}