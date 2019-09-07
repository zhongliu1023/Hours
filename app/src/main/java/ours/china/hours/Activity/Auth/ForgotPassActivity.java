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
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.Model.VerifyForgot;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends AppCompatActivity {

    EditText edForMobile, edForVerify, edForPass;
    Button btnForComplete, btnForConfirm;
    ImageView imgForBack;

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
        verifyForgot();
        confirmForgot();
    }


    private void initUI(){

        edForMobile = (EditText)findViewById(R.id.edForMobile);
        edForVerify = (EditText)findViewById(R.id.edForVerify);
        edForPass = (EditText)findViewById(R.id.edForPass);
        btnForComplete = (Button)findViewById(R.id.btnForComplete);
        imgForBack = (ImageView)findViewById(R.id.imgForBack);
        btnForConfirm = (Button)findViewById(R.id.btnForConfirm);

    }


    private void verifyForgot(){

        btnForComplete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                final String mobile = edForMobile.getText().toString();
                final String password = edForPass.getText().toString();

                if (mobile.equals("")){
                    Global.alert(ForgotPassActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
                    edForMobile.requestFocus();
                    return;
                }

                if (password.equals("")){
                    Global.alert(ForgotPassActivity.this, getResources().getString(R.string.register), getResources().getString(R.string.login_password), getResources().getString(R.string.confirm));
                    edForPass.requestFocus();
                    return;
                }

                try {
                    Global.showLoading(ForgotPassActivity.this,"generate_report");
                    Call<VerifyForgot> call = apiInterface.verifyForgot(mobile, password);
                    call.enqueue(new Callback<VerifyForgot>() {
                        @Override
                        public void onResponse(Call<VerifyForgot> call, Response<VerifyForgot> response) {
                            Global.hideLoading();

                            if (response.code() == 404){
                                Toast.makeText(ForgotPassActivity.this, "404", Toast.LENGTH_SHORT).show();
                            }else if (response.code() == 422){
                                Toast.makeText(ForgotPassActivity.this, "422", Toast.LENGTH_SHORT).show();
                            }else if (response.code() == 500){
                                Toast.makeText(ForgotPassActivity.this, "500", Toast.LENGTH_SHORT).show();
                            }else if (response.code() == 200){
                                String res = response.body().res;
                                Log.i("ForgotPassActivity", res);
                                if (res.equals("success")){
                                    btnForComplete.setVisibility(View.INVISIBLE);
                                    btnForConfirm.setVisibility(View.VISIBLE);
                                    Toast.makeText(ForgotPassActivity.this, getResources().getString(R.string.login_verify_code), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ForgotPassActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override public void onFailure(Call<VerifyForgot> call, Throwable t) {
                            Global.hideLoading();
                            Toast.makeText(ForgotPassActivity.this, "fail2", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e) {
                    Log.i("register" ,e.getMessage());
                    Toast.makeText(ForgotPassActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                final String mobile = edForMobile.getText().toString();
                final String password = edForPass.getText().toString();
                final String otp = edForVerify.getText().toString();

                if (mobile.equals("")){
                    Global.alert(ForgotPassActivity.this, getResources().getString(R.string.login_forgot_password), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
                    edForMobile.requestFocus();
                    return;
                }

                if (password.equals("")){
                    Global.alert(ForgotPassActivity.this, getResources().getString(R.string.login_forgot_password), getResources().getString(R.string.login_password), getResources().getString(R.string.confirm));
                    edForPass.requestFocus();
                    return;
                }

                try {
                    Global.showLoading(ForgotPassActivity.this,"generate_report");
                    Call<VerifyForgot> call = apiInterface.confirmForgot(mobile, password, otp);
                    call.enqueue(new Callback<VerifyForgot>() {
                        @Override
                        public void onResponse(Call<VerifyForgot> call, Response<VerifyForgot> response) {
                            Global.hideLoading();

                            if (response.code() == 404){
                                Toast.makeText(ForgotPassActivity.this, "404", Toast.LENGTH_SHORT).show();
                            }else if (response.code() == 422){
                                Toast.makeText(ForgotPassActivity.this, "422", Toast.LENGTH_SHORT).show();
                            }else if (response.code() == 500){
                                Toast.makeText(ForgotPassActivity.this, "500", Toast.LENGTH_SHORT).show();
                            }else if (response.code() == 200){
                                String res = response.body().res;
                                Log.i("ForgotPassActivity", res);
                                if (res.equals("success")){
                                    Toast.makeText(ForgotPassActivity.this, getResources().getString(R.string.forgot_complete), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ForgotPassActivity.this, LoginOptionActivity.class);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(ForgotPassActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override public void onFailure(Call<VerifyForgot> call, Throwable t) {
                            Global.hideLoading();
                            Toast.makeText(ForgotPassActivity.this, "fail2", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e) {
                    Log.i("register" ,e.getMessage());
                    Toast.makeText(ForgotPassActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
