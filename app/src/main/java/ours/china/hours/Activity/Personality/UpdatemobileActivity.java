package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ours.china.hours.R;

public class UpdatemobileActivity extends AppCompatActivity {

    private ImageView imgUpdateMobileBack;
    private EditText edUpdateMobileNew, edUpdateMobileVerify;
    private Button btnUpdateMobileConfirm;
    private TextView tvUpdateMobileState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatemobile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
    }


    private void initUI(){

        tvUpdateMobileState = (TextView)findViewById(R.id.tvUpdateMobileState);
        imgUpdateMobileBack = (ImageView) findViewById(R.id.imgUpdateMobileBack);
        edUpdateMobileNew = (EditText)findViewById(R.id.edUpdateMobileNew);
        edUpdateMobileVerify = (EditText)findViewById(R.id.edUpdateMobileVerify);
        btnUpdateMobileConfirm = (Button)findViewById(R.id.btnUpdateMobileConfirm);
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
}
