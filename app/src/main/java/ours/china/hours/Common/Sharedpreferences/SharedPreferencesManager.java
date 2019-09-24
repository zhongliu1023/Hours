package ours.china.hours.Common.Sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesManager {
    private final String SHARED_PREFERENCE_NAME = "ours.china.hours";
    private SharedPreferences sharedPreferences;

    private Context context;

    private static SharedPreferencesManager sharedPreferenceManager;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesManager getInstance(Context context) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferencesManager(context);
        }
        return sharedPreferenceManager;
    }

    public void clearPrefernces() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getPreferenceValueString(String key) {
        return sharedPreferences.getString(key, "");
    }
    public void setPrefernceValueString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean getPreferenceBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setPreferenceBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getPreferenceValueInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }
    public void setPreferenceValueInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public float getPreferenceValueFloat(String key) {
        return sharedPreferences.getFloat(key, 0.0f);
    }

    public void setPreferenceValueFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

}
