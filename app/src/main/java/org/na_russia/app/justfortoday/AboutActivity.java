package org.na_russia.app.justfortoday;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

	TextView mAppVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		mAppVersion = (TextView) findViewById(R.id.tvAppVersion);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mAppVersion.setText("Версия: " + getAppVersion());

	}

	public String getAppVersion() {
		Context context = getApplicationContext(); // or
													// activity.getApplicationContext()
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();

		String myVersionName = "00"; // initialize String

		try {
			myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return myVersionName;
	}

}
