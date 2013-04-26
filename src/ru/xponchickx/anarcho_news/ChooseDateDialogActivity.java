package ru.xponchickx.anarcho_news;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChooseDateDialogActivity extends Activity {
	// Scrolling flag
	private boolean scrolling = false;
	Integer pDay = 1;
	Integer pMonth = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_date_dialog);

		final WheelView month = (WheelView) findViewById(R.id.month);
		final WheelView day = (WheelView) findViewById(R.id.day);
		month.setVisibleItems(3);

		final String days[][] = new String[][] {
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30", "31" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30", "31" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30", "31" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30", "31" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30", "31" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30", "31" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30" },
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
						"9", "10", "11", "12", "13", "14", "15", "16", "17",
						"18", "19", "20", "21", "22", "23", "24", "25", "26",
						"27", "28", "29", "30", "31" } };

		Button viewDate = (Button) findViewById(R.id.view_date);
		Button cancel = (Button) findViewById(R.id.cancel);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		viewDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra("day", day.getCurrentItem() + 1);
				i.putExtra("month", month.getCurrentItem() + 1);

				setResult(RESULT_OK, i);
				finish();
			}
		});

		final String months[] = getResources().getStringArray(R.array.months);
		day.setVisibleItems(5);

		month.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateCities(day, days, newValue);
				}
			}
		});

		month.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateCities(day, days, month.getCurrentItem());
			}
		});

		month.setCurrentItem(1);
		month.setViewAdapter(new ArrayWheelAdapter<String>(this, months));
		day.setViewAdapter(new ArrayWheelAdapter<String>(this, days[0]));
	}

	/**
	 * Updates the city wheel
	 */
	private void updateCities(WheelView day, String days[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, days[index]);
		day.setViewAdapter(adapter);
		day.setCurrentItem(0);
	}
}