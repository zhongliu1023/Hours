package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import ours.china.hours.R;

public class FeedbackActivity extends AppCompatActivity {

    private ImageView imgFeedBack;
    private EditText edFeedbakck;
    private Button btnFeedbackSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
        showDialog();
    }


    private void initUI(){
        imgFeedBack = (ImageView)findViewById(R.id.imgFeedBack);
        edFeedbakck = (EditText)findViewById(R.id.edFeedback);
        btnFeedbackSubmit = (Button)findViewById(R.id.btnFeedbackSubmit);
    }


    private void gotoBack(){
        imgFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    FeedbackActivity.this.finish();
                } else {
                    FeedbackActivity.super.onBackPressed();
                }
            }
        });
    }


    private void showDialog(){
        btnFeedbackSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                FeedbackDialog();
            }
        });
    }


    private void FeedbackDialog(){

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_feedback, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = alertDialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}
