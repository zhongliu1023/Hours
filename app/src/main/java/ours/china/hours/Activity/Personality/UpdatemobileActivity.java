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

import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatemobileActivity extends AppCompatActivity {

    private ImageView imgUpdateMobileBack;
    private EditText edUpdateMobileNew, edUpdateMobileVerify;
    private Button btnUpdateMobileConfirm, btnUpdateMobileComplete;
    private TextView tvUpdateMobileState;

    APIInterface apiInterface;

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

        tvUpdateMobileState = (TextView)findViewById(R.id.tvUpdateMobileState);
        imgUpdateMobileBack = (ImageView) findViewById(R.id.imgUpdateMobileBack);
        edUpdateMobileNew = (EditText)findViewById(R.id.edUpdateMobileNew);
        edUpdateMobileVerify = (EditText)findViewById(R.id.edUpdateMobileVerify);
        btnUpdateMobileConfirm = (Button)findViewById(R.id.btnUpdateMobileConfirm);
        btnUpdateMobileComplete = (Button)findViewById(R.id.btnUpdateMobileComplete);
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

        try {
            Global.showLoading(UpdatemobileActivity.this,"generate_report");
            Call<VerifyCode> call = apiInterface.updateMobile(access_token, oldMobile, newMobile);
            call.enqueue(new Callback<VerifyCode>() {
                @Override
                public void onResponse(Call<VerifyCode> call, Response<VerifyCode> response) {
                    Global.hideLoading();

                    if (response.code() == 404){
                        Toast.makeText(UpdatemobileActivity.this, "404", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 422){
                        Toast.makeText(UpdatemobileActivity.this, "422", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 500){
                        Toast.makeText(UpdatemobileActivity.this, "500", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 200){
                        String res = response.body().res;
                        Log.i("Register", res);
                        if (res.equals("success")){
                            Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.login_verify_code), Toast.LENGTH_SHORT).show();
                            btnUpdateMobileComplete.setVisibility(View.INVISIBLE);
                            btnUpdateMobileConfirm.setVisibility(View.VISIBLE);
                        }else if (res.equals("fail")){
                            Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override public void onFailure(Call<VerifyCode> call, Throwable t) {
                    Global.hideLoading();
                    Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e) {
            Log.i("register" ,e.getMessage());
            Toast.makeText(UpdatemobileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

        try {
            Global.showLoading(UpdatemobileActivity.this,"generate_report");
            Call<VerifyCode> call = apiInterface.confirmUpdateMobile(access_token, oldMobile, newMobile, otp);
            call.enqueue(new Callback<VerifyCode>() {
                @Override
                public void onResponse(Call<VerifyCode> call, Response<VerifyCode> response) {
                    Global.hideLoading();

                    if (response.code() == 404){
                        Toast.makeText(UpdatemobileActivity.this, "404", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 422){
                        Toast.makeText(UpdatemobileActivity.this, "422", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 500){
                        Toast.makeText(UpdatemobileActivity.this, "500", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 200){
                        String res = response.body().res;
                        Log.i("Register", res);
                        if (res.equals("success")){
                            Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.login_verify_code), Toast.LENGTH_SHORT).show();
                            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                UpdatemobileActivity.this.finish();
                            } else {
                                UpdatemobileActivity.super.onBackPressed();
                            }
                        }else if (res.equals("fail")){
                            Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override public void onFailure(Call<VerifyCode> call, Throwable t) {
                    Global.hideLoading();
                    Toast.makeText(UpdatemobileActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e) {
            Log.i("register" ,e.getMessage());
            Toast.makeText(UpdatemobileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
