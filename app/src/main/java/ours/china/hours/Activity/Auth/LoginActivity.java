package ours.china.hours.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.kaopiz.kprogresshud.KProgressHUD;

import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Management.SoftKeyboardHandle;
import ours.china.hours.R;


public class LoginActivity extends AppCompatActivity {

    private KProgressHUD hud;
    private SharedPreferencesManager sharedPreferencesManager;

    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        SoftKeyboardHandle.setupUI(findViewById(R.id.parentLogin), this);

        init();
        setListener();
    }

    private void init(){
        loginBtn = (Button) findViewById(R.id.loginBtn);
    }
    private void setListener(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }
}
