package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import ours.china.hours.R;

public class StatementActivity extends AppCompatActivity {

    private ImageView imgStateBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
    }


    private void initUI(){
        imgStateBack = (ImageView)findViewById(R.id.imgStateBack);
    }


    private void gotoBack(){
        imgStateBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    StatementActivity.this.finish();
                } else {
                    StatementActivity.super.onBackPressed();
                }
            }
        });
    }
}
