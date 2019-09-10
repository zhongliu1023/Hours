package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    private ImageView imgUpdatePassBack;
    private EditText edUpdatePassOld, edUpdatePassNew, edUpdatePassConfirm;
    private Button btnUpdatePassSubmit;

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
        updatePass();
    }


    private void initUI(){

        imgUpdatePassBack = (ImageView)findViewById(R.id.imgUpdatePassBack);
        edUpdatePassOld = (EditText)findViewById(R.id.edUpdatePassOld);
        edUpdatePassNew = (EditText)findViewById(R.id.edUpdatePassNew);
        edUpdatePassConfirm = (EditText)findViewById(R.id.edUpdatePassConfirm);
        btnUpdatePassSubmit = (Button)findViewById(R.id.btnUpdatePassSubmit);
    }


    private void gotoBack(){
        imgUpdatePassBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    UpdatePasswordActivity.this.finish();
                } else {
                    UpdatePasswordActivity.super.onBackPressed();
                }
            }
        });
    }


    private void updatePass(){
        btnUpdatePassSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                updatePassProcessing();
            }
        });
    }


    private void updatePassProcessing(){

        final String oldPass = edUpdatePassOld.getText().toString();
        final String newPass = edUpdatePassNew.getText().toString();
        final String confirPass = edUpdatePassConfirm.getText().toString();
        final String access_token = Global.access_token;
        final String mobile = Global.mobile;

            if (oldPass.equals("")){
                Global.alert(UpdatePasswordActivity.this, getResources().getString(R.string.update_password), getResources().getString(R.string.update_password_old_hint), getResources().getString(R.string.confirm));
                edUpdatePassOld.requestFocus();
                return;
            }

        if (newPass.equals("")){
            Global.alert(UpdatePasswordActivity.this, getResources().getString(R.string.update_password), getResources().getString(R.string.update_password_new_hint), getResources().getString(R.string.confirm));
            edUpdatePassNew.requestFocus();
            return;
        }

        if (confirPass.equals("")){
            Global.alert(UpdatePasswordActivity.this, getResources().getString(R.string.update_password), getResources().getString(R.string.update_password_confirm_hint), getResources().getString(R.string.confirm));
            edUpdatePassConfirm.requestFocus();
            return;
        }

        if (isSamePass(newPass, confirPass)){
            try {
                Global.showLoading(UpdatePasswordActivity.this,"generate_report");
                Call<VerifyCode> call = apiInterface.updatePassword(access_token, mobile, oldPass,newPass);
                call.enqueue(new Callback<VerifyCode>() {
                    @Override
                    public void onResponse(Call<VerifyCode> call, Response<VerifyCode> response) {
                        Global.hideLoading();

                        if (response.code() == 404){
                            Toast.makeText(UpdatePasswordActivity.this, "404", Toast.LENGTH_SHORT).show();
                        }else if (response.code() == 422){
                            Toast.makeText(UpdatePasswordActivity.this, "422", Toast.LENGTH_SHORT).show();
                        }else if (response.code() == 500){
                            Toast.makeText(UpdatePasswordActivity.this, "500", Toast.LENGTH_SHORT).show();
                        }else if (response.code() == 200){
                            String res = response.body().res;
                            Log.i("Register", res);
                            if (res.equals("success")){
                                Toast.makeText(UpdatePasswordActivity.this, getResources().getString(R.string.succes), Toast.LENGTH_SHORT).show();
                                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                    UpdatePasswordActivity.this.finish();
                                } else {
                                    UpdatePasswordActivity.super.onBackPressed();
                                }
                            }else if (res.equals("fail")){
                                Toast.makeText(UpdatePasswordActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override public void onFailure(Call<VerifyCode> call, Throwable t) {
                        Global.hideLoading();
                        Toast.makeText(UpdatePasswordActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e) {
                Log.i("register" ,e.getMessage());
                Toast.makeText(UpdatePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Global.alert(UpdatePasswordActivity.this, getResources().getString(R.string.update_password), getResources().getString(R.string.enter_same_pass), getResources().getString(R.string.confirm));
        }


    }


    private boolean isSamePass(String pass, String confirmPass){

        if (pass.equals(confirmPass)){
            return true;
        }else
            return false;
    }


}
