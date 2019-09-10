package ours.china.hours.Activity.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.MainApplication;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.User;
import ours.china.hours.R;
import ours.china.hours.Utility.SessionManager;

/**
 * Created by liujie on 1/6/18.
 */

public class Splash extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(Splash.this, LoginOptionActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void init() {
        sessionManager = new SessionManager(Splash.this);

        if (!sessionManager.getMobileNumber().equals("") && !sessionManager.getPassword().equals("")) {
            Global.mainApplication.setMobileNumber(sessionManager.getMobileNumber());
            Global.mainApplication.setPassword(sessionManager.getPassword());

            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);

            getDataFromServer();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash.this, LoginOptionActivity.class);
                    startActivity(intent);
                    Splash.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    public void getDataFromServer() {
        Ion.with(Splash.this)
                .load(Url.loginUrl)
                .setTimeout(10000)
                .setBodyParameter("grant_type", "password")
                .setBodyParameter("client_id", "testclient")
                .setBodyParameter("client_secret", "testpass")
                .setBodyParameter("scope", "userinfo cloud file node")
                .setBodyParameter("username", Global.mainApplication.getMobileNumber())
                .setBodyParameter("password", Global.mainApplication.getPassword())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Global.hideLoading();
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                // save token and refresh token.
                                Global.token = resObj.getString("access_token");
                                Global.refresh_token = resObj.getString("refresh_token");

                                // go to main activity
                                Intent intent = new Intent(Splash.this, MainActivity.class);
                                startActivity(intent);

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(Splash.this, "Unexpected error occur.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}