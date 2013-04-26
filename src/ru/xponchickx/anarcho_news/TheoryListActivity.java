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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.fedorvlasov.lazylist.LazyAdapter;

public class TheoryListActivity extends Activity {
	Activity context;
	ArrayList<String> images;
	ArrayList<String> dates;
	ArrayList<String> views;
	ArrayList<String> titels;
	ArrayList<String> times;
	int pageNumber = 1;
	List<CategoryMessage> messages;
	String url;
	ListView theoryList;
	LazyAdapter adapter;
	Button loadMoreButton;
	SaxFeedParser saxFeedParser;
	Boolean loadAutomatically;
	boolean lastPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		url = ANApplication.THEORY;
		context = this;
		theoryList = (ListView) findViewById(R.id.news_list);
		loadAutomatically = ANApplication.getLoadAutomatically();
		
		if (loadAutomatically) {
			theoryList.setOnScrollListener(new OnScrollListener() {
				private int visibleThreshold = 5;
				private int previousTotal = 0;
				private boolean loading = true;
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					
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
							//++pageNumber;
							new LoadAsyncTask().execute(url + ANApplication.QUESTION_MARK + ANApplication.PLUS_PAGE + pageNumber);
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
					new LoadAsyncTask().execute(url + ANApplication.QUESTION_MARK + ANApplication.PLUS_PAGE + pageNumber);
				}
			});
			theoryList.addFooterView(loadMoreButton);
		}
		
		theoryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Intent onePieceOfNewsIntent = new Intent(context, NewsActivity.class);
				onePieceOfNewsIntent.putExtra("url", ANApplication.THEORY + ANApplication.PLUS_ID + messages.get(position).getId());
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
	}

	public class LoadAsyncTask extends AsyncTask<String, Void, Boolean> {
		Dialog loadingDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = new LoadingDialog(context, LoadAsyncTask.this);
			loadingDialog.show();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				saxFeedParser = new SaxFeedParser(params[0]);
				if (messages == null) {
					messages = saxFeedParser.categoriesParse();
					titels = new ArrayList<String>();
					images = new ArrayList<String>();
					dates = new ArrayList<String>();
					times = new ArrayList<String>();
					views = new ArrayList<String>();
					for (CategoryMessage m: messages) {
						titels.add(m.getTitle());
						images.add(m.getImage());
						dates.add(m.getDate());
						views.add(m.getViews());
						times.add(m.getTime());
					}
				} else {
					ArrayList<CategoryMessage> loadedMessages = saxFeedParser.categoriesParse();
					messages.addAll(loadedMessages);
					int size = messages.size();
					int loadedSize = loadedMessages.size();
					for (int i = size - loadedSize; i < messages.size(); ++i) {
						CategoryMessage m = messages.get(i);
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
				if (messages.size() <= 15) {
					adapter = new LazyAdapter(context, titels, images, dates, times, views);
					theoryList.setAdapter(adapter);
				} else {
					int index = theoryList.getFirstVisiblePosition();
					View v = theoryList.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();
					adapter.notifyDataSetChanged();
					theoryList.setSelectionFromTop(index, top);
				}
				if (saxFeedParser.isLastPage()) {
					theoryList.removeFooterView(loadMoreButton);
				}
				loadingDialog.dismiss();
				super.onPostExecute(result);
			} else {
				loadingDialog.dismiss();
				ErrorDialog err = new ErrorDialog(context);
				err.show();
			}
		}
	}
}