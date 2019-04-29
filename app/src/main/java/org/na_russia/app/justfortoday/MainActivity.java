package org.na_russia.app.justfortoday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	View mHr;
	TextView mDate;
	TextView mDayTitle;
	TextView mDayBase;
	TextView mDayText;
	TextView mDayJustTitle;
	TextView mDayJust;
	ImageView mBtnMenu;
	LinearLayout mLLMain;

	private ProgressDialog dialog;
	private Calendar cal = Calendar.getInstance();
	final SimpleDateFormat df = new SimpleDateFormat("d MMMM, EEEE",
			Locale.getDefault());
	final SimpleDateFormat sdf = new SimpleDateFormat("d.M");

	public final static String FLD_TITLE = "title";
	public final static String FLD_DATE = "date";
	public final static String FLD_BASE_TEXT = "basetext";
	public final static String FLD_DAY_TEXT = "daytext";
	public final static String FLD_JUST_TEXT = "justfortoday";
	public final static String FLD_ACTUAL = "actual";

	public final static String FLD_FONT_COLOR = "font_color";
	public final static String FLD_BG_COLOR = "background_color";

	public static final String DAIRY_URL = "http://na-russia.org/egednevnik/dairy_json.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mHr = (View) findViewById(R.id.hr);
		mDate = (TextView) findViewById(R.id.tvDate);
		mDayTitle = (TextView) findViewById(R.id.tvDayTitle);
		mDayBase = (TextView) findViewById(R.id.tvDayBase);
		mDayText = (TextView) findViewById(R.id.tvDayText);
		mDayJust = (TextView) findViewById(R.id.tvDayJustText);
		mDayJustTitle = (TextView) findViewById(R.id.tvDayJustTitle);
		mLLMain = (LinearLayout) findViewById(R.id.llMain);
		mBtnMenu = (ImageView) findViewById(R.id.ibMenu);
		registerForContextMenu(mBtnMenu);
		mBtnMenu.setOnClickListener(this);

		loadDairy();

	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	protected void init() {

		int fontSize = getResources().getIntArray(R.array.font_sizes)[Utils
				.getFontSize(this)];

		mDate.setTextSize(fontSize - 3);
		mDayTitle.setTextSize(fontSize + 5);
		mDayBase.setTextSize(fontSize);
		mDayText.setTextSize(fontSize);
		mDayJust.setTextSize(fontSize);
		mDayJustTitle.setTextSize(fontSize + 3);

		int color = Utils.getColorPref(this, FLD_FONT_COLOR);
		mDate.setTextColor(color);
		mDayTitle.setTextColor(color);
		mDayBase.setTextColor(color);
		mDayText.setTextColor(color);
		mDayJust.setTextColor(color);
		mDayJustTitle.setTextColor(color);
		mHr.setBackgroundColor(color);

		mLLMain.setBackgroundColor(Utils.getColorPref(this, FLD_BG_COLOR));

	}

	private void loadDairy() {

		if (Utils.checkActualDairyIsset(this)) {
			Log.d("loadDairy", "ActualDairyIsset");
			setStrings(Utils.getDairyJSONObject(this));
		} else {
			Log.d("loadDairy", "ActualDairy NOT Isset");
			loadRemoteDairy();
		}

	}

	private String getDairyUrl() {
		return DAIRY_URL + "?c=android&d=" + cal.get(Calendar.DAY_OF_MONTH)
				+ "&m=" + (cal.get(Calendar.MONTH) + 1);
	}

	private void loadRemoteDairy() {

		new DairyLoaderTask().execute(getDairyUrl());

	}

	class DairyLoaderTask extends AsyncTask<String, String, JSONObject> {

		public DairyLoaderTask() {
			super();
		}

		@Override
		protected JSONObject doInBackground(String... urls) {

			HttpURLConnection con = null;

			try {

				URL url = new URL(urls[0]);
				con = (HttpURLConnection) url.openConnection();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				return new JSONObject(sb.toString());

			} catch (UnknownHostException e) {
				System.out.println("UnknownHostException: " + e);
				return new JSONObject();
			} catch (IOException e) {
				System.out.println("IOException: " + e);
			} catch (JSONException e) {
				System.out.println("JSONException: " + e);
			} catch (Exception e) {
				System.out.println("Exception: " + e);
			} finally {
				con.disconnect();
			}

			// return jsonObject.toString();
			return new JSONObject();

		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {

			super.onPostExecute(jsonObject);

			if (!jsonObject.isNull(FLD_DATE)) {

				Utils.saveActual(getApplicationContext(),
						jsonObject.optInt(FLD_ACTUAL, 0));
				Utils.saveDnTime(getApplicationContext(), cal.getTime()
						.getTime());
				Utils.saveDairy(getApplicationContext(), jsonObject.toString());
				Utils.saveDairyDate(getApplicationContext(), jsonObject.optString(FLD_DATE,null));
			}

			setStrings(jsonObject);

			dialog.dismiss();

		}

		@Override
		protected void onPreExecute() {

			mLLMain.setVisibility(View.GONE);

			dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage(getString(R.string.data_loading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
			super.onPreExecute();
		}
	}

	public void setStrings(JSONObject jsonObject) {

		String title = null;
		String base = null;
		String text = null;
		String just = null;
		
		//cal.setTimeInMillis(Utils.getDnTime(getApplicationContext()));
		
		try {
			Log.d("getDairyDate", "getDairyDate: "+Utils.getDairyDate(getApplicationContext()));
		    Date date = sdf.parse(Utils.getDairyDate(getApplicationContext()));
		    cal.setTime(date);
		    System.out.println("Date ->" + date);
		} catch (Exception e) {
		    e.printStackTrace();
		}

		title = jsonObject.optString(FLD_TITLE, getString(R.string.error));
		base = jsonObject.optString(FLD_BASE_TEXT, getString(R.string.failed));
		text = jsonObject.optString(FLD_DAY_TEXT,
				getString(R.string.cant_load_data));
		just = jsonObject.optString(FLD_JUST_TEXT,
				getString(R.string.try_again_later));

		mLLMain.setVisibility(View.VISIBLE);

		mDate.setText(df.format(cal.getTime()));

		mDayTitle.setText(Html.fromHtml(title));
		mDayBase.setText(Html.fromHtml(base));
		mDayText.setText(Html.fromHtml(text));
		mDayJust.setText(Html.fromHtml(just));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			showSettings();
			return true;
		case R.id.action_about:
			showAbout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showSettings() {
		Intent i = new Intent(this, PrefsActivity.class);
		startActivity(i);
	}

	public void showAbout() {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibMenu:
			this.openContextMenu(v);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			showSettings();
			break;
		case R.id.about:
			showAbout();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

}
