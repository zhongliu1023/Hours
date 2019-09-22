package ours.china.hours.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ours.china.hours.R;

public class NewsDetailActivity extends AppCompatActivity {

    ImageView imgBack;
    TextView newsTime, textContent;
    String strTime, content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        strTime = getIntent().getStringExtra("time");
        content = getIntent().getStringExtra("content");
        init();
        event();
    }

    public void init() {
        newsTime = findViewById(R.id.newsTime);
        textContent = findViewById(R.id.textContent);
        newsTime.setText(strTime);
        textContent.setText(content);
    }

    public void event() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailActivity.super.onBackPressed();
            }
        });
    }
}
