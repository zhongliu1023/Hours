package ours.china.hours.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.ReadingCompleteStatusBookAdapter;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.ReadingCompleteStatusBook;
import ours.china.hours.R;

public class ReadingCompleteBookActivity extends AppCompatActivity {

    RecyclerView recyclerMoreReadingCompleteBook;
    ImageView imgBack;
    ReadingCompleteStatusBookAdapter adapter;

    ArrayList<Book> bookList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_complete_more);

        init();
        event();
    }

    public void init() {
        recyclerMoreReadingCompleteBook = findViewById(R.id.moreReadingCompleteBook);

        bookList = new ArrayList<>();

        adapter = new ReadingCompleteStatusBookAdapter(this, bookList);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(ReadingCompleteBookActivity.this);
        recyclerMoreReadingCompleteBook.setLayoutManager(manager);

        recyclerMoreReadingCompleteBook.setAdapter(adapter);

    }

    public void event() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    ReadingCompleteBookActivity.this.finish();
                } else {
                    ReadingCompleteBookActivity.super.onBackPressed();
                }
            }
        });
    }
}
