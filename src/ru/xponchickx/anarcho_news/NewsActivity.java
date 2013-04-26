package ru.xponchickx.anarcho_news;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
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

public class NewsActivity extends Activity {
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
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	public class NewsAsyncTask extends AsyncTask<String, Void, Boolean> {
		Dialog loadingDialog;
		@Override
		protected void onPreExecute() {
			loadingDialog = new LoadingDialog(context, NewsAsyncTask.this);
			loadingDialog.show();
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
				newsDescription.loadDataWithBaseURL("", message.getFullText(), "text/html", "utf-8", "");
				super.onPostExecute(result);
				loadingDialog.dismiss();
			} else {
				loadingDialog.dismiss();
				ErrorDialog err = new ErrorDialog(context);
				err.show();
			}
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d("ANA", "NewsActivity: onRestoreInstanceState");
		newsDescription.restoreState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}
}