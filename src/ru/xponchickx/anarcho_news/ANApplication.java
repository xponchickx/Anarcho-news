package ru.xponchickx.anarcho_news;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.slidingmenu.lib.SlidingMenu;

@SuppressLint("SetJavaScriptEnabled")
public class ANApplication extends Application {
	public static final String NEWS = "http://anarcho-news.com/rss/androidapp/news.php";
	public static final String NEWS_BY_CATEGORY = "http://anarcho-news.com/rss/androidapp/news_by_cat.php";
	public static final String CATEGORIES = "http://anarcho-news.com/rss/androidapp/cat.php";
	public static final String BOOKS = "http://anarcho-news.com/rss/androidapp/books.php";
	public static final String MOVIES = "http://anarcho-news.com/rss/androidapp/movies.php";
	public static final String COMMENT = "http://anarcho-news.com/rss/androidapp/comment.php";
	public static final String THEORY = "http://anarcho-news.com/rss/androidapp/articles.php";
	public static final String PLUS_ID = "?id=";
	public static final String QUESTION_MARK = "?";
	public static final String AND_MARK = "&";
	public static final String PLUS_PAGE = "page=";
	public static final String DOINGS = "http://anarcho-news.com/rss/androidapp/events.php?id=";
	private static List<CategoryMessage> categories;
	static boolean loadPictures;
	static String loadTo;
	static SharedPreferences appPrefs;
	static Context context;

	static public void newsDescriptionInit(Activity context, final WebView newsDescription) {
		appPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		loadPictures = appPrefs.getBoolean("load_pictures", true);
		newsDescription.getSettings().setPluginState(WebSettings.PluginState.ON);
		newsDescription.getSettings().setJavaScriptEnabled(true);
		newsDescription.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		newsDescription.getSettings().setAllowFileAccess(true);
		newsDescription.getSettings().setLoadsImagesAutomatically(loadPictures);
		newsDescription.getSettings().setAllowFileAccess(true);
		newsDescription.setWebChromeClient(new WebChromeClient() {
			private Object mCustomView;
			private CustomViewCallback mCustomViewCallback;
			private ViewGroup mMainContentContainer;

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				if (mCustomView != null) {
					callback.onCustomViewHidden();
				} else {
					mCustomView = view;
					mCustomViewCallback = callback;
					newsDescription.setVisibility(View.GONE);
					mMainContentContainer.addView(view);
				}
			}

			@Override
			public void onHideCustomView() {
				if (mCustomView != null) {
					mMainContentContainer.removeView((View) mCustomView);
					mCustomViewCallback.onCustomViewHidden();
					mCustomView = null;
					newsDescription.setVisibility(View.VISIBLE);
				}
			}
		});
		
		/*newsDescription.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				newsDescription.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		});*/
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this.getBaseContext();
	}

	static public boolean getLoadPictures() {
		return loadPictures;
	}

	static public String getLoadTo() {
		appPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return appPrefs.getString("load_to", context.getResources().getString(R.string.sdcard_download));
	}

	static public Boolean getLoadAutomatically() {
		appPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return appPrefs.getBoolean("load_automatically", false);
	}

	public static List<CategoryMessage> getCategories() {
		return categories;
	}

	public static void setCategories(List<CategoryMessage> categories) {
		ANApplication.categories = categories;
	}
	
	public static void configureSlidingMenu(SlidingMenu menu, final Activity activity) {
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.sliding_menu);
		menu.setBehindOffset(105);
		
		
		ListView menuList = (ListView) menu.findViewById(R.id.menu_list_view);
		String[] categoriesArray = new String[categories.size()];
		int i = 0;
		for (CategoryMessage m: categories) {
			categoriesArray[i] = m.getTitle();
			Log.d("ANA", categoriesArray[i]);
			i++;
		}
		menuList.setAdapter(new ArrayAdapter<String>(context, R.layout.sliding_menu_item, R.id.sliding_menu_item_title, categoriesArray));
		menuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				Intent i = new Intent(context, NewsListActivity.class);
				i.putExtra("url", NEWS_BY_CATEGORY + PLUS_ID + categories.get(pos).getId());
				i.putExtra("title", categories.get(pos).getTitle());
				activity.startActivity(i);
				activity.finish();
			}
		});
		
		Button newsButton = (Button) menu.findViewById(R.id.news_button);
		Button literatureButton = (Button) menu.findViewById(R.id.books_button);
		Button theoryButton = (Button) menu.findViewById(R.id.articles_button);
		Button setupButton = (Button) menu.findViewById(R.id.settings_button);
		
		newsButton.setOnClickListener(new MenuListener(activity, menu));
		literatureButton.setOnClickListener(new MenuListener(activity, menu));
		theoryButton.setOnClickListener(new MenuListener(activity, menu));
		setupButton.setOnClickListener(new MenuListener(activity, menu));
	}
}