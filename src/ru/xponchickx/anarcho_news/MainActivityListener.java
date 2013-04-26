package ru.xponchickx.anarcho_news;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivityListener implements OnClickListener {
	Intent callingIntent;
	Activity context;
	SlidingMenu menu;
	
	public MainActivityListener(Activity context, SlidingMenu menu) {
		this.context = context;
		this.menu = menu;
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == (R.id.news_button)) {
			callingIntent = new Intent(context, NewsListActivity.class);
			callingIntent.putExtra("url", ANApplication.NEWS_BY_CATEGORY);
			context.startActivity(callingIntent);
			context.finish();
		} else if (id == (R.id.articles_button)) {
			callingIntent = new Intent(context, NewsListActivity.class);
			callingIntent.putExtra("url", ANApplication.THEORY);
			callingIntent.putExtra("title", "Теория");
			context.startActivity(callingIntent);
			context.finish();
		} else if (id == (R.id.books_button)) {
			callingIntent = new Intent(context, LiteratureActivity.class);
			context.startActivity(callingIntent);
			context.finish();
		} else if (id == (R.id.doings_button)) {
			callingIntent = new Intent(context, DoingsActivity.class);
			context.startActivity(callingIntent);
			context.finish();
		} else if (id == (R.id.settings_button)) {
			callingIntent = new Intent(context, SetupActivity.class);
			context.startActivity(callingIntent);
		}
	}
}
