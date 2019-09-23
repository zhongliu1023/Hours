package ours.china.hours.Fragment.HistoryTab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

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
import ours.china.hours.Common.Interfaces.SelectStatePositionInterface;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.BookStatus;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class HistoryFragment extends Fragment implements SelectStatePositionInterface {

    PieChartView pieChartView;
    RecyclerView recyclerReadingBookNow, recyclerReadingBookCompleted;

    TextView txtMoreRecently, txtMoreCompleted, txtReadTime, timePerPerson, txtRanking, txtNaturalCount, txtHumanCount, txtLiteratureCount;

    TextView readCompletedCount;
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
        readingBookStatusAdapter = new ReadingStatusBookAdapter(getContext(), bookArrayList, this);
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getActivity());
        recyclerReadingBookNow.setLayoutManager(manager1);
        recyclerReadingBookNow.setAdapter(readingBookStatusAdapter);
        recyclerReadingBookNow.setNestedScrollingEnabled(false);

        recyclerReadingBookCompleted = rootView.findViewById(R.id.recycler_book_reading_completed);
        readingBookCompleteAdapter = new ReadingCompleteStatusBookAdapter(getContext(), completeStatusBooks);
        recyclerReadingBookCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerReadingBookCompleted.setAdapter(readingBookCompleteAdapter);
        recyclerReadingBookCompleted.setNestedScrollingEnabled(false);

        txtReadTime = rootView.findViewById(R.id.txtReadTime);
        timePerPerson = rootView.findViewById(R.id.timePerPerson);
        txtRanking = rootView.findViewById(R.id.txtRanking);
        txtNaturalCount = rootView.findViewById(R.id.txtNaturalCount);
        txtHumanCount = rootView.findViewById(R.id.txtHumanCount);
        txtLiteratureCount = rootView.findViewById(R.id.txtLiteratureCount);
        readCompletedCount = rootView.findViewById(R.id.readCompletedCount);
    }

    public void getDataFromLocalDB() {
        fetchBooksStatistics();

        Global.readingNowOrHistory = "history";
        db = new DBController(getActivity());
        ArrayList<Book> tempBookArrayList = db.getAllData();
        if (tempBookArrayList != null && tempBookArrayList.size() != 0) {
            completeStatusBooks.clear();
            bookArrayList.clear();
            for (int i = 0; i < tempBookArrayList.size(); i++) {
                Book one = (Book) tempBookArrayList.get(i);
                BookStatus tempBook = db.getBookStateData(one.bookId);
                if (tempBook != null) {
                    one.bookStatus = tempBook;
                }
                if (one.bookStatus != null && one.pageCount.equals(String.valueOf(one.bookStatus.pages.split(",").length))) {
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

    public void fetchBooksStatistics(){
        Ion.with(getActivity())
                .load(Url.mybooksStatistics)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());
                                if (resObj.getString("res").toLowerCase().equals("success")) {
                                    JSONObject statistics = new JSONObject(resObj.getString("statistics"));
                                    reloadStatistics(statistics);
                                } else {
                                    Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void reloadStatistics(JSONObject statistics){
        try {
            String totalBooks = statistics.getString("totalBooks");
            String zhiren = statistics.getString("zhiren");
            String renwen = statistics.getString("renwen");
            String wenxie = statistics.getString("wenxie");
            String totalReadBooks = statistics.getString("totalReadBooks");
            readCompletedCount.setText(totalReadBooks);
            String zhirenRead = statistics.getString("zhirenRead");
            txtNaturalCount.setText(getString(R.string.popup1_natural, zhirenRead));
            String renwenRead = statistics.getString("renwenRead");
            txtHumanCount.setText(getString(R.string.popup1_human, renwenRead));
            String wenxieRead = statistics.getString("wenxieRead");
            txtLiteratureCount.setText(getString(R.string.popup1_literature, wenxieRead));

            String attention = statistics.getString("attention");
            String recommended = statistics.getString("recommended");

            float hours = ((float) (Integer.parseInt(statistics.getString("totalReadTime")) / 1000)) / 3600;
            txtReadTime.setText(String.format("%.2f", hours));

            txtRanking.setText(statistics.getString("ranking"));
            String totalUsers = statistics.getString("totalUsers");
            hours = ((float) (Float.parseFloat(statistics.getString("averageReadTime")) / 1000)) / 3600;
            timePerPerson.setText(String.format("%.2f", hours));

            List pieData = new ArrayList();
            if (Integer.parseInt(zhirenRead) == 0 && Integer.parseInt(renwenRead) == 0 && Integer.parseInt(wenxieRead) == 0){
                pieData.add(new SliceValue(1, getResources().getColor(R.color.lt_grey_alpha2)));

            }else{
                pieData.add(new SliceValue(Integer.parseInt(zhirenRead), getResources().getColor(R.color.natural_color)));
                pieData.add(new SliceValue(Integer.parseInt(renwenRead), getResources().getColor(R.color.human_color)));
                pieData.add(new SliceValue(Integer.parseInt(wenxieRead), getResources().getColor(R.color.literature_color)));

            }
            PieChartData pieChartData = new PieChartData(pieData);
            pieChartData.setHasCenterCircle(true);

            pieChartView.setPieChartData(pieChartData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void pieChartInit(View rootView) {
        pieChartView = rootView.findViewById(R.id.pieChart);

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

    @Override
    public void onClickStatePosition(Book selectedBook, int pageNumber) {

    }
}