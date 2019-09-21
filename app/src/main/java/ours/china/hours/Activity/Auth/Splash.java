package ours.china.hours.Activity.Auth;

import android.Manifest;
import android.content.Intent;
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
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

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
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Constants.URLConstant;
import ours.china.hours.FaceDetect.common.Constants;
import ours.china.hours.FaceDetect.util.ConfigUtil;
import ours.china.hours.Management.DownloadFile;
import ours.china.hours.Management.Url;
import ours.china.hours.R;
import ours.china.hours.Utility.SessionManager;

/**
 * Created by liujie on 1/6/18.
 */

public class Splash extends AppCompatActivity {

    private static final String TAG = "Splash";
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    SessionManager sessionManager;

    private Toast toast = null;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    private FaceEngine faceEngine = new FaceEngine();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Log.i(TAG, "Here is start point");
//        ConfigUtil.setFtOrient(Splash.this, FaceEngine.ASF_OP_0_HIGHER_EXT);
//
//        activeEngine();
        List<FileMeta> localBooks = AppDB.get().getAll();
        ExtUtils.openFile(Splash.this, localBooks.get(0));

//        new DownloadFile(Splash.this).execute(Url.baseUrl + "assets/upload/book/mimetype.epub");

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
                            showToast(getString(R.string.active_success));
                            init();
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
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
        sessionManager = new SessionManager(Splash.this);

        Log.i(TAG, "mobile number =>" + sessionManager.getMobileNumber());
        Log.i(TAG, "password => " + sessionManager.getPassword());

        if (!sessionManager.getMobileNumber().equals("") && !sessionManager.getPassword().equals("")) {

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


        // for theme color. In HorizontalBookReadingActivity, set the color of bottombar and actionbar to white color.
        TintUtil.color = getResources().getColor(R.color.white);
        AppState.get().tintColor = getResources().getColor(R.color.white);
        TempHolder.listHash++;

        AppProfile.save(this);
        // -- end.

    }

    public void getDataFromServer() {
        Ion.with(Splash.this)
                .load(Url.loginUrl)
                .setTimeout(10000)
                .setBodyParameter("grant_type", "password")
                .setBodyParameter("client_id", "testclient")
                .setBodyParameter("client_secret", "testpass")
                .setBodyParameter("scope", "userinfo cloud file node")
                .setBodyParameter("username", sessionManager.getMobileNumber())
                .setBodyParameter("password", sessionManager.getPassword())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.i(TAG, "result => " + result);
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
                            Toast.makeText(Splash.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}