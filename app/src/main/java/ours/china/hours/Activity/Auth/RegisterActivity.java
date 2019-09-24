package ours.china.hours.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import ours.china.hours.Activity.Global;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.User;
import ours.china.hours.R;

/**
 * Created by liujie on 1/6/18.
 */

public class RegisterActivity  extends AppCompatActivity {

    private EditText edReMobile, edReVerification, edRePassword;
    private Button btnRegConfirm;
    private TextView tvReForgot, tvReLogin, tvRegOtp;
    private RelativeLayout viewBack;

    private static String TAG = "RegisterActivity";

    String mobile, password;

    APIInterface apiInterface;
    SharedPreferencesManager sessionManager;

    Boolean isStartedCount = false;
    int totalCount = 60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        initListiner();

    }


    private void initUI(){
        sessionManager = new SharedPreferencesManager(this);
        edReMobile = (EditText)findViewById(R.id.edReMobile);
        edReVerification = (EditText)findViewById(R.id.edReVerification);
        edRePassword = (EditText)findViewById(R.id.edRePassword);
        tvReForgot = (TextView)findViewById(R.id.tvReForgot);
        tvReLogin = findViewById(R.id.tvReLogin);
        btnRegConfirm = (Button)findViewById(R.id.btnRegConfirm);
        tvRegOtp = (TextView) findViewById(R.id.tvRegOtp);
        viewBack =  findViewById(R.id.viewBack);
    }

    private void initListiner(){
        tvReForgot.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });
        tvReLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });
        tvRegOtp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                if (!isStartedCount){
                    verifyProcess();
                }
            }
        });
        btnRegConfirm.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.i(TAG, "btnRegister = " + btnRegConfirm.getText().toString());
                confirmVerifyProcess();
            }
        });
        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });
    }

    private void verifyProcess(){

        String mobile = edReMobile.getText().toString();
        String password = edRePassword.getText().toString();

        Global.mobile = mobile;
        Global.password = password;

        if (mobile.equals("")){
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
            edReMobile.requestFocus();
            return;
        }

        Log.i(TAG, "mobile => " + mobile);
        Log.i(TAG, "password => " + password);
        Global.showLoading(RegisterActivity.this,"generate_report");
        Ion.with(RegisterActivity.this)
                .load(Url.verifyCode)
                .setTimeout(10000)
                .setBodyParameter("mobile", mobile)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.i(TAG, "result = >" + result);
                        Global.hideLoading();

                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {
                                    Toast.makeText(RegisterActivity.this, "验证码成功发送", Toast.LENGTH_SHORT).show();
                                    decountTime();
                                }else {
                                    Toast.makeText(RegisterActivity.this, resObj.getString("err_msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "验证码成功失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    void decountTime(){
        isStartedCount = true;
        Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    totalCount--;
                    tvRegOtp.setText(getString(R.string.countTime_verification, Integer.toString(totalCount)));
                    if (totalCount == 0){
                        isStartedCount = false;
                        tvRegOtp.setText(getString(R.string.login_verify_code));
                    }
                });
            }
        }, 1000, 1000);
    }
    public void confirmVerifyProcess(){

        String otp = edReVerification.getText().toString();
        mobile = edReMobile.getText().toString();
        password = edRePassword.getText().toString();

        if (mobile.equals("")){
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
            edReMobile.requestFocus();
            return;
        }

        if (password.equals("")) {
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_password), getResources().getString(R.string.confirm));
            edRePassword.requestFocus();
        }

        if (otp.equals("")){
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.register_identy), getResources().getString(R.string.confirm));
            edReVerification.requestFocus();
            return;
        }

        Log.i(TAG, "mobile => " + mobile);
        Log.i(TAG, "password => " + password);
        Log.i(TAG, "opt => " + otp);

        Global.showLoading(RegisterActivity.this,"generate_report");
        Ion.with(RegisterActivity.this)
                .load(Url.confirmVerify)
                .setTimeout(10000)
                .setBodyParameter("username", mobile)
                .setBodyParameter("password", password)
                .setBodyParameter("otp", otp)
                .setBodyParameter("grant_type", "password")
                .setBodyParameter("client_id", Url.CLIENT_ID)
                .setBodyParameter("client_secret", Url.CLIENT_SECRET)
                .setBodyParameter("scope", Url.SCOPE)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Global.hideLoading();
                        Log.i(TAG, "result => " + result);

                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {
                                    // save token and refresh token.
                                    Global.access_token = resObj.getString("access_token");
                                    Global.refresh_token = resObj.getString("refresh_token");

                                    Global.mobile = mobile;
                                    User currentUser = new User();
                                    currentUser.mobile = mobile;
                                    currentUser.password = password;
                                    UsersManagement.saveCurrentUser(currentUser, sessionManager);

                                    Log.i(TAG, "mobile number => " + Global.mobile);
                                    Intent intent = new Intent(RegisterActivity.this, PerfectInforActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}