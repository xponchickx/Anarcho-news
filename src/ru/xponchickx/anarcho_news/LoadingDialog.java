package ru.xponchickx.anarcho_news;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Window;

public class LoadingDialog extends Dialog{

	private LoadingDialog(Context context) {
		super(context);
	}
	
	public LoadingDialog(final Activity a, final AsyncTask<String, Void, Boolean> at) {
		super(a);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.loading_dialog_layout);
		this.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				at.cancel(true);
				a.finish();
			}
		});
	}
}
