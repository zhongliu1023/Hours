package ours.china.hours;

import android.app.Application;

public class MainApplication extends Application {

    private String userName;
    private String mobileNumber;
    private String password;

    public static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = MainApplication.this;
    }

    public static MainApplication getInstance() {
        return mInstance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
