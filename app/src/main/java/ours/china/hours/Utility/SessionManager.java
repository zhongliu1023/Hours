package ours.china.hours.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Hours";

    public static String KEY_mobile = "mobile";
    public static String KEY_password = "password";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getMobileNumber() {
        String mobileNumber = pref.getString(KEY_mobile, "");
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_mobile, "");
        editor.commit();
    }

    public String getPassword() {
        String password = pref.getString(KEY_password, "");
        return password;
    }

    public void setPassword(String password) {
        editor.putString(KEY_password, "");
        editor.commit();
    }

}
