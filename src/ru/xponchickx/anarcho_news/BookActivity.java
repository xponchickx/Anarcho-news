package ru.xponchickx.anarcho_news;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class BookActivity extends SherlockActivity {
	Intent bookIntent;
	BookAsyncTask BookTask;
	String url;
	String fileUrl;
	TextView titel, size;
	WebView newsDescription;
	ImageView imageView;
	BookMessage message;
	Activity context; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.book_layout);
		context = this;
		bookIntent = getIntent();
		url = bookIntent.getStringExtra("url");
		newsDescription = (WebView) findViewById(R.id.book_web_view);
		ANApplication.newsDescriptionInit(context, newsDescription);
		imageView = (ImageView) findViewById(R.id.literature_image);
		titel = (TextView) findViewById(R.id.literature_text);
		size = (TextView) findViewById(R.id.book_size);
		imageView.setImageBitmap((Bitmap) bookIntent.getParcelableExtra("image"));
		titel.setText(bookIntent.getStringExtra("titel"));
		BookTask = new BookAsyncTask();
		BookTask.execute(url);
	}
	
	public class BookAsyncTask extends AsyncTask<String, Void, Boolean> {
		//Dialog loadingDialog;

		@Override
		protected void onPreExecute() {
			//loadingDialog = new LoadingDialog(context, BookAsyncTask.this);
			//loadingDialog.show();
			getSherlock().setProgressBarIndeterminateVisibility(true);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				message = new SaxFeedParser(params[0]).bookParse();
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
				String textSize = "<u><font size=\"5\" color=\"blue\" face=\"Arial\">Cкачать: в формате "
									+ getFileExtention(message.getFile()) + " - "
									+ message.getFileSize()
									+ "kB</font></u>";
				size.setText(Html.fromHtml(textSize));
				size.setMovementMethod(LinkMovementMethod.getInstance());
				size.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String fullFilePath = ANApplication.getLoadTo()
								+ message.getFile().substring(message.getFile().lastIndexOf('/')+1,
								message.getFile().length());
						String filePath = getFilePath(fullFilePath);
						String fileName = getFileName(fullFilePath);
						new DownloadFileFromURL().execute(message.getFile(), filePath, fileName);
					}
				});
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

	class DownloadFileFromURL extends AsyncTask<String, String, Boolean> {
		ProgressDialog pDialog;
		InputStream input;
		OutputStream output;
		String absolutePath;

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
	        pDialog = new ProgressDialog(context);
	        pDialog.setMessage("Downloading file. Please wait...");
	        pDialog.setIndeterminate(false);
	        pDialog.setMax(100);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        pDialog.setCancelable(true);
	        pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					DownloadFileFromURL.this.cancel(true);
				}
			});
	        pDialog.show();
		}

		/**
		 * Downloading file in background thread
		 * */
		@Override
		protected Boolean doInBackground(final String... f_url) {
			int count;
			absolutePath = f_url[1] + f_url[2];
			File f = new File(f_url[1]);
			if (!f.exists()) {
				f.mkdirs();
			}
			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					File file = new File(absolutePath);
					file.delete();
					DownloadFileFromURL.this.cancel(true);
				}
			});
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				// getting file length
				int lenghtOfFile = conection.getContentLength();

				// input stream to read file - with 8k buffer
				input = new BufferedInputStream(url.openStream(), 8192);

				// Output stream to write file
				output = new FileOutputStream(absolutePath);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					// After this onProgressUpdate will be called
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));

					// writing data to file
					output.write(data, 0, count);
				}

				// flushing output
				output.flush();

				// closing streams
				output.close();
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (SocketException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		/**
		 * Updating progress bar
		 * */
		@Override
		protected void onProgressUpdate(String... progress) {
			pDialog.setProgress(Integer.parseInt(progress[0]));
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				pDialog.dismiss();
				Toast.makeText(context, "Сохранено в " + absolutePath +".", Toast.LENGTH_LONG).show();
			} else {
				pDialog.dismiss();
				Toast.makeText(context, "Возникла ошибка при сохранении файла в "
										+ absolutePath
										+". Убедитесь, что данный путь существует.",
										Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onCancelled() {
			Toast.makeText(context, "Загрузка отменена.", Toast.LENGTH_LONG).show();
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			super.onCancelled();
		}
	}

	public static String getFileExtention(String fullPath) {
		int sepPos = fullPath.lastIndexOf(File.separator);
		String nameAndExt = fullPath.substring(sepPos + 1, fullPath.length());
		int dotPos = nameAndExt.lastIndexOf(".");
		return dotPos != -1 ? nameAndExt.substring(dotPos + 1) : "";
	}
	
	public static String getFilePath(String fullFilePath) {
		File f = new File(fullFilePath);
		return f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator) + 1);
	}
	
	public static String getFileName(String fullFilePath) {
		File f = new File(fullFilePath);
		return f.getName();
	}

	@Override
	protected void onStop() {
		newsDescription.destroy();
		super.onStop();
	}
}