package ours.china.hours.Activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.ReadingStatusBookAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class ReadingNowBookActivity extends AppCompatActivity {

    RecyclerView recyclerMoreReadingNowBook;
    ImageView imgBack;

    ReadingStatusBookAdapter adapter;
    ArrayList<Book> bookList;

    LinearLayout temp;
    int deviceWidth, deviceHeight, position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_now_more);

        init();
        event();
    }

    public void init() {

        //
        getDeviceWidthAndHeight();

        // for recyclerView
        temp = findViewById(R.id.tempReadingNowMore);
        recyclerMoreReadingNowBook = findViewById(R.id.moreReadingNowBook);

        bookList = new ArrayList<>();
        Book one = new Book();
        one.setBookName("Hello");
        one.setReadTime("23sdfsf");
        one.setSpecifiedTime("sdfjlskfjskfs;lfk");
        one.setLastTime("28349we8r0w8");
        one.setBookImageLocalUrl("whate 's kdjfkl");
        one.setTotalPage("60");
        one.setPagesArray("23,45,5,34");

        bookList.add(one);
        bookList.add(one);
        bookList.add(one);
        bookList.add(one);
        bookList.add(one);
        bookList.add(one);
        bookList.add(one);
        bookList.add(one);

        adapter = new ReadingStatusBookAdapter(ReadingNowBookActivity.this, bookList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ReadingNowBookActivity.this);
        recyclerMoreReadingNowBook.setLayoutManager(manager);
        recyclerMoreReadingNowBook.setAdapter(adapter);
    }
    public void event() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    ReadingNowBookActivity.this.finish();
                } else {
                    ReadingNowBookActivity.super.onBackPressed();
                }
            }
        });

//        temp.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                temp.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                for (int position = 0; position < 4; position++) {
//
//                    ViewGroup tempViewGroup = (ViewGroup) recyclerMoreReadingNowBook.findViewHolderForAdapterPosition(position).itemView;
//
//                    TextView txtStateBar = tempViewGroup.findViewById(R.id.stateBar);
//                    LinearLayout txtContainerStateBar = tempViewGroup.findViewById(R.id.containerStateBar);
//
//                    float paramMarginLeft = txtStateBar.getX();
//                    float paramMarginTop = txtStateBar.getY();
//                    int paramWidth = (int) txtStateBar.getWidth();
//                    int paramHeight = (int) txtStateBar.getHeight();
//
//                    Log.e("ReadingNowBookActivity", String.valueOf(paramMarginLeft));
//                    LOG.d("ReadingNow", paramMarginTop);
//                    LOG.d("ReadingNow", paramWidth);
//                    LOG.d("ReadingNow", paramHeight);
//
//                    float paramWidthPerPage = (int) paramWidth / Integer.parseInt(bookList.get(position).getTotalPage());
//
//                    String[] pages = bookList.get(position).getPagesArray().split(",");
//                    for (int i = 0; i < pages.length; i++) {
//
//                        TextView tv = new TextView(ReadingNowBookActivity.this);
//                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) paramWidthPerPage, paramHeight);
//                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//                        params.leftMargin = (int) (paramWidthPerPage * Integer.parseInt(pages[i]) + paramMarginLeft + deviceWidth * 1.2 / 5.6);
//                        params.topMargin = (int) (convertDpToPixel(8) + txtContainerStateBar.getHeight() * 2 / 3 + paramMarginTop);
//                        tv.setBackgroundColor(getResources().getColor(R.color.pink));
//
//                        tempViewGroup.addView(tv, params);
//                    }
//                }
//            }
//        });

//        for (position = 0; position < bookList.size(); position++) {
//            RelativeLayout relNowItem = recyclerMoreReadingNowBook.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.relNowItem);
//            Log.v("hello", "hello");
//
//            relNowItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
////                    ViewGroup tempViewGroup = (ViewGroup) recyclerMoreReadingNowBook.findViewHolderForAdapterPosition(position).itemView;
//                    relNowItem.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    TextView txtStateBar = relNowItem.findViewById(R.id.stateBar);
//                    LinearLayout txtContainerStateBar = relNowItem.findViewById(R.id.containerStateBar);
//
//                    float paramMarginLeft = txtStateBar.getX();
//                    float paramMarginTop = txtStateBar.getY();
//                    int paramWidth = (int) txtStateBar.getWidth();
//                    int paramHeight = (int) txtStateBar.getHeight();
//
//                    Log.e("ReadingNowBookActivity", String.valueOf(paramMarginLeft));
//                    LOG.d("ReadingNow", paramMarginTop);
//                    LOG.d("ReadingNow", paramWidth);
//                    LOG.d("ReadingNow", paramHeight);
//
//                    float paramWidthPerPage = (int) paramWidth / Integer.parseInt(bookList.get(position).getTotalPage());
//
//                    String[] pages = bookList.get(position).getPagesArray().split(",");
//                    for (int i = 0; i < pages.length; i++) {
//
//                        TextView tv = new TextView(ReadingNowBookActivity.this);
//                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) paramWidthPerPage, paramHeight);
//                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//                        params.leftMargin = (int) (paramWidthPerPage * Integer.parseInt(pages[i]) + paramMarginLeft + deviceWidth * 1.2 / 5.6);
//                        params.topMargin = (int) (convertDpToPixel(8) + txtContainerStateBar.getHeight() * 2 / 3 + paramMarginTop);
//                        tv.setBackgroundColor(getResources().getColor(R.color.pink));
//
//                        relNowItem.addView(tv, params);
//                    }
//                }
//            });
//        }
    }

    public void getDeviceWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
    }

    public static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

}
