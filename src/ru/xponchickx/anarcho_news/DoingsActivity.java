package ru.xponchickx.anarcho_news;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class DoingsActivity extends Activity {
	String url;
	SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
	SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
	SimpleDateFormat fullFormat = new SimpleDateFormat("d MMMM", new Locale("ru"));
	Button chooseDate;
	Date date;
	TextView displayDate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doings_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		date = new Date();
		url = ANApplication.DOINGS + dayFormat.format(date) + "." + monthFormat.format(date);
		displayDate = (TextView) findViewById(R.id.display_date);
		chooseDate = (Button) findViewById(R.id.choose_date);
		displayDate.setText(fullFormat.format(date));
		chooseDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chooseDateIntent = new Intent(DoingsActivity.this, ChooseDateDialogActivity.class);
				startActivityForResult(chooseDateIntent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, data);
			int day = data.getIntExtra("day", 1);
			int month = data.getIntExtra("month", 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
			try {
				date = sdf.parse(String.valueOf(day) + "-" + String.valueOf(month));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			url = ANApplication.DOINGS + dayFormat.format(date) + "." + monthFormat.format(date);
			displayDate.setText(fullFormat.format(date));
		}
	}
}