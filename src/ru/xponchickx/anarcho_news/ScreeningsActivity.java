package ru.xponchickx.anarcho_news;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class ScreeningsActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screenings_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}
