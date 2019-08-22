package ours.china.hours.Fragment.HistoryTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.ReadingStatusBookAdapter;
import ours.china.hours.Model.ReadingStatusBook;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class HistoryFragment extends Fragment {

    ArrayList<ReadingStatusBook> mBookList;
    RecyclerView recyclerBookView;
    ReadingStatusBookAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_tab, container, false);

        recyclerBookViewInit(rootView);

        return rootView;
    }

    public void recyclerBookViewInit(View view) {
        recyclerBookView = view.findViewById(R.id.recycler_book_reading_now);

        mBookList = new ArrayList<>();
        mBookList.add(new ReadingStatusBook("hello", "Huang Xin", "29", "14.5", "99"));
        mBookList.add(new ReadingStatusBook("hello", "Huang Xin", "29", "14.5", "99"));
        mBookList.add(new ReadingStatusBook("hello", "Huang Xin", "29", "14.5", "99"));
        mBookList.add(new ReadingStatusBook("hello", "Huang Xin", "29", "14.5", "99"));

        adapter = new ReadingStatusBookAdapter(getActivity(), mBookList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerBookView.setLayoutManager(manager);

        recyclerBookView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}