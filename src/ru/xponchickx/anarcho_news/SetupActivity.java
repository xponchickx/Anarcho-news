package ru.xponchickx.anarcho_news;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class SetupActivity extends PreferenceActivity {
	
	boolean loadPictures;
	String loadTo;
	EditTextPreference loadToView;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setup_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		loadPictures = ((CheckBoxPreference) findPreference("load_pictures")).isChecked();
		loadToView = (EditTextPreference) findPreference("load_to");
		loadTo = (loadToView).getText();
		loadToView.setSummary(loadTo);
		loadToView.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String value = (String) newValue;
				if (value.charAt(value.length() - 1) != '/') {
					value += '/';
				}
				loadToView.setSummary(value);
				loadToView.setText(value);
				return false;
			}
		});
	}
}