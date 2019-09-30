package ours.china.hours.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Adapter.AttentionBookAdapter;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.Common.Interfaces.BookItemEditInterface;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.Model.QueryRequest;
import ours.china.hours.R;

public class AttentionActivity extends AppCompatActivity implements BookItemInterface, BookItemEditInterface {
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

        init();
        getDataFromServer();
        event();
    }


    public void init() {
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

    @Override
    protected void onPause() {

        removeSelectedState();
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }


}
