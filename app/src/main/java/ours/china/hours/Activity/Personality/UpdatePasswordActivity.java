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

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
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
            Global.showLoading(UpdatePasswordActivity.this,"generate_report");
            Ion.with(UpdatePasswordActivity.this)
                    .load(Url.change_password)
                    .setBodyParameter(Global.KEY_token, Global.access_token)
                    .setBodyParameter("old_password", oldPass)
                    .setBodyParameter("new_password", newPass)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception error, JsonObject result) {

                            Global.hideLoading();
                            if (error == null) {
                                try {
                                    JSONObject resObject = new JSONObject(result.toString());
                                    if (resObject.getString("res").equals("success")) {
                                        Toast.makeText(UpdatePasswordActivity.this, getResources().getString(R.string.succes), Toast.LENGTH_SHORT).show();
                                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                            UpdatePasswordActivity.this.finish();
                                        } else {
                                            UpdatePasswordActivity.super.onBackPressed();
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });

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
