package ours.china.hours.Fragment.HistoryTab;

import android.content.Intent;
import android.os.Bundle;
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
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Activity.ReadingCompleteBookActivity;
import ours.china.hours.Activity.ReadingNowBookActivity;
import ours.china.hours.Adapter.ReadingCompleteStatusBookAdapter;
import ours.china.hours.Adapter.ReadingStatusBookAdapter;
import ours.china.hours.Model.ReadingCompleteStatusBook;
import ours.china.hours.Model.ReadingStatusBook;
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

    private ArrayList<ReadingStatusBook> statusBooks;
    private ArrayList<ReadingCompleteStatusBook>  completeStatusBooks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_tab, container, false);

        init(rootView);
        pieChartInit(rootView);
        event(rootView);

        return rootView;
    }

    public void init(View rootView) {
        recyclerReadingBookNow = rootView.findViewById(R.id.recycler_book_reading_now);
        recyclerReadingBookCompleted = rootView.findViewById(R.id.recycler_book_reading_completed);

        statusBooks = new ArrayList<>();
        completeStatusBooks = new ArrayList<>();

        statusBooks.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        statusBooks.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));
        statusBooks.add(new ReadingStatusBook("hello", "百年孤独", "24", "20.6", "29", "19-10-01"));

        completeStatusBooks.add(new ReadingCompleteStatusBook("hello", "百年孤独", "20.3", "20"));
        completeStatusBooks.add(new ReadingCompleteStatusBook("hello", "百年孤独", "20.3", "20"));

        readingBookStatusAdapter = new ReadingStatusBookAdapter(getContext(), statusBooks);
        readingBookCompleteAdapter = new ReadingCompleteStatusBookAdapter(getContext(), completeStatusBooks);

        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager manager2 = new LinearLayoutManager(getActivity());
        recyclerReadingBookNow.setLayoutManager(manager1);
        recyclerReadingBookCompleted.setLayoutManager(manager2);

        recyclerReadingBookNow.setAdapter(readingBookStatusAdapter);
        recyclerReadingBookCompleted.setAdapter(readingBookCompleteAdapter);
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