package ours.china.hours.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.ImageListener;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.DownloadFile;
import ours.china.hours.Management.DownloadImage;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class BookDetailActivity extends AppCompatActivity {

    SharedPreferencesManager sessionManager;
    Book focusBook = new Book();

    ImageView imgBack, bookCoverImg;
    TextView bookName, bookAuthor, txtDeadlineDate, txtAverageTime, txtSpecifiedTime,bookSummary;
    RatingBar ratingView;
    TextView txtDownloadButton;

    DBController db = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdetail);

        initView();
        initListener();
    }

    private void initView() {
        db = new DBController(BookDetailActivity.this);
        sessionManager = new SharedPreferencesManager(this);
        focusBook = BookManagement.getFocuseBook(sessionManager);

        imgBack = findViewById(R.id.imgBack);
        bookCoverImg = findViewById(R.id.bookCoverImg);
        bookName = findViewById(R.id.bookName);
        bookAuthor = findViewById(R.id.bookAuthor);
        txtDeadlineDate = findViewById(R.id.txtDeadlineDate);
        txtAverageTime = findViewById(R.id.txtAverageTime);
        txtSpecifiedTime = findViewById(R.id.txtSpecifiedTime);
        bookSummary = findViewById(R.id.bookSummary);
//        ratingView = findViewById(R.id.ratingView);
        txtDownloadButton = findViewById(R.id.txtDownloadButton);

        if (focusBook.bookImageLocalUrl.isEmpty()){
            Glide.with(BookDetailActivity.this)
                    .load(Url.domainUrl + "/" + focusBook.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(bookCoverImg);
        }else{
            Glide.with(BookDetailActivity.this)
                    .load(focusBook.bookImageLocalUrl)
                    .placeholder(R.drawable.book_image)
                    .into(bookCoverImg);
        }
        txtDownloadButton.setVisibility(focusBook.bookLocalUrl.isEmpty()?View.GONE:View.VISIBLE);
        bookName.setText(focusBook.bookName);
        bookAuthor.setText(focusBook.author);
        txtDeadlineDate.setText(focusBook.deadline);
        txtAverageTime.setText(focusBook.allAverageTime);
        txtSpecifiedTime.setText(focusBook.demandTime);
        bookSummary.setText(focusBook.summary);
//        ratingView.setRating(focusBook.);
    }
    private void initListener(){
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
        txtDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadImage(BookDetailActivity.this, new ImageListener() {
                    @Override
                    public void onImagePath(String path) {
                        focusBook.bookImageLocalUrl = path;
                        BookManagement.saveFocuseBook(focusBook, sessionManager);
                        downloadFile(focusBook);
                    }
                }).execute(Url.domainUrl + "/" + focusBook.coverUrl);
            }
        });
    }
    void downloadFile(Book book){
        new DownloadFile(BookDetailActivity.this, new ImageListener() {
            @Override
            public void onImagePath(String path) {
                if (path.equals("")) {
                    Toast.makeText(BookDetailActivity.this, "下载错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                book.bookLocalUrl = path;
                BookManagement.saveFocuseBook(book, sessionManager);

                txtDownloadButton.setVisibility(View.GONE);
                resetBookInfoAfterDownloading();
            }
        }).execute(Url.domainUrl + "/" + book.bookNameUrl);
    }

    void resetBookInfoAfterDownloading(){
        Book focuseBook = BookManagement.getFocuseBook(sessionManager);

        int tempPosition = 0;
        if (AppDB.get().getAll() == null || AppDB.get().getAll().size() == 0) {
            tempPosition = 0;
        } else {
            tempPosition = AppDB.get().getAll().size();
        }
        focuseBook.libraryPosition = String.valueOf(tempPosition);
        if (db.getBookData(focuseBook.bookId) == null){
            db.insertData(focuseBook);
        }else{
            db.updateBookDataWithDownloadData(focuseBook);
        }
        BookManagement.saveFocuseBook(focuseBook, sessionManager);

        if (!ExtUtils.isExteralSD(focuseBook.bookLocalUrl)) {
            FileMeta meta = AppDB.get().getOrCreate(focuseBook.bookLocalUrl);
            AppDB.get().updateOrSave(meta);
            IMG.loadCoverPageWithEffect(meta.getPath(), IMG.getImageSize());
        }
        Ion.with(BookDetailActivity.this)
                .load(Url.addToMybooks)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("bookId", focuseBook.bookId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {

                        if (error == null) {
                            try {
                                JSONObject resObject = new JSONObject(result.toString());
                                if (resObject.getString("res").equals("success")) {
                                    if (getFragmentManager().getBackStackEntryCount() == 0) {
                                        BookDetailActivity.this.finish();
                                    } else {
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
