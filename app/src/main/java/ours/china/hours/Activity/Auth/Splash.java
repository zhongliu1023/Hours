package ours.china.hours.Activity.Auth;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Constants.URLConstant;
import ours.china.hours.FaceDetect.common.Constants;
import ours.china.hours.FaceDetect.util.ConfigUtil;
import ours.china.hours.Management.DownloadFile;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.User;
import ours.china.hours.R;
import ours.china.hours.Utility.SessionManager;

/**
 * Created by liujie on 1/6/18.
 */

public class Splash extends AppCompatActivity {

    private static final String TAG = "Splash";
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    public String key_passwordOrHash = "";
    public String value_passwordOrHash = "";
    public String value_grant_type = "";
    public String url = "";

    User currentUser = new User();
    SharedPreferencesManager sessionManager;

    private Toast toast = null;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    private FaceEngine faceEngine = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        faceEngine = new FaceEngine();
        ConfigUtil.setFtOrient(Splash.this, FaceEngine.ASF_OP_0_HIGHER_EXT);

       activeEngine();

    }

    public void activeEngine() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                int activeCode = faceEngine.activeOnline(Splash.this, Constants.APP_ID, Constants.SDK_KEY);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            Log.i(TAG, "active engine completed");
//                            showToast(getString(R.string.active_success));
                            init();
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
//                            showToast(getString(R.string.already_activated));
                            init();
                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }

                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = faceEngine.getActiveFileInfo(Splash.this,activeFileInfo);
                        if (res == ErrorInfo.MOK) {
//                            Log.i(TAG, activeFileInfo.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                activeEngine();
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    private void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(s);
            toast.show();
        }
    }

    public void init() {
        sessionManager = new SharedPreferencesManager(Splash.this);
        currentUser = UsersManagement.getCurrentUser(sessionManager);

        if (!currentUser.userId.isEmpty() && !currentUser.password.equals("")) {
            url = Url.loginUrl;
            value_grant_type = "password";
            key_passwordOrHash = "password";
            value_passwordOrHash = currentUser.password;
            getDataFromServer();
        } else if (!currentUser.userId.isEmpty() && currentUser.password.equals("")) {
            url = Url.faceLogin;
            value_grant_type = "face";
            key_passwordOrHash = "face_hash";
            value_passwordOrHash = currentUser.faceHash;
            getDataFromServer();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoLoginView();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }


        // for theme color. In HorizontalBookReadingActivity, set the color of bottombar and actionbar to white color.
        TintUtil.color = getResources().getColor(R.color.white);
        AppState.get().tintColor = getResources().getColor(R.color.white);
        TempHolder.listHash++;

        AppProfile.save(this);
        // -- end.
    }

    void gotoLoginView(){
        Intent intent = new Intent(Splash.this, LoginOptionActivity.class);
        startActivity(intent);
        Splash.this.finish();
    }

    public void getDataFromServer() {
        Global.showLoading(Splash.this,"generate_report");
        Ion.with(Splash.this)
                .load(url)
                .setTimeout(10000)
                .setBodyParameter("grant_type", value_grant_type)
                .setBodyParameter("client_id", "hours_reader")
                .setBodyParameter("client_secret", "a55b8ca1-c6b5-4867-b0dc-766dfb41d073")
                .setBodyParameter("scope", "userinfo bookinfo readinfo")
                .setBodyParameter("username", currentUser.mobile)
                .setBodyParameter(key_passwordOrHash, value_passwordOrHash)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                // save token and refresh token.
                                Global.access_token = resObj.getString("access_token");
                                Global.refresh_token = resObj.getString("refresh_token");

                                // to update token
                                Intent intent = new Intent();
                                intent.setAction(Url.filter_refresh_token);
                                intent.putExtra("refresh_token", Global.refresh_token);
                                sendBroadcast(intent);

                                getUserInfo();

                            } catch (JSONException ex) {
                                Global.hideLoading();
                                ex.printStackTrace();
                                gotoLoginView();
                                Toast.makeText(Splash.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Global.hideLoading();
                            gotoLoginView();
                            Toast.makeText(Splash.this, "网路不给力， 加载失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    void getUserInfo(){
        Ion.with(Splash.this)
                .load(Url.get_profile)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());
                                User user = new UsersManagement().mapFetchProfileResponse(resObj);
                                user.password = Global.password;
                                if (user.name.isEmpty()){
                                    Intent intent = new Intent(Splash.this, PerfectInforActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Global.hideLoading();
                                    UsersManagement.saveCurrentUser(currentUser, sessionManager);
                                    // go to main activity
                                    Intent intent = new Intent(Splash.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (JSONException ex) {
                                Global.hideLoading();
                                gotoLoginView();
                                ex.printStackTrace();
                                Toast.makeText(Splash.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Global.hideLoading();
                            gotoLoginView();
                            Toast.makeText(Splash.this, "网路不给力， 加载失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}