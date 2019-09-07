package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import ours.china.hours.R;

public class UpdatePasswordActivity extends AppCompatActivity {

    private ImageView imgUpdatePassBack;
    private EditText edUpdatePassOld, edUpdatePassNew, edUpdatePassConfirm;
    private Button btnUpdatePassSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
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
}
