package ours.china.hours.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Adapter.AttentionBookAdapter;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.BookItemEditInterface;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Fragment.HomeTab.HomeFragment;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.Model.QueryRequest;
import ours.china.hours.R;
import ours.china.hours.Services.BookFile;
import ours.china.hours.Services.DownloadingService;
import pub.devrel.easypermissions.EasyPermissions;

import static ours.china.hours.Constants.ActivitiesCodes.CONNECTED_FILE_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.FINISHED_DOWNLOADING_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.PROGRESS_UPDATE_ACTION;

public class AttentionActivity extends AppCompatActivity implements BookItemInterface, BookItemEditInterface, BookDetailsDialog.OnDownloadBookListenner {
    private final String TAG = "AttentionActivity";

    ImageView imgBack;
    RelativeLayout mainToolbar, otherToolbar;
    TextView txtToolbarComplete, txtToolbarDelete;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerAttention;
    AttentionBookAdapter adapter;

    SharedPreferencesManager sessionManager;
    DBController db = null;
    BookDetailsDialog bookDetailsDialog;

    ArrayList<Book> mBookList = new ArrayList<>();
    ArrayList<Book> selectedBookLists = new ArrayList<>();

    private QueryBook.Category category = QueryBook.Category.ALL;
    private QueryRequest req;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTED_FILE_ACTION);
        filter.addAction(PROGRESS_UPDATE_ACTION);
        filter.addAction(FINISHED_DOWNLOADING_ACTION);
        LocalBroadcastManager.getInstance(AttentionActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String bookID = intent.getStringExtra("bookID");
                        switch (intent.getAction()) {
                            case CONNECTED_FILE_ACTION:
                                connectedFile(bookID);
                                break;
                            case PROGRESS_UPDATE_ACTION:
                                int progress = intent.getIntExtra("progress", -1);
                                publishProgress(bookID, progress);
                                break;
                            case FINISHED_DOWNLOADING_ACTION:
                                String path = intent.getStringExtra("path");
                                finishedDownload(bookID, path);
                                break;
                        }
                    }
                }, filter);

        init();
        getDataFromServer();
        event();

    }

    private void connectedFile(String bookID) {
        if (!Global.mBookFiles.containsKey(bookID)){
            BookFile bookFile = new BookFile(bookID, "");
            Global.mBookFiles.put(bookID, bookFile);
        }
        adapter.reloadbookwithDownloadStatus(Global.mBookFiles);
    }

    private void publishProgress(String bookID, int soFarBytes) {
        if (Global.mBookFiles.containsKey(bookID)){
            BookFile bookFile = Global.mBookFiles.get(bookID);
            bookFile.setProgress(soFarBytes);
        }else {
            BookFile bookFile = new BookFile(bookID, "");
            bookFile.setProgress(soFarBytes);
            Global.mBookFiles.put(bookID, bookFile);
        }
        adapter.reloadbookwithDownloadStatus(Global.mBookFiles);
    }

    private void finishedDownload(String bookID, String path) {

        Book focusBook = new Book();
        int itemPosition = 0;
        for (int i = 0; i < mBookList.size(); i++) {
            Book one = mBookList.get(i);
            if (one.bookId.equals(bookID)) {
                focusBook = one;
                itemPosition = i;
                break;
            }
        }

        if (Global.mBookFiles.containsKey(bookID)){
            Global.mBookFiles.remove(bookID);
        } else {
            int tempPosition = AppDB.get().getAll().size() - 1;
            focusBook.bookLocalUrl = path;
            focusBook.libraryPosition = String.valueOf(tempPosition);
            adapter.notifyItemChanged(itemPosition);
            return;
        }

        int tempPosition = 0;
        if (AppDB.get().getAll() == null || AppDB.get().getAll().size() == 0) {
            tempPosition = 0;
        } else {
            tempPosition = AppDB.get().getAll().size();
        }

        focusBook.bookLocalUrl = path;
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

        Ion.with(AttentionActivity.this)
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

                                    adapter.reloadBookList(mBookList);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void init() {
        db = new DBController(AttentionActivity.this);
        sessionManager = new SharedPreferencesManager(AttentionActivity.this);

        // primary state
        mainToolbar = findViewById(R.id.mainToolbar);
        otherToolbar = findViewById(R.id.otherToolbar);
        txtToolbarComplete = findViewById(R.id.txtToolbarComplete);
        txtToolbarDelete = findViewById(R.id.txtToolbarDelete);

        // for recyclerView.
        recyclerAttention = findViewById(R.id.recyclerAttention);
        adapter = new AttentionBookAdapter(mBookList, AttentionActivity.this, this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AttentionActivity.this, 3);
        recyclerAttention.setLayoutManager(gridLayoutManager);
        recyclerAttention.setAdapter(adapter);

    }

    public void getDataFromServer() {
        Global.showLoading(AttentionActivity.this,"generate_report");

        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("category", Collections.singletonList(category.toString()));

        Ion.with(this)
                .load(Url.query_books)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameters(params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        Log.i(TAG, "result => " + result);
                        Global.hideLoading();

                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").toLowerCase().equals("success")) {

                                    JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<Book>>() {}.getType();
                                    mBookList = gson.fromJson(dataArray.toString(), type);
                                    getAttentionBookData();
                                } else {
                                    Toast.makeText(AttentionActivity.this, "无法从服务器获取数据。", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(AttentionActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getAttentionBookData() {

        ArrayList<Book> tempBooks = new ArrayList<>();
        for (Book one : mBookList) {
            if (Global.currentUser.attentionBookIds.contains(one.bookId)) {
                tempBooks.add(one);
            }
        }

        mBookList = tempBooks;
        getTotalData();
    }

    public void getTotalData() {
        ArrayList<Book> books = db.getAllData();
        if (books == null) {
            adapter.reloadBookList(mBookList);
            return;
        }

        for (int i = 0; i < mBookList.size(); i++) {
            for (int j = 0; j < books.size(); j++) {
                Book one = mBookList.get(i);
                Book oneLocal = books.get(j);

                if (one.bookId != null && oneLocal.bookId != null && one.bookId.equals(oneLocal.bookId)) {
                    one.bookLocalUrl = oneLocal.bookLocalUrl;
                    one.bookImageLocalUrl = oneLocal.bookImageLocalUrl;
                    one.libraryPosition = oneLocal.libraryPosition;
                    break;
                }
            }
        }

        adapter.reloadBookList(mBookList);
    }

    public void reflectAttentionBooksStateToServer(String attentionBookIds) {
        Global.showLoading(AttentionActivity.this,"generate_report");
        Ion.with(this)
                .load(Url.update_profile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("attentionBookIds", attentionBookIds)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        Log.i(TAG, "result => " + result);
                        Global.hideLoading();

                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").toLowerCase().equals("success")) {
                                    Global.hideLoading();

                                    Global.bookAction = QueryBook.BookAction.NONE;
                                    selectedBookLists.clear();

                                    Global.currentUser.attentionBookIds = resObj.getString("attentionBookIds");
                                    UsersManagement.saveCurrentUser(Global.currentUser, sessionManager);

                                    adapter.reloadBookList(mBookList);

                                    mainToolbar.setVisibility(View.VISIBLE);
                                    otherToolbar.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(AttentionActivity.this, "无法从服务器获取数据。", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(AttentionActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    public void event() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttentionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        txtToolbarComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedState();
            }
        });

        txtToolbarDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject attentionBookIds = new JSONObject();

                JSONArray tempArray = new JSONArray();
                for (Book one : selectedBookLists) {
                    mBookList.remove(one);
                    tempArray.put(Integer.parseInt(one.bookId));
                }
//                String tempArrayString = tempArray.toString().replace("\\", "");

                try {
                    attentionBookIds.put("req", QueryRequest.DELETE.toString());
                    attentionBookIds.put("bookIds", tempArray);

                    Log.i(TAG, "Request parameter => " + attentionBookIds.toString());

                    reflectAttentionBooksStateToServer(attentionBookIds.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    public void removeSelectedState() {
        Global.bookAction = QueryBook.BookAction.NONE;
        selectedBookLists.clear();

        mainToolbar.setVisibility(View.VISIBLE);
        otherToolbar.setVisibility(View.GONE);

        adapter.reloadBookList(mBookList);
    }

    @Override
    public void onClickBookItem(Book selectedBook, int position) {
        if (Global.bookAction == QueryBook.BookAction.SELECTTION) {
            if (selectedBookLists.contains(selectedBook)) {
                selectedBookLists.remove(selectedBook);
            } else {
                selectedBookLists.add(selectedBook);
            }

            adapter.reloadBookListWithSelection(selectedBookLists);

            if (selectedBookLists.size() == 0) {
                txtToolbarDelete.setEnabled(false);
                txtToolbarDelete.setTextColor(getResources().getColor(R.color.alpa_40));
            } else {
                txtToolbarDelete.setEnabled(true);
                txtToolbarDelete.setTextColor(getResources().getColor(R.color.alpa_90));
            }
        } else {
            if (!selectedBook.bookLocalUrl.equals("")) {
                BookManagement.saveFocuseBook(selectedBook, sessionManager);
                gotoReadingViewFile();
                return;
            }
            BookManagement.saveFocuseBook(selectedBook, sessionManager);

            if (EasyPermissions.hasPermissions(AttentionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                bookDetailsDialog = new BookDetailsDialog(AttentionActivity.this, position, AttentionActivity.this);
                bookDetailsDialog.show();

                // set needed frame of dialog. Without this code, all the component of the dialog's layout don't have original size.
                Window rootWindow = AttentionActivity.this.getWindow();
                Rect displayRect = new Rect();
                rootWindow.getDecorView().getWindowVisibleDisplayFrame(displayRect);
                bookDetailsDialog.getWindow().setLayout(displayRect.width(), displayRect.height());
                bookDetailsDialog.setCanceledOnTouchOutside(false);
            }else{
                EasyPermissions.requestPermissions(AttentionActivity.this, getString(R.string.write_file), 300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

    }

    @Override
    public void onLongClickBookItem(Book selectedBook) {
        Global.bookAction = QueryBook.BookAction.SELECTTION;

        mainToolbar.setVisibility(View.GONE);
        otherToolbar.setVisibility(View.VISIBLE);

        selectedBookLists.add(selectedBook);

        txtToolbarDelete.setEnabled(true);
        txtToolbarDelete.setTextColor(getResources().getColor(R.color.alpa_90));
        adapter.reloadBookListWithSelection(selectedBookLists);
    }


    void gotoReadingViewFile(){
        Book focuseBook = BookManagement.getFocuseBook(sessionManager);
        List<FileMeta> localBooks = AppDB.get().getAll();
        int tempLibraryPosition = Integer.parseInt(focuseBook.libraryPosition);

        ExtUtils.openFile(AttentionActivity.this, localBooks.get(tempLibraryPosition));
    }


    @Override
    protected void onPause() {

        removeSelectedState();
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }


    @Override
    public void onFinishDownload(Book book, Boolean isSuccess, int position) {
        if (isSuccess){
            for (int i = 0; i < mBookList.size(); i++) {
                Book one = mBookList.get(i);
                if (one.bookId != null && book.bookId != null && one.bookId.equals(book.bookId)) {
                    one.bookLocalUrl = book.bookLocalUrl;
                    one.bookImageLocalUrl = book.bookImageLocalUrl;
                    one.libraryPosition = book.libraryPosition;
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
            gotoReadingViewFile();
        }
    }

    @Override
    public void onStartDownload(Book book, int position) {
        if (!Global.mBookFiles.containsKey(book.bookId)){
            BookFile bookFile = new BookFile(book.bookId, Url.domainUrl + book.bookNameUrl);
            Global.mBookFiles.put(book.bookId, bookFile);
            Intent intent = new Intent(AttentionActivity.this, DownloadingService.class);
            intent.putParcelableArrayListExtra("bookFile", new ArrayList<BookFile>(Arrays.asList(bookFile)));
            AttentionActivity.this.startService(intent);
        }
    }
}
