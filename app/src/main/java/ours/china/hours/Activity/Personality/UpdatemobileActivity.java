package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Global;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.User;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatemobileActivity extends AppCompatActivity {

    private ImageView imgUpdateMobileBack;
    private EditText edUpdateMobileNew, edUpdateMobileVerify;
    private Button btnUpdateMobileConfirm, btnUpdateMobileComplete;
    private TextView tvUpdateMobileState, currentMobile;

    APIInterface apiInterface;
    SharedPreferencesManager sessionManager;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatemobile);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
        getOtp();
        sendVerify();
    }


    private void initUI(){

        sessionManager = new SharedPreferencesManager(this);
        currentUser = UsersManagement.getCurrentUser(sessionManager);
        tvUpdateMobileState = (TextView)findViewById(R.id.tvUpdateMobileState);
        imgUpdateMobileBack = (ImageView) findViewById(R.id.imgUpdateMobileBack);
        edUpdateMobileNew = (EditText)findViewById(R.id.edUpdateMobileNew);
        edUpdateMobileVerify = (EditText)findViewById(R.id.edUpdateMobileVerify);
        btnUpdateMobileConfirm = (Button)findViewById(R.id.btnUpdateMobileConfirm);
        btnUpdateMobileComplete = (Button)findViewById(R.id.btnUpdateMobileComplete);
        currentMobile = findViewById(R.id.currentMobile);
        currentMobile.setText(currentUser.mobile);
    }


    private void gotoBack(){
        imgUpdateMobileBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    UpdatemobileActivity.this.finish();
                } else {
                    UpdatemobileActivity.super.onBackPressed();
                }
            }
        });
    }


    private void getOtp(){

        btnUpdateMobileComplete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                updateMobile();
            }
        });
    }


    private void sendVerify(){

        btnUpdateMobileConfirm.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                confirmUpdateMobile();
            }
        });
    }


    private void updateMobile(){

        final String newMobile = edUpdateMobileNew.getText().toString();
        final String access_token = Global.access_token;
        final String oldMobile = Global.mobile;

        if (newMobile.equals("")){
            Global.alert(UpdatemobileActivity.this, getResources().getString(R.string.update_mobile), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
            edUpdateMobileNew.requestFocus();
            return;
        }
        Global.showLoading(UpdatemobileActivity.this,"generate_report");
        Ion.with(UpdatemobileActivity.this)
                .load(Url.request_changemobile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("mobile", newMobile)
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
                                    Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.login_verify_code), Toast.LENGTH_SHORT).show();
                                    btnUpdateMobileComplete.setVisibility(View.INVISIBLE);
                                    btnUpdateMobileConfirm.setVisibility(View.VISIBLE);
                                }else {
                                    Toast.makeText(UpdatemobileActivity.this, resObj.getString("err_msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(UpdatemobileActivity.this, "验证码成功失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void confirmUpdateMobile(){

        final String newMobile = edUpdateMobileNew.getText().toString();
        final  String otp = edUpdateMobileVerify.getText().toString();
        final String access_token = Global.access_token;
        final String oldMobile = Global.mobile;

        if (newMobile.equals("")){
            Global.alert(UpdatemobileActivity.this, getResources().getString(R.string.update_mobile), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
            edUpdateMobileNew.requestFocus();
            return;
        }

        if (newMobile.equals("")){
            Global.alert(UpdatemobileActivity.this, getResources().getString(R.string.update_mobile), getResources().getString(R.string.login_verify_code), getResources().getString(R.string.confirm));
            edUpdateMobileVerify.requestFocus();
            return;
        }
        Global.showLoading(UpdatemobileActivity.this,"generate_report");
        Ion.with(UpdatemobileActivity.this)
                .load(Url.confirm_changemobile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("mobile", newMobile)
                .setBodyParameter("otp", otp)
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

                                    Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.login_verify_code), Toast.LENGTH_SHORT).show();
                                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                        UpdatemobileActivity.this.finish();
                                    } else {
                                        UpdatemobileActivity.super.onBackPressed();
                                    }
                                }else {
                                    Toast.makeText(UpdatemobileActivity.this, resObj.getString("err_msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(UpdatemobileActivity.this, "验证码成功失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
