package ru.xponchickx.anarcho_news;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.slidingmenu.lib.SlidingMenu;

public class LiteratureActivity extends SherlockActivity {
	private ListView literatureList;
	ArrayAdapter<String> adapter;
	Activity context;
	ArrayList<String> titels;
	ArrayList<String> images;
	int pageNumber = 1;
	Button loadMoreButton;
	String url = ANApplication.BOOKS;
	public ArrayList<CategoryMessage> messages;
	SaxFeedParser saxFeedParser;
	boolean loadAutomatically;
	boolean lastPage;
	SlidingMenu slidingMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.literature_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		context = this;
		loadAutomatically = ANApplication.getLoadAutomatically();
		literatureList = (ListView) findViewById(R.id.literature_list);
		literatureList.setAdapter(adapter);
		
		setTitle("Литература");
		slidingMenu = new SlidingMenu(this);
		ANApplication.configureSlidingMenu(slidingMenu, LiteratureActivity.this);
		
		if (loadAutomatically) {
			literatureList.setOnScrollListener(new OnScrollListener() {
				private int visibleThreshold = 5;
				private int previousTotal = 0;
				private boolean loading = true;
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
			
			literatureList.addFooterView(loadMoreButton);
		}
		literatureList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Intent bookIntent = new Intent(context, BookActivity.class);
				bookIntent.putExtra("url", ANApplication.BOOKS + ANApplication.PLUS_ID + messages.get(position).getId());
				ImageView image = (ImageView) view.findViewById(R.id.literature_image);
				Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
				bookIntent.putExtra("image", bitmap);
				bookIntent.putExtra("titel", titels.get(position));
				startActivity(bookIntent);
			}
		});
		new LoadAsyncTask().execute(url);
	}

	public class LoadAsyncTask extends AsyncTask<String, Void, Boolean> {
		Dialog loadingDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getSherlock().setProgressBarIndeterminateVisibility(true);
			//loadingDialog = new LoadingDialog(context, LoadAsyncTask.this);
			//loadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
					saxFeedParser = new SaxFeedParser(params[0]);
					if (messages == null) {
					messages = saxFeedParser.categoriesParse();
					titels = new ArrayList<String>();
					images = new ArrayList<String>();
					for (CategoryMessage m : messages) {
						titels.add(m.getTitle());
						images.add(m.getImage());
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
					}
				}
			}  catch (UnknownHostException e) {
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
				if (messages.size() <= 15) {
					adapter = new LiteratureAdapter(context, R.layout.literature_item_layout, R.id.literature_text, titels, images);
					literatureList.setAdapter(adapter);
				} else {
					int index = literatureList.getFirstVisiblePosition();
					View v = literatureList.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();
					adapter.notifyDataSetChanged();
					literatureList.setSelectionFromTop(index, top);
				}
				if (lastPage) {
					literatureList.removeFooterView(loadMoreButton);
				}
				//loadingDialog.dismiss();
				getSherlock().setProgressBarIndeterminateVisibility(false);
			} else {
				//loadingDialog.dismiss();
				getSherlock().setProgressBarIndeterminateVisibility(false);
				ErrorDialog err = new ErrorDialog(context);
				err.show();
			}
			super.onPostExecute(result);
		}
	}
}