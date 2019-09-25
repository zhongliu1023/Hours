package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

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
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;

public class BookDetailsDialog extends Dialog {

    private final String TAG = "BookDetailsDialog";

    Context context;
    SharedPreferencesManager sessionManager;
    Book focusBook = new Book();

    ImageView imgBack, bookCoverImg;
    TextView bookName, bookAuthor, txtDeadlineDate, txtAverageTime, txtSpecifiedTime,bookSummary;
//    RatingBar ratingView;
    TextView txtDownloadButton;

    ImageView starImage;
    int starImagePos = 0;

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
//        ratingView = findViewById(R.id.ratingView);
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
        txtDownloadButton.setVisibility(focusBook.bookLocalUrl.isEmpty() ? View.VISIBLE : View.GONE);
        bookName.setText(focusBook.bookName);
        bookAuthor.setText(focusBook.author);
        txtDeadlineDate.setText(focusBook.deadline);
        txtAverageTime.setText(focusBook.allAverageTime);
        txtSpecifiedTime.setText(focusBook.demandTime);
        bookSummary.setText(focusBook.summary);

        starImage = findViewById(R.id.starImage);
        if (focusBook.bookStatus.isAttention.equals(QueryBook.BookAttention.ATTENTION.toString())) {
            starImage.setImageResource(R.drawable.collect2_icon);
            starImagePos = 1;
        } else {
            starImage.setImageResource(R.drawable.collect_icon);
            starImagePos = 0;
        }

    }

    private void initListener(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addOrRemoveAttentionApiWork(false);
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

        starImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (starImagePos == 0) {
                    starImagePos = 1;
                    starImage.setImageResource(R.drawable.collect2_icon);
                    focusBook.bookStatus.isAttention = QueryBook.BookAttention.ATTENTION.toString();
                } else if (starImagePos == 1) {
                    starImagePos = 0;
                    starImage.setImageResource(R.drawable.collect_icon);
                    focusBook.bookStatus.isAttention = QueryBook.BookAttention.NONE.toString();
                }
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
        focusBook = BookManagement.getFocuseBook(sessionManager);

        int tempPosition = 0;
        if (AppDB.get().getAll() == null || AppDB.get().getAll().size() == 0) {
            tempPosition = 0;
        } else {
            tempPosition = AppDB.get().getAll().size();
        }
        focusBook.libraryPosition = String.valueOf(tempPosition);
        if (db.getBookData(focusBook.bookId) == null){
            db.insertData(focusBook);
        }else{
            db.updateBookDataWithDownloadData(focusBook);
        }
        BookManagement.saveFocuseBook(focusBook, sessionManager);

        if (!ExtUtils.isExteralSD(focusBook.bookLocalUrl)) {
            FileMeta meta = AppDB.get().getOrCreate(focusBook.bookLocalUrl);
            AppDB.get().updateOrSave(meta);
            IMG.loadCoverPageWithEffect(meta.getPath(), IMG.getImageSize());
        }

        Ion.with(context)
                .load(Url.addToMybooks)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("bookId", focusBook.bookId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        if (error == null) {
                            try {
                                JSONObject resObject = new JSONObject(result.toString());
                                if (resObject.getString("res").equals("success")||
                                        (resObject.getString("res").equals("fail") && resObject.getString("err_msg").equals("已添加"))) {

//                                    addOrRemoveAttentionApiWork(true);
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

    public void addOrRemoveAttentionApiWork(boolean closeOrDownload) {
        Global.showLoading(context, "generate_report");
        Ion.with(context)
                .load(Url.bookStateChangeOperation)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .setBodyParameter("bookId", focusBook.bookId)
                .setBodyParameter("isAttention", focusBook.bookStatus.isAttention)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Global.hideLoading();
                        Log.i(TAG, "result => " + result);
                        if (e == null) {
                            try {
                                JSONObject resObj = new JSONObject(result);
                                if (resObj.getString("res").equals("success")) {
                                    db.updateBookStateData(focusBook.bookStatus, focusBook.bookId);
                                    onDownloadBookListner.onFinishDownload(focusBook, closeOrDownload);
                                    dismiss();
                                } else {
                                    Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

    }

    public interface OnDownloadBookListenner {
        void onFinishDownload(Book book, Boolean isSuccess);
    }
}
