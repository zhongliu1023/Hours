package ours.china.hours;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Url;

public class HoursApplication extends Application {
    private final String TAG = "HoursApplication";

    String refresh_token;
    int decount_time = 30 * 60 * 1000;         //30 minutes

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Url.filter_refresh_token);
        registerReceiver(receiver, filter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh_token = intent.getStringExtra("refresh_token");
            Log.i(TAG, "HoursApplication => " + refresh_token);

            decountTime();
        }
    };

    void decountTime(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getRefreshToken();
                handler.postDelayed(this, decount_time);
            }
        }, decount_time);
    }

    public void getRefreshToken() {
        Ion.with(getApplicationContext())
                .load(Url.refresh_token)
                .setTimeout(10000)
                .setBodyParameter("grant_type", "refresh_token")
                .setBodyParameter("client_id", Url.CLIENT_ID)
                .setBodyParameter("client_secret", Url.CLIENT_SECRET)
                .setBodyParameter("scope", Url.SCOPE)
                .setBodyParameter("refresh_token", refresh_token)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.i(TAG, "result => " + result);

                        if (e == null) {
                            JSONObject resObj = null;

                            try {
                                resObj = new JSONObject(result);
                                if (resObj.getString("res").equals("success")) {
                                    // save token and refresh token.
                                    Global.access_token = resObj.getString("access_token");
                                    Global.refresh_token = resObj.getString("refresh_token");
                                    refresh_token = Global.refresh_token;

                                    Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "网络通讯失败。", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "网络通讯失败。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
