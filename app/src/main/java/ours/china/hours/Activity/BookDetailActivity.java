package ours.china.hours.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ours.china.hours.R;

public class BookDetailActivity extends AppCompatActivity {

    ImageView imgBack;
    TextView txtSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdetail);

        init();
    }

    public void init() {
        imgBack = findViewById(R.id.img_back);
        txtSearch = findViewById(R.id.txtSearch);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    BookDetailActivity.this.finish();
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        });

        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    BookDetailActivity.this.finish();
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }
}
