package org.na_russia.app.justfortoday;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {

	// ListPreference lpColorScheme;
	ListPreference lpFontSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);

		lpFontSize = (ListPreference) this.findPreference("font_size");
		lpFontSize.setOnPreferenceChangeListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		prepare();
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		String val = null;
		// String key = preference.getKey();
		boolean showMsg = true;
		boolean setSums = true;

		if (preference instanceof ListPreference) {
			int newVal = Integer.parseInt((String) newValue);
			val = (String) ((ListPreference) preference).getEntries()[newVal];
		}

		if (val == null) {
			val = newValue.toString();
		}

		if (setSums) {
			preference.setSummary(val);
		}

		if (showMsg) {
			Utils.showMessage(this, getString(R.string.saved) + ": "
					+ preference.getTitle() + " " + val, Toast.LENGTH_SHORT,
					Gravity.CENTER);
		}

		return true;
	}

	private void prepare() {

		try {

			lpFontSize.setSummary(lpFontSize.getEntry());

		} catch (NullPointerException e) {
		}
	}

}
