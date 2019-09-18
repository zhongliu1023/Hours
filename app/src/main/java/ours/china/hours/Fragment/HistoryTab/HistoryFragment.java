package ours.china.hours.Fragment.HistoryTab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.ReadingCompleteBookActivity;
import ours.china.hours.Activity.ReadingNowBookActivity;
import ours.china.hours.Adapter.ReadingCompleteStatusBookAdapter;
import ours.china.hours.Adapter.ReadingStatusBookAdapter;
import ours.china.hours.DB.DBController;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.ReadingCompleteStatusBook;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class HistoryFragment extends Fragment {

    PieChartView pieChartView;
    RecyclerView recyclerReadingBookNow, recyclerReadingBookCompleted;

    TextView txtMoreRecently, txtMoreCompleted;

    ReadingCompleteStatusBookAdapter readingBookCompleteAdapter;
    ReadingStatusBookAdapter readingBookStatusAdapter;

    ArrayList<Book> bookArrayList;
    ArrayList<Book> completeStatusBooks;


    DBController db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_tab, container, false);

        bookArrayList = new ArrayList<>();
        completeStatusBooks = new ArrayList<>();
        init(rootView);
        pieChartInit(rootView);
        event(rootView);
        getDataFromLocalDB();


        return rootView;
    }

    public void init(View rootView) {
        recyclerReadingBookNow = rootView.findViewById(R.id.recycler_book_reading_now);
        readingBookStatusAdapter = new ReadingStatusBookAdapter(getContext(), bookArrayList);
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getActivity());
        recyclerReadingBookNow.setLayoutManager(manager1);
        recyclerReadingBookNow.setAdapter(readingBookStatusAdapter);
        recyclerReadingBookNow.setNestedScrollingEnabled(false);

        recyclerReadingBookCompleted = rootView.findViewById(R.id.recycler_book_reading_completed);
        readingBookCompleteAdapter = new ReadingCompleteStatusBookAdapter(getContext(), completeStatusBooks);
        recyclerReadingBookCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerReadingBookCompleted.setAdapter(readingBookCompleteAdapter);
        recyclerReadingBookCompleted.setNestedScrollingEnabled(false);

    }

    public void getDataFromLocalDB() {
        Global.readingNowOrHistory = "history";
        db = new DBController(getActivity());
        ArrayList<Book> tempBookArrayList = db.getAllData();
        if (tempBookArrayList != null && tempBookArrayList.size() != 0) {
            completeStatusBooks.clear();
            bookArrayList.clear();
            for (int i = 0; i < tempBookArrayList.size(); i++) {
                Book one = (Book) tempBookArrayList.get(i);
                Book tempBook = db.getBookStateData(one.getBookID());
                if (tempBook != null) {
                    one.setPagesArray(tempBook.getPagesArray());
                    one.setReadTime(tempBook.getReadTime());
                    one.setLastTime(tempBook.getLastTime());
                }
                if (one.getTotalPage().equals(String.valueOf(one.getPagesArray().split(",").length))) {
                    completeStatusBooks.add(one);
                } else {
                    bookArrayList.add(one);
                }
            }
            readingBookStatusAdapter.notifyDataSetChanged();
            readingBookCompleteAdapter.notifyDataSetChanged();

            Log.i("HistoryFragment", "data list => " + bookArrayList.toString() + bookArrayList.size());
        }
    }


    public void pieChartInit(View rootView) {
        pieChartView = rootView.findViewById(R.id.pieChart);

        List pieData = new ArrayList();
        pieData.add(new SliceValue(35, getResources().getColor(R.color.natural_color)));
        pieData.add(new SliceValue(5, getResources().getColor(R.color.human_color)));
        pieData.add(new SliceValue(30, getResources().getColor(R.color.literature_color)));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasCenterCircle(true);

        pieChartView.setPieChartData(pieChartData);

    }

    public void event(View rootView) {
        txtMoreRecently = rootView.findViewById(R.id.moreRecently);
        txtMoreRecently.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReadingNowBookActivity.class);
                startActivity(intent);
            }
        });

        txtMoreCompleted = rootView.findViewById(R.id.moreCompleted);
        txtMoreCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReadingCompleteBookActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}