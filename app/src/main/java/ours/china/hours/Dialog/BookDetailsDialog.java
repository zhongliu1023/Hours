package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import ours.china.hours.Activity.Auth.LoginOptionActivity;
import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.Activity.Global;
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
import ours.china.hours.Utility.SessionManager;

public class BookDetailsDialog extends Dialog {

    Context context;
    SharedPreferencesManager sessionManager;
    Book focusBook = new Book();

    ImageView imgBack, bookCoverImg;
    TextView bookName, bookAuthor, txtDeadlineDate, txtAverageTime, txtSpecifiedTime,bookSummary;
    RatingBar ratingView;
    TextView txtDownloadButton;

    DBController db = null;

    OnDownloadBookListenner onDownloadBookListner;


    public BookDetailsDialog(Context context, OnDownloadBookListenner listenner) {
        super(context);
        this.context = context;
        this.onDownloadBookListner = listenner;
        setContentView(R.layout.activity_bookdetail);

        initView();
        initListener();
    }
    private void initView() {
        db = new DBController(context);
        sessionManager = new SharedPreferencesManager(context);
        focusBook = BookManagement.getFocuseBook(sessionManager);

        imgBack = findViewById(R.id.imgBack);
        bookCoverImg = findViewById(R.id.bookCoverImg);
        bookName = findViewById(R.id.bookName);
        bookAuthor = findViewById(R.id.bookAuthor);
        txtDeadlineDate = findViewById(R.id.txtDeadlineDate);
        txtAverageTime = findViewById(R.id.txtAverageTime);
        txtSpecifiedTime = findViewById(R.id.txtSpecifiedTime);
        bookSummary = findViewById(R.id.bookSummary);
        ratingView = findViewById(R.id.ratingView);
        txtDownloadButton = findViewById(R.id.txtDownloadButton);

        if (focusBook.bookImageLocalUrl.isEmpty()){
            Glide.with(context)
                    .load(Url.domainUrl + "/" + focusBook.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(bookCoverImg);
        }else{
            Glide.with(context)
                    .load(focusBook.bookImageLocalUrl)
                    .placeholder(R.drawable.book_image)
                    .into(bookCoverImg);
        }
        txtDownloadButton.setVisibility(focusBook.bookLocalUrl.isEmpty()?View.VISIBLE:View.GONE);
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
                onDownloadBookListner.onFinishDownload(focusBook, false);
                dismiss();
            }
        });
        txtDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadImage(context, new ImageListener() {
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
        new DownloadFile(context, new ImageListener() {
            @Override
            public void onImagePath(String path) {
                if (path.equals("")) {
                    Toast.makeText(context, "下载错误", Toast.LENGTH_SHORT).show();
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
        Ion.with(context)
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
                                if (resObject.getString("res").equals("success")||
                                        (resObject.getString("res").equals("fail") && resObject.getString("err_msg").equals("已添加"))) {

                                    onDownloadBookListner.onFinishDownload(focusBook, true);
                                    dismiss();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }


    public interface OnDownloadBookListenner {
        void onFinishDownload(Book book, Boolean isSuccess);
    }
}
