package ours.china.hours.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.ReadingStatusBookAdapter;
import ours.china.hours.Model.ReadingStatusBook;
import ours.china.hours.R;

public class ReadingNowBookActivity extends AppCompatActivity {

    RecyclerView recyclerMoreReadingNowBook;
    ImageView imgBack;

    ReadingStatusBookAdapter adapter;
    ArrayList<ReadingStatusBook> bookList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_now_more);

        init();
        event();
    }

    public void init() {
        recyclerMoreReadingNowBook = findViewById(R.id.moreReadingNowBook);

        bookList = new ArrayList<>();
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        bookList.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));

        adapter = new ReadingStatusBookAdapter(ReadingNowBookActivity.this, bookList);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(ReadingNowBookActivity.this);
        recyclerMoreReadingNowBook.setLayoutManager(manager);

        recyclerMoreReadingNowBook.setAdapter(adapter);
    }
    public void event() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    ReadingNowBookActivity.this.finish();
                } else {
                    ReadingNowBookActivity.super.onBackPressed();
                }
            }
        });
    }
}
