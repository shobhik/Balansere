package dev.shobhik.balansere.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by shobh_000 on 12/16/2017.
 */

public class LocalData {
    public static final String CALIBRATION_VALUE = "1";
    public static final String CALIBRATION_VALUE_FLOAT = "2";

    public static long getLong(Context context, String name) {

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getLong(name, -1);
    }

    public static void setLong(Context context, String name, long value) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(name, value);
        // Commit the edits!
        editor.commit();
    }

    public static float getFloat(Context context, String name) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getFloat(name, -1);
    }

    public static void setFloat(Context context, String name, float value) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(name, value);
        // Commit the edits!
        editor.commit();
    }

    public static String getString(Context context, String name) {
        return getString(context, name, "");
    }

    public static String getString(Context context, String name,
                                   String defaultValue) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getString(name, defaultValue);
    }

    public static void setString(Context context, String name, String value) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        // Commit the edits!
        editor.commit();
    }

    public static boolean getBoolean(Context context, String name) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getBoolean(name, false);
    }

    public static boolean getBoolean(Context context, String name,
                                     Boolean defaultValue) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getBoolean(name, defaultValue);
    }

    public static void setBoolean(Context context, String name, boolean value) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name, value);
        // Commit the edits!
        editor.commit();
    }


    public static int getInt(Context context, String name) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getInt(name, 0);
    }

    public static int getInt(Context context, String name,
                             int defaultValue) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getInt(name, defaultValue);
    }

    public static void setInt(Context context, String name, int value) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(name, value);
        // Commit the edits!
        editor.commit();
    }


}
