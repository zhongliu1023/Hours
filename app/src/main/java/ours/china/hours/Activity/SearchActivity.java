package ours.china.hours.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v4.app.INotificationSideChannel;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import net.arnx.wmf2svg.Main;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ours.china.hours.Adapter.FlexboxAdapter;
import ours.china.hours.BookLib.artifex.mupdf.fitz.Image;
import ours.china.hours.Common.Interfaces.SearchItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Fragment.FragmentUtil;
import ours.china.hours.Fragment.Search.FlexSearchFragment;
import ours.china.hours.Fragment.Search.SearchResultFragment;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.R;

public class SearchActivity extends AppCompatActivity implements SearchItemInterface {

    public SearchView searchBook;
    public TextView txtSearch;
    SharedPreferencesManager sessionManager;
    ImageView imgBack;
    RelativeLayout relSearchBook;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
        event();
    }

    public void init(){
        sessionManager = new SharedPreferencesManager(this);
        FlexSearchFragment flexSearchFragment = new FlexSearchFragment(new SearchItemInterface() {
            @Override
            public void onClickSearchItem(String keyword) {
                replaceSearchBook(keyword);
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, flexSearchFragment, "flexSearchFragment");
        transaction.commit();
    }

    public void event() {
        searchBook = findViewById(R.id.searchBook);
        searchBook.setQueryHint("搜索书名、作者、出版社");
        searchBook.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Global.SearchQuery = s;
                return false;
            }
        });

        relSearchBook = findViewById(R.id.relSearchBook);
        relSearchBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBook.onActionViewExpanded();
            }
        });

        txtSearch = findViewById(R.id.txtSearch);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceSearchBook(searchBook.getQuery().toString());
            }
        });
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    void replaceSearchBook(String keywords){

        // I will check again below code.
//        Fragment searchResultFragment = FragmentUtil.getFragmentByTagName(getSupportFragmentManager(), "searchResultFragment");
//        if (searchResultFragment == null) {
//            searchResultFragment = new SearchResultFragment(keywords);
//        }
        Fragment searchResultFragment = new SearchResultFragment(keywords);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, searchResultFragment, "searchResultFragment");
        transaction.addToBackStack(null);
        transaction.commit();

        FragmentUtil.printActivityFragmentList(getSupportFragmentManager());
    }

    @Override
    public void onClickSearchItem(String keyword) {

    }
}
