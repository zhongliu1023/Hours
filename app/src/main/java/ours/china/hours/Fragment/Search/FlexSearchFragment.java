package ours.china.hours.Fragment.Search;

import android.os.Bundle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.FlexboxAdapter;
import ours.china.hours.BookLib.artifex.mupdf.fitz.Context;
import ours.china.hours.Common.Interfaces.SearchItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.R;

public class FlexSearchFragment extends Fragment {

    public ArrayList<String> flexStringArray;
    public RecyclerView flexRecyclerView;
    public FlexboxAdapter adapter;

    TextView txtClearSearchHistory, searchRecord;
    SharedPreferencesManager sessionManager;
    SearchItemInterface searchItemInterface;

    public FlexSearchFragment(SearchItemInterface searchActivity) {
        searchItemInterface = searchActivity;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flexsearch, container, false);
        init(view);
        event(view);

        return view;
    }

    public void init(View rootView) {

        sessionManager = new SharedPreferencesManager(getActivity());
        flexStringArray = UsersManagement.getFlexStrings(sessionManager);
        // for flexbox recyclerView
        flexRecyclerView = rootView.findViewById(R.id.flexbox_recyclerview);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        flexRecyclerView.setLayoutManager(flexboxLayoutManager);
        adapter = new FlexboxAdapter(flexStringArray, getActivity(), new SearchItemInterface() {
            @Override
            public void onClickSearchItem(String keyword) {
                searchItemInterface.onClickSearchItem(keyword);
            }
        });
        flexRecyclerView.setAdapter(adapter);
    }

    public void event(View rootView) {
        txtClearSearchHistory = rootView.findViewById(R.id.clearSearchHistory);
        txtClearSearchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexStringArray.clear();
                UsersManagement.setFlexStrings(flexStringArray, sessionManager);
                adapter.notifyDataSetChanged();
            }
        });
        searchRecord = rootView.findViewById(R.id.searchRecord);
        searchRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexStringArray.add(Global.SearchQuery);
                UsersManagement.setFlexStrings(flexStringArray, sessionManager);
                adapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
