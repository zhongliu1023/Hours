package ours.china.hours.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Adapter.ReadingStatusBookAdapter;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.model.AppTemp;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.nostra13.universalimageloader.core.ImageLoader;
import ours.china.hours.Common.Interfaces.SelectStatePositionInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.BookStatus;
import ours.china.hours.R;

public class ReadingNowBookActivity extends AppCompatActivity implements SelectStatePositionInterface {
    private final String TAG = "ReadingNowBookActivity";

    ReadingStatusBookAdapter adapter;
    ArrayList<Book> bookArrayList = new ArrayList();
    DBController db;
    ImageView imgBack;
    RecyclerView recyclerMoreReadingNowBook;
    SharedPreferencesManager sessionManager;


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

        sessionManager = new SharedPreferencesManager(ReadingNowBookActivity.this);
        recyclerMoreReadingNowBook = findViewById(R.id.moreReadingNowBook);

        adapter = new ReadingStatusBookAdapter(ReadingNowBookActivity.this, bookArrayList, this);
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
            BookStatus tempBook = db.getBookStateData(one.bookId);
            if (tempBook != null) {
                one.bookStatus  = tempBook;
            }
        }
    }


    @Override
    public void onClickStatePosition(Book selectedBook, int pageNumber) {
        if (!selectedBook.bookLocalUrl.equals("") && !selectedBook.bookImageLocalUrl.equals("")) {
            BookManagement.saveFocuseBook(selectedBook, sessionManager);
            Global.pageNumber = pageNumber;

            Log.i(TAG, "click event is done");
            Log.i(TAG, "pageNumber => " + pageNumber);
            gotoReadingViewFile();
            return;
        }
    }

    void gotoReadingViewFile(){
        Book focuseBook = BookManagement.getFocuseBook(sessionManager);

        List<FileMeta> localBooks = AppDB.get().getAll();
        int tempLibraryPosition = Integer.parseInt(focuseBook.libraryPosition);

        ExtUtils.openFile(ReadingNowBookActivity.this, localBooks.get(tempLibraryPosition));
//        FileMeta meta = localBooks.get(tempLibraryPosition);
//        File file = new File(meta.getPath());
//
//        Log.i(TAG, "here go to selected percent page");
//
//        ImageLoader.getInstance().clearAllTasks();
//        AppTemp.get().readingMode = AppState.READING_MODE_BOOK;
//        ExtUtils.showDocument(ReadingNowBookActivity.this, Uri.fromFile(file), percent, null);
    }
}
