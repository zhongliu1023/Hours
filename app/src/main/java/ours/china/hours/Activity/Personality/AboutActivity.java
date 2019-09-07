package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import ours.china.hours.R;

public class AboutActivity extends AppCompatActivity {

    private ImageView imgAboutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
    }


    private void initUI(){
        imgAboutBack = (ImageView)findViewById(R.id.imgAboutBack);
    }


    private void gotoBack(){
        imgAboutBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    AboutActivity.this.finish();
                } else {
                    AboutActivity.super.onBackPressed();
                }
            }
        });
    }
}


