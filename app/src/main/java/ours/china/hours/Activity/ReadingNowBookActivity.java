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
import ours.china.hours.DB.DBController;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class ReadingNowBookActivity extends AppCompatActivity {

    ReadingStatusBookAdapter adapter;
    ArrayList<Book> bookArrayList = new ArrayList();
    DBController db;
    ImageView imgBack;
    RecyclerView recyclerMoreReadingNowBook;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_now_more);

        Global.readingNowOrHistory = "reading";
        getDataFromLocalDB();
        init();
        event();

    }

    public void init() {

        recyclerMoreReadingNowBook = findViewById(R.id.moreReadingNowBook);

        adapter = new ReadingStatusBookAdapter(ReadingNowBookActivity.this, bookArrayList);
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


    public void getDataFromLocalDB() {
        db = new DBController(this);
        bookArrayList = db.getAllData();
        for (int i = 0; i < bookArrayList.size(); i++) {
            Book one = (Book) bookArrayList.get(i);
            Book tempBook = db.getBookStateData(one.getBookID());
            if (tempBook != null) {
                one.setPagesArray(tempBook.getPagesArray());
                one.setReadTime(tempBook.getReadTime());
                one.setLastTime(tempBook.getLastTime());
            }
        }
    }


}
