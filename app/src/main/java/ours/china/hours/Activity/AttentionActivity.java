package ours.china.hours.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.Common.Interfaces.BookItemEditInterface;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;

public class AttentionActivity extends AppCompatActivity implements BookItemInterface, BookItemEditInterface {
    private final String TAG = "AttentionActivity";

    ImageView imgBack;
    TextView txtDelete;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerAttention;
    AttentionBookAdapter adapter;

    SharedPreferencesManager sessionManager;
    DBController db = null;
    BookDetailsDialog bookDetailsDialog;

    ArrayList<Book> mBookList;
    ArrayList<Book> selectedBookLists;
    private QueryBook.Category category = QueryBook.Category.ATEENTION;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        getDataFromServer();
        init();
        event();
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

                                } else {
                                    Toast.makeText(AttentionActivity.this, "错误", Toast.LENGTH_SHORT).show();
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

    public void init() {

        // primary state
        txtDelete = findViewById(R.id.txtDelete);
        txtDelete.setEnabled(false);
        txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));

        // for recyclerView.
        recyclerAttention = findViewById(R.id.recyclerAttention);
        adapter = new AttentionBookAdapter(mBookList, AttentionActivity.this, this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AttentionActivity.this, 3);
        recyclerAttention.setLayoutManager(gridLayoutManager);
        recyclerAttention.setAdapter(adapter);

        // for edit state.
        selectedBookLists = new ArrayList<Book>();

    }

    public void event() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.bookAction == QueryBook.BookAction.NONE) {
                    AttentionActivity.super.onBackPressed();
                } else {
                    Global.bookAction = QueryBook.BookAction.NONE;
                    adapter.reloadBookList(mBookList);
                }
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Book one : selectedBookLists) {
//
//                    one.bookStatus.isAttention = QueryBook.BookAttention.ATTENTION.toString();

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
                txtDelete.setEnabled(false);
                txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
            } else {
                txtDelete.setEnabled(true);
                txtDelete.setTextColor(getResources().getColor(R.color.alpa_90));
            }
        }
    }

    @Override
    public void onLongClickBookItem(Book selectedBook) {
        Global.bookAction = QueryBook.BookAction.SELECTTION;

        selectedBookLists.add(selectedBook);
        adapter.reloadBookListWithSelection(selectedBookLists);
    }
}
