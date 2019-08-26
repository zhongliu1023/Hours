package ours.china.hours.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import ours.china.hours.Adapter.FlexboxAdapter;
import ours.china.hours.Fragment.FragmentUtil;
import ours.china.hours.Fragment.Search.FlexSearchFragment;
import ours.china.hours.Fragment.Search.SearchResultFragment;
import ours.china.hours.R;

public class SearchActivity extends AppCompatActivity {

    public ImageView imgBack;
    public TextView txtSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
        event();
    }

    public void init(){
        FlexSearchFragment flexSearchFragment = new FlexSearchFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, flexSearchFragment, "flexSearchFragment");
        transaction.commit();
    }

    public void event() {
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getFragmentManager().getBackStackEntryCount() == 0) {
                    SearchActivity.super.onBackPressed();
                }
                else {
                    getFragmentManager().popBackStack();
                }
            }
        });

        txtSearch = findViewById(R.id.txtSearch);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // when we go from fragment to fragment or go back from fragment to fragment, we use "stack" using FragmentUtil to solve relationship between them correctly.
                // In Fragment Stack, we use "Tag"
                Fragment searchResultFragment = FragmentUtil.getFragmentByTagName(getSupportFragmentManager(), "searchResultFragment");
                if (searchResultFragment == null) {
                    searchResultFragment = new SearchResultFragment();
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, searchResultFragment, "searchResultFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                FragmentUtil.printActivityFragmentList(getSupportFragmentManager());
            }
        });
    }

}
