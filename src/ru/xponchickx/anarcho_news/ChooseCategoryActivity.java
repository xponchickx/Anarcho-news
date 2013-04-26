package ru.xponchickx.anarcho_news;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ChooseCategoryActivity extends Activity {
	Activity context;
	ArrayAdapter<String> adapter;
	ListView chooseCategoryList;
	List<CategoryMessage> messages;
	Dialog exceptionDialog;
	String[] titels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_category_dialog);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		context = this;
		setTitle(getResources().getString(R.string.choose_category_string));
		chooseCategoryList = (ListView) findViewById(R.id.choose_category_list);
		new CategoriesAsyncTask().execute(ANApplication.CATEGORIES);
	}

	class CategoriesAsyncTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				messages = new SaxFeedParser(params[0]).categoriesParse();
				titels = new String[messages.size() + 1];
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return false;
			} catch (ConnectException e) {
				e.printStackTrace();
				return false;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (SAXException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return false;
			} catch (NullPointerException e) {
				e.printStackTrace();
				return false;
			}
			int i = 1;
			titels[0] = "Последние новости";
			for (CategoryMessage m : messages) {
					titels[i] = m.getTitle();
					++i;
				}
			adapter = new ArrayAdapter<String>(context,	android.R.layout.simple_list_item_1, titels);
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				ProgressBar loadingProgressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
				loadingProgressBar.setVisibility(View.GONE);
				chooseCategoryList.setAdapter(adapter);
				chooseCategoryList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Intent newsIntent = new Intent(context, NewsListActivity.class);
						if (position == 0) {
							newsIntent.putExtra("url", ANApplication.NEWS_BY_CATEGORY);
						} else {
							newsIntent.putExtra("url", ANApplication.NEWS_BY_CATEGORY + ANApplication.PLUS_ID + messages.get(position - 1).getId());
						}
						context.startActivity(newsIntent);
					}
				});
			} else {
				ErrorDialog err = new ErrorDialog(context);
				err.show();
			}
			super.onPostExecute(result);
		}
	}
}