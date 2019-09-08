package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import ours.china.hours.R;

public class ProblemActivity extends AppCompatActivity {

    private ImageView imgProblemBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
    }


    private void initUI(){
        imgProblemBack = (ImageView)findViewById(R.id.imgProblemBack);
    }


    private void gotoBack(){
        imgProblemBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    ProblemActivity.this.finish();
                } else {
                    ProblemActivity.super.onBackPressed();
                }
            }
        });
    }
}
