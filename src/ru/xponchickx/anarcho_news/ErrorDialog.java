package ru.xponchickx.anarcho_news;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class ErrorDialog extends AlertDialog{
	Activity activity;

	protected ErrorDialog(final Activity context) {
		super(context);
		activity = context;
		this.setTitle("Возможно нет подключения к инетернет.");
		this.setMessage("Проверьте подключение к интернет. Затем повторите попытку.\nПовторить?");
		this.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.repeat), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				context.finish();
				context.startActivity(context.getIntent());
			}
		});
		
		this.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				context.finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		this.dismiss();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			activity.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
