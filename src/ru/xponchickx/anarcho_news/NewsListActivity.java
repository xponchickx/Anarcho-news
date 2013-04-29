package ru.xponchickx.anarcho_news;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.fedorvlasov.lazylist.LazyAdapter;
import com.slidingmenu.lib.SlidingMenu;

public class NewsListActivity extends SherlockActivity {
	Activity context;
	ArrayList<String> images;
	ArrayList<String> dates;
	ArrayList<String> views;
	ArrayList<String> titels;
	ArrayList<String> times;
	int pageNumber = 1;
	List<CategoryMessage> newsMessages;
	List<CategoryMessage> categoriesMessages;
	String url;
	ListView newsList;
	LazyAdapter adapter;
	Button loadMoreButton;
	SaxFeedParser saxFeedParser;
	boolean loadAutomatically;
	SlidingMenu slidingMenu;
	boolean lastPage;
	boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.news_list_layout);
		if (getIntent().hasExtra("title")) {
			setTitle(getIntent().getStringExtra("title"));
		} else {
			setTitle("Последние новости");
		}

		if (getIntent().hasExtra("url")) {
			url = getIntent().getStringExtra("url");
		} else {
			url = ANApplication.NEWS_BY_CATEGORY;
		}
		
		// configure the SlidingMenu
		
		slidingMenu = new SlidingMenu(this);
		
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		context = this;
		loadAutomatically = ANApplication.getLoadAutomatically();
		newsList = (ListView) findViewById(R.id.news_list);

		if (loadAutomatically) {
			newsList.setOnScrollListener(new OnScrollListener() {
				private int visibleThreshold = 5;
				private int previousTotal = 0;
				private boolean loading = true;

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					if (!lastPage) {
						if (loading) {
							if (totalItemCount > previousTotal) {
								loading = false;
								previousTotal = totalItemCount;
								++pageNumber;
							}
						}
						if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
							// I load the next page of gigs using a background task,
							// but you can call any function here.
							
							if (url.equals(ANApplication.NEWS_BY_CATEGORY) || (url.equals(ANApplication.THEORY))) {
								new LoadAsyncTask().execute(url + ANApplication.QUESTION_MARK + ANApplication.PLUS_PAGE + pageNumber);
							} else if (!url.equals(ANApplication.THEORY)) {
								new LoadAsyncTask().execute(url + ANApplication.AND_MARK + ANApplication.PLUS_PAGE + pageNumber);
							}
							loading = true;
						}
					}
				}
			});
		} else {
			loadMoreButton = new Button(context);
			loadMoreButton.setText(R.string.load_more);
			loadMoreButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					++pageNumber;
					if (url.equals(ANApplication.NEWS_BY_CATEGORY)) {
						new LoadAsyncTask().execute(url + ANApplication.QUESTION_MARK + ANApplication.PLUS_PAGE + pageNumber);
					} else {
						new LoadAsyncTask().execute(url + ANApplication.AND_MARK + ANApplication.PLUS_PAGE + pageNumber);
					}
				}
			});
			newsList.addFooterView(loadMoreButton);
		}

		newsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Intent onePieceOfNewsIntent = new Intent(context, NewsActivity.class);
				if (!url.equals(ANApplication.THEORY)) {
					onePieceOfNewsIntent.putExtra("url", ANApplication.NEWS + ANApplication.PLUS_ID + newsMessages.get(position).getId());
				} else {
					onePieceOfNewsIntent.putExtra("url", ANApplication.THEORY + ANApplication.PLUS_ID + newsMessages.get(position).getId());
				}
				onePieceOfNewsIntent.putExtra("views", views.get(position));
				ImageView image = (ImageView) view.findViewById(R.id.news_image);
				Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
				onePieceOfNewsIntent.putExtra("image", bitmap);
				onePieceOfNewsIntent.putExtra("date", dates.get(position));
				onePieceOfNewsIntent.putExtra("time", times.get(position));
				onePieceOfNewsIntent.putExtra("titel", titels.get(position));
				startActivity(onePieceOfNewsIntent);
			}
		});
		new LoadAsyncTask().execute(url);
		if (ANApplication.getCategories() == null) {
			new CategoriesAsyncTask().execute(ANApplication.CATEGORIES);
		} else {
			ANApplication.configureSlidingMenu(slidingMenu, this);
		}
	}

	public class LoadAsyncTask extends AsyncTask<String, Void, Boolean> {
		//Dialog loadingDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//loadingDialog = new LoadingDialog(context, LoadAsyncTask.this);
			//loadingDialog.show();
			getSherlock().setProgressBarIndeterminateVisibility(true);
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				saxFeedParser = new SaxFeedParser(params[0]);
				if (newsMessages == null) {
					newsMessages = saxFeedParser.categoriesParse();
					titels = new ArrayList<String>();
					images = new ArrayList<String>();
					dates = new ArrayList<String>();
					times = new ArrayList<String>();
					views = new ArrayList<String>();
					for (CategoryMessage m: newsMessages) {
						titels.add(m.getTitle());
						images.add(m.getImage());
						dates.add(m.getDate());
						views.add(m.getViews());
						times.add(m.getTime());
					}
				} else {
					ArrayList<CategoryMessage> loadedMessages = saxFeedParser.categoriesParse();
					newsMessages.addAll(loadedMessages);
					int size = newsMessages.size();
					int loadedSize = loadedMessages.size();
					for (int i = size - loadedSize; i < newsMessages.size(); ++i) {
						CategoryMessage m = newsMessages.get(i);
						titels.add(m.getTitle());
						images.add(m.getImage());
						dates.add(m.getDate());
						views.add(m.getViews());
						times.add(m.getTime());
					}
				}
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
			lastPage = saxFeedParser.isLastPage();
			if (result) {
				if (newsMessages.size() <= 15) {
					adapter = new LazyAdapter(context, titels, images, dates, times, views);
					newsList.setAdapter(adapter);
				} else {
					int index = newsList.getFirstVisiblePosition();
					View v = newsList.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();
					adapter.notifyDataSetChanged();
					newsList.setSelectionFromTop(index, top);
				}
				if (lastPage) {
					newsList.removeFooterView(loadMoreButton);
				}
				getSherlock().setProgressBarIndeterminateVisibility(false);
				//loadingDialog.dismiss();
			} else {
				//loadingDialog.dismiss();
				getSherlock().setProgressBarIndeterminateVisibility(false);
				ErrorDialog err = new ErrorDialog(context);
				err.show();
			}
			super.onPostExecute(result);
		}
	}

	class CategoriesAsyncTask extends AsyncTask<String, Void, Boolean> {
		Dialog loadingDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//loadingDialog = new LoadingDialog(context, CategoriesAsyncTask.this);
			//loadingDialog.show();
			getSherlock().setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				categoriesMessages = new SaxFeedParser(params[0]).categoriesParse();
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
				ANApplication.setCategories(categoriesMessages);
				ANApplication.configureSlidingMenu(slidingMenu, NewsListActivity.this);
				//loadingDialog.dismiss();
				getSherlock().setProgressBarIndeterminateVisibility(false);
				for (CategoryMessage m: ANApplication.getCategories()) {
					Log.d("ANA", m.getTitle());
				}
			} else {
				ErrorDialog err = new ErrorDialog(context);
				err.show();
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (slidingMenu.isMenuShowing()) {
			slidingMenu.toggle();
		} else {
			if (doubleBackToExitPressedOnce) {
				super.onBackPressed();
				return;
			}
			this.doubleBackToExitPressedOnce = true;
			Toast.makeText(this, "Нажмите \"Назад\" ещё раз , чтобы выйти", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					doubleBackToExitPressedOnce = false;
				}
			}, 2500);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		slidingMenu.toggle();
		return true;
	}
}