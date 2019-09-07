package ours.china.hours.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Confirm;
import ours.china.hours.Model.User;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liujie on 1/6/18.
 */

public class RegisterActivity  extends AppCompatActivity {

    private EditText edReMobile, edReVerification, edRePassword;
    private Button btnRegister, btnRegConfirm;
    private TextView tvReForgot, tvReLogin;
    private String TAG = RegisterActivity.class.getSimpleName();

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();

        gotoForgot();
        gotoLogin();
        verifyCode();
        confirmVerify();

    }


    private void initUI(){
        edReMobile = (EditText)findViewById(R.id.edReMobile);
        edReVerification = (EditText)findViewById(R.id.edReVerification);
        edRePassword = (EditText)findViewById(R.id.edRePassword);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        tvReForgot = (TextView)findViewById(R.id.tvReForgot);
        tvReLogin = (TextView)findViewById(R.id.tvReLogin);
        btnRegConfirm = (Button)findViewById(R.id.btnRegConfirm);
    }


    private void gotoForgot(){

        tvReForgot.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });
    }


    private void gotoLogin(){

        tvReLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginOptionActivity.class);
                startActivity(intent);
            }
        });
    }


    private void verifyCode(){

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.i(TAG, "btnRegister = " + btnRegister.getText().toString());
                verifyProcess();
            }
        });
    }


    private void confirmVerify(){

        btnRegConfirm.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.i(TAG, "btnRegister = " + btnRegConfirm.getText().toString());
                confirmVerifyProcess();
            }
        });
    }


    private void verifyProcess(){

        final String mobile = edReMobile.getText().toString();
        final String password = edRePassword.getText().toString();

        if (mobile.equals("")){
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
            edReMobile.requestFocus();
            return;
        }

        if (password.equals("")){
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_password), getResources().getString(R.string.confirm));
            edRePassword.requestFocus();
            return;
        }

        Global.showLoading(RegisterActivity.this,"generate_report");
        Ion.with(RegisterActivity.this)
                .load(Url.verifyCode)
                .setTimeout(10000)
                .setBodyParameter("mobile", mobile)
                .setBodyParameter("password", password)
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
                                    Toast.makeText(RegisterActivity.this, "verify success", Toast.LENGTH_SHORT).show();
                                    btnRegister.setVisibility(View.INVISIBLE);
                                    btnRegConfirm.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "verify error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "Api error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public void confirmVerifyProcess(){

        String otp = edReVerification.getText().toString();
        final String mobile = edReMobile.getText().toString();

        if (mobile.equals("")){
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
            edReMobile.requestFocus();
            return;
        }

        if (otp.equals("")){
            Global.alert(RegisterActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.register_identy), getResources().getString(R.string.confirm));
            edReVerification.requestFocus();
            return;
        }

        Global.showLoading(RegisterActivity.this,"generate_report");
        Ion.with(RegisterActivity.this)
                .load(Url.confirmVerify)
                .setTimeout(10000)
                .setBodyParameter("mobile", mobile)
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
                                    Intent intent = new Intent(RegisterActivity.this, PerfectInforActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "confirm error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "Api error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}