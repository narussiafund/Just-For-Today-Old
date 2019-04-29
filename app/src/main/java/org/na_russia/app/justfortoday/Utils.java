package org.na_russia.app.justfortoday;

import java.text.DateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Utils {

	public final static String DN_TIME_KEY = "dntime";
	public final static String DAIRY_KEY = "dairy";

	/**
	 * Обертка для доступа к хранилищу
	 * 
	 * @param context
	 * @return SharedPreferences
	 */
	public static SharedPreferences prefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Обертка для сохранения чисел
	 * 
	 * @param context
	 * @param key
	 *            ключ
	 * @param val
	 *            значение
	 */
	public static void saveInt(Context context, String key, int val) {
		Editor ed = prefs(context).edit();
		ed.putInt(key, val);
		ed.commit();
	}

	/**
	 * Обертка для сохранения больших чисел
	 * 
	 * @param context
	 * @param key
	 *            ключ
	 * @param val
	 *            значение
	 */
	public static void saveLong(Context context, String key, long val) {
		Editor ed = prefs(context).edit();
		ed.putLong(key, val);
		ed.commit();
	}

	/**
	 * Обертка для сохранения строк
	 * 
	 * @param context
	 * @param key
	 *            ключ
	 * @param val
	 *            значение
	 */
	public static void saveString(Context context, String key, String val) {
		Editor ed = prefs(context).edit();
		ed.putString(key, val);
		ed.commit();
	}

	/**
	 * Обертка для получения чисел
	 * 
	 * @param context
	 * @param key
	 * @param def
	 * @return int
	 */
	public static int getInt(Context context, String key, int def) {
		return prefs(context).getInt(key, def);
	}

	/**
	 * Обертка для получения больших чисел
	 * 
	 * @param context
	 * @param key
	 * @param def
	 * @return int
	 */
	public static long getLong(Context context, String key, int def) {
		return prefs(context).getLong(key, def);
	}

	/**
	 * Обертка для получения строки
	 * 
	 * @param context
	 * @param key
	 * @param def
	 * @return int
	 */
	public static String getString(Context context, String key, String def) {
		return prefs(context).getString(key, def);
	}

	/**
	 * Сохраняет время актуальности ежедневника в секундах
	 * 
	 * @param context
	 * @param val
	 */
	public static void saveActual(Context context, int val) {
		saveInt(context, MainActivity.FLD_ACTUAL, val);
	}

	/**
	 * Сохраняет точку UNIX последней загрузки ежедневника
	 * 
	 * @param context
	 * @param val
	 */
	public static void saveDnTime(Context context, long val) {
		saveLong(context, DN_TIME_KEY, val);
	}

	/**
	 * Возвращает кол-во секунд актуальности данных ежедневника
	 * 
	 * @param context
	 * @return int
	 */
	public static int getActual(Context context) {
		return getInt(context, MainActivity.FLD_ACTUAL, 0);
	}

	/**
	 * Возвращает точку UNIX последней загрузки ежедневника
	 * 
	 * @param context
	 * @return int
	 */
	public static long getDnTime(Context context) {
		return getLong(context, DN_TIME_KEY, 0);
	}

	/**
	 * 
	 * @return boolean
	 */
	public static boolean checkActualDairyIsset(Context context) {
		Date now = new Date();
		Date xst = new Date(getDnTime(context));
		DateFormat df = DateFormat.getDateInstance();
		Log.d("DateFormat", "DateFormat xst: " + df.format(xst));
		Log.d("DateFormat", "DateFormat now: " + df.format(now));
		if (df.format(xst).equals(df.format(now))) {
			Log.d("DateFormat", "DateFormats equals");
			return true;
		} else {
			Log.d("DateFormat", "DateFormats NOT equals");
			return false;
		}
	}
	/*
	 * public static boolean checkActualDairyIsset(Context context) {
		Date now = new Date();
		Date xst = new Date(getDnTime(context));
		DateFormat df = DateFormat.getDateInstance();
		Log.d("DateFormat", "DateFormat xst: " + df.format(xst));
		Log.d("DateFormat", "DateFormat now: " + df.format(now));
		if (df.format(xst).equals(df.format(now))) {
			Log.d("DateFormat", "DateFormats equals");
			return true;
		} else {
			Log.d("DateFormat", "DateFormats NOT equals");
			return false;
		}
	}
	 */

	/*
	 * public static boolean checkActualDairyIsset(Context context) { long now =
	 * new Date().getTime(); long point = getDnTime(context) +
	 * getActual(context) * 1000; if (now >= point) { return false; } else {
	 * return true; } }
	 */

	/**
	 * 
	 * @param context
	 * @return String
	 */
	public static String getDairy(Context context) {
		return getString(context, DAIRY_KEY, null);
	}
	
	/**
	 * 
	 * @param context
	 * @return String
	 */
	public static String getDairyDate(Context context) {
		return getString(context, MainActivity.FLD_DATE, null);
	}

	/**
	 * 
	 * @param context
	 * @return JSONObject
	 */
	public static JSONObject getDairyJSONObject(Context context) {
		try {
			return new JSONObject(getDairy(context));
		} catch (JSONException e) {
			System.out.println("JSONException: " + e);
		}
		return new JSONObject();
	}

	/**
	 * 
	 * @param context
	 * @param str
	 */
	public static void saveDairy(Context context, String str) {
		saveString(context, DAIRY_KEY, str);
	}
	
	/**
	 * 
	 * @param context
	 * @param str
	 */
	public static void saveDairyDate(Context context, String str) {
		saveString(context, MainActivity.FLD_DATE, str);
	}

	/**
	 * Получает и возвращает из хранилища значение размера шрифта
	 * 
	 * @param context
	 * @return string
	 */
	public static String getFontSizePref(Context context) {
		return prefs(context).getString("font_size",
				context.getString(R.string.font_size_default));
	}

	public static byte getFontSize(Context context) {
		return (byte) Integer.parseInt(Utils.getFontSizePref(context));
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @return int
	 */
	public static int getColorPref(Context context, String key) {
		int id = context.getResources().getIdentifier(key, "color",
				context.getPackageName());
		return prefs(context).getInt(key,
				Color.parseColor(context.getResources().getString(id)));
	}

	public static String getColorPrefStr(Context context, String key) {
		return String.format("#%06X", 0xFFFFFF & getColorPref(context, key));
	}

	public static void saveColorPref(Context context, String key, int val) {
		Editor ed = prefs(context).edit();
		ed.putInt(key, val);
		ed.commit();
	}

	public static void saveColorPrefs(Context context, String[] keys, int[] vals) {
		Editor ed = prefs(context).edit();
		for (int i = 0; i < keys.length; i++) {
			ed.putInt(keys[i], vals[i]);
		}
		ed.commit();
	}

	public static void removeColorPrefs(Context context, String[] keys) {
		Editor ed = prefs(context).edit();
		for (int i = 0; i < keys.length; i++) {
			ed.remove(keys[i]);
		}
		ed.commit();
	}

	/**
	 * Выводит уведомление - toast
	 * 
	 * @param context
	 * @param text
	 *            - текст уведомления
	 */
	public static void showMessage(Context context, String text) {
		Toast t = Toast.makeText(context, text, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	public static void showMessage(Context c, String text, int duration,
			int gravity) {
		Toast t = Toast.makeText(c, text, duration);
		t.setGravity(gravity, 0, 0);
		t.show();
	}

}
