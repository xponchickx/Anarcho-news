package ru.xponchickx.anarcho_news;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class NewsActivity extends SherlockActivity {
	Intent NewsIntent;
	NewsAsyncTask NewsTask;
	String url;
	WebView newsDescription;
	TextView timeView, dateView, titelView, viewsView;
	ImageView imageView;
	NewsMessage message;
	Activity context;
	ProgressDialog progress;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.news_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		context = this;
		NewsIntent = getIntent();
		url = NewsIntent.getStringExtra("url");
		timeView = (TextView) findViewById(R.id.pub_time_text);
		dateView = (TextView) findViewById(R.id.pub_date_text);
		viewsView = (TextView) findViewById(R.id.views_text);
		titelView = (TextView) findViewById(R.id.news_text);
		imageView = (ImageView) findViewById(R.id.news_image);
		timeView.setText(NewsIntent.getStringExtra("time"));
		dateView.setText(NewsIntent.getStringExtra("date"));
		viewsView.setText(NewsIntent.getStringExtra("views"));
		titelView.setText(NewsIntent.getStringExtra("titel"));
		imageView.setImageBitmap((Bitmap) NewsIntent.getParcelableExtra("image"));
		newsDescription = (WebView) findViewById(R.id.one_piece_of_news_web_view);
		ANApplication.newsDescriptionInit(context, newsDescription);
		if (savedInstanceState == null) {
			NewsTask = new NewsAsyncTask();
			NewsTask.execute(url);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		newsDescription.saveState(outState);
		super.onSaveInstanceState(outState);
	}

	public class NewsAsyncTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			getSherlock().setProgressBarIndeterminateVisibility(true);
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				message = new SaxFeedParser(params[0]).newsParse();
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
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.d("ANA", message.getFullText());
				Log.d("ANA", message.getFullText());
				message.setFullText(message.getFullText().replaceAll("src=\"/upload", "src=\"http://anarcho-news.com/upload").replaceAll("href=\"/upload", "href=\"http://anarcho-news.com/upload"));
				newsDescription.loadDataWithBaseURL("", message.getFullText(), "text/html", "utf-8", "");
				super.onPostExecute(result);
				getSherlock().setProgressBarIndeterminateVisibility(false);
			} else {
				getSherlock().setProgressBarIndeterminateVisibility(false);
				ErrorDialog err = new ErrorDialog(context);
				err.show();
			}
		}
	}

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (newsDescription.canGoBack() == true) {
					newsDescription.goBack();
				} else {
					onBackPressed();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}*/
}