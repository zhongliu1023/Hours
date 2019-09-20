package ours.china.hours.Activity.Auth;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.Model.VerifyForgot;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends AppCompatActivity {

    EditText edForMobile, edForVerify, edForPass;
    Button btnForConfirm;
    ImageView imgForBack;
    TextView tvRegOtp;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
        verifyCode();
        confirmForgot();
    }


    private void initUI(){

        edForMobile = (EditText)findViewById(R.id.edForMobile);
        edForVerify = (EditText)findViewById(R.id.edForVerify);
        edForPass = (EditText)findViewById(R.id.edForPass);
        imgForBack = (ImageView)findViewById(R.id.imgForBack);
        btnForConfirm = (Button)findViewById(R.id.btnForConfirm);
        tvRegOtp = (TextView) findViewById(R.id.tvRegOtp);

    }

    private void verifyCode(){

        tvRegOtp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                verifyProcess();
            }
        });
    }

    private void verifyProcess(){
        String mobile = edForMobile.getText().toString();
        Global.mobile = mobile;
        if (mobile.equals("")){
            Global.alert(ForgotPassActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
            edForMobile.requestFocus();
            return;
        }
        Global.showLoading(ForgotPassActivity.this,"generate_report");
        Ion.with(ForgotPassActivity.this)
                .load(Url.request_forgot)
                .setTimeout(10000)
                .setBodyParameter("mobile", mobile)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Global.hideLoading();
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {
                                    Toast.makeText(ForgotPassActivity.this, "验证码成功发送", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ForgotPassActivity.this, resObj.getString("err_msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(ForgotPassActivity.this, "验证码成功失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
}


    private void gotoBack(){
        imgForBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    ForgotPassActivity.this.finish();
                } else {
                    ForgotPassActivity.super.onBackPressed();
                }
            }
        });
    }


    private void confirmForgot(){

        btnForConfirm.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String otp = edForVerify.getText().toString();
                String mobile = edForMobile.getText().toString();
                String password = edForPass.getText().toString();

                if (mobile.equals("")){
                    Global.alert(ForgotPassActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
                    edForVerify.requestFocus();
                    return;
                }

                if (password.equals("")) {
                    Global.alert(ForgotPassActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_password), getResources().getString(R.string.confirm));
                    edForPass.requestFocus();
                }

                if (otp.equals("")){
                    Global.alert(ForgotPassActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.register_identy), getResources().getString(R.string.confirm));
                    edForVerify.requestFocus();
                    return;
                }

                Global.showLoading(ForgotPassActivity.this,"generate_report");
                Ion.with(ForgotPassActivity.this)
                        .load(Url.confirm_forgot)
                        .setTimeout(10000)
                        .setBodyParameter("mobile", mobile)
                        .setBodyParameter("password", password)
                        .setBodyParameter("otp", otp)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                Global.hideLoading();

                                if (e == null) {
                                    JSONObject resObj = null;
                                    try {
                                        resObj = new JSONObject(result.toString());

                                        if (resObj.getString("res").equals("success")) {
                                            Global.mobile = mobile;
                                            Global.password = password;
                                            finish();
                                        } else {
                                            Toast.makeText(ForgotPassActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(ForgotPassActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

}
