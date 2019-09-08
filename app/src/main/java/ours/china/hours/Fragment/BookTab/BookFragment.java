package ours.china.hours.Fragment.BookTab;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import ours.china.hours.Activity.FavoritesActivity;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Common.Utils.ItemOffsetDecoration;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class BookFragment extends Fragment implements BookItemInterface {

    String tempPopupWindow2String = "默认";

    ArrayList<Book> mBookList;
    HomeBookAdapter adapter;
    RelativeLayout maskLayer;

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerBooksView;
    private RelativeLayout relTypeBook;
    ImageView imgSort;
    ImageView imgSearch;
    ImageView imgArrow;
    TextView txtTypeBook;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_tab, container, false);

        init(rootView);
        popupWindowWork(inflater);
        event(rootView);

        return rootView;
    }

    public void init(View view) {

        // recyclerViewWork.
        recyclerBooksView = view.findViewById(R.id.recycler_books);

        mBookList = new ArrayList<>();
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
//        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));

        adapter = new HomeBookAdapter(mBookList, getActivity(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerBooksView.setLayoutManager(gridLayoutManager);
        recyclerBooksView.setAdapter(adapter);

        // popupWindowWork.
        relTypeBook = view.findViewById(R.id.relTypeBook);
        imgSort = view.findViewById(R.id.imgSort);
        txtTypeBook = view.findViewById(R.id.txtTypeBook);
        maskLayer = view.findViewById(R.id.maskLayer);

        txtTypeBook.setText(getString(R.string.popup3_all, "90"));
        imgArrow = view.findViewById(R.id.imgArrow);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    PopupWindow popupWindow1;
    PopupWindow popupWindow2;
    TextView txtAllBooks, txtRead, txtUnread, txtDownloaded, txtFavorites;
    LinearLayout linAllBooks, linRead, linUnread, linDownloaded, linFavorites;
    LinearLayout linDefault, linRecent;
    RelativeLayout relTitle, relAuthor;
    TextView txtDefault, txtRecent, txtTitle, txtAuthor;
    ImageView imgTitle, imgAuthor;

    public void popupWindowWork(LayoutInflater inflater) {

        popupWindowInit(inflater);

        relTypeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskLayer.setVisibility(View.VISIBLE);
                allTextPopupWindow1();
                judgePopupWindow1();
                popupWindow1.showAsDropDown(view);
                imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.retract_icon));
                popupWindow2.dismiss();
            }
        });

        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTextImagePopupWindow2();
                judgePopupWindow2();
                popupWindow2.showAsDropDown(view);
                maskLayer.setVisibility(View.VISIBLE);
                popupWindow1.dismiss();
            }
        });

        // for event when we pressed item in popupWindow.
        linAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("全都(1000)");
                dismissPopupWindow1();
            }
        });

        linDownloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("已下载");
                dismissPopupWindow1();
            }
        });

        linRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("已读");
                dismissPopupWindow1();
            }
        });

        linUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("未读");
                dismissPopupWindow1();
            }
        });


        linFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("收藏夹");
                maskLayer.setVisibility(View.GONE);
                dismissPopupWindow1();

                Intent intent = new Intent(getActivity(), FavoritesActivity.class);
                startActivity(intent);
            }
        });


        linRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "最近";
                maskLayer.setVisibility(View.GONE);
                popupWindow2.dismiss();

            }
        });

        relTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "标题";
                maskLayer.setVisibility(View.GONE);
                popupWindow2.dismiss();
            }
        });

        relAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "作者";
                maskLayer.setVisibility(View.GONE);
                popupWindow2.dismiss();
            }
        });

        maskLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPopupWindow1();
                popupWindow2.dismiss();
            }
        });

    }

    public void dismissPopupWindow1() {
        popupWindow1.dismiss();
        maskLayer.setVisibility(View.GONE);
        imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.pulldown_icon));
    }

    public void allTextPopupWindow1() {
        txtAllBooks.setTextColor(getResources().getColor(R.color.alpa_90));
        txtDownloaded.setTextColor(getResources().getColor(R.color.alpa_90));
        txtRead.setTextColor(getResources().getColor(R.color.alpa_90));
        txtUnread.setTextColor(getResources().getColor(R.color.alpa_90));
        txtFavorites.setTextColor(getResources().getColor(R.color.alpa_90));
    }

    public void allTextImagePopupWindow2() {
        txtRecent.setTextColor(getResources().getColor(R.color.alpa_90));
        txtTitle.setTextColor(getResources().getColor(R.color.alpa_90));
        txtAuthor.setTextColor(getResources().getColor(R.color.alpa_90));

        imgTitle.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.positive_sequence_icon)));
        imgAuthor.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.reverse_order_icon)));
    }

    public void popupWindowInit(LayoutInflater inflater) {

        // for popupWindow 1
        View view1 = inflater.inflate(R.layout.popup3, null);

        txtAllBooks = view1.findViewById(R.id.txtAllBooks);
        txtDownloaded = view1.findViewById(R.id.txtDownloaded);
        txtRead = view1.findViewById(R.id.txtRead);
        txtUnread = view1.findViewById(R.id.txtUnread);
        txtFavorites = view1.findViewById(R.id.txtFavorites);

        linAllBooks = view1.findViewById(R.id.linAllBooks);
        linDownloaded = view1.findViewById(R.id.linDownloaded);
        linRead = view1.findViewById(R.id.linRead);
        linUnread = view1.findViewById(R.id.linUnread);
        linFavorites = view1.findViewById(R.id.linFavorites);

        popupWindow1 = new PopupWindow(view1, 180, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow1.setAnimationStyle(R.style.popupwindowAnimation);

        // for popupWindow 2
        View view2 = inflater.inflate(R.layout.popup4, null);

        linRecent = view2.findViewById(R.id.linRecent);
        relTitle = view2.findViewById(R.id.relTitle);
        relAuthor = view2.findViewById(R.id.relAuthor);

        txtRecent = view2.findViewById(R.id.txtRecent);
        txtTitle = view2.findViewById(R.id.txtTitle);
        txtAuthor = view2.findViewById(R.id.txtAuthor);

        imgTitle = view2.findViewById(R.id.imgTitle);
        imgAuthor = view2.findViewById(R.id.imgAuthor);

        popupWindow2 = new PopupWindow(view2, 180, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow2.setAnimationStyle(R.style.popupwindowAnimation);
    }

    public void judgePopupWindow1() {

        String tempStr = txtTypeBook.getText().toString();
        if (tempStr.contains("全都")) {
            allTextPopupWindow1();
            txtAllBooks.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("已下载")) {
            allTextPopupWindow1();
            txtDownloaded.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("已读")) {
            allTextPopupWindow1();
            txtRead.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("未读")) {
            allTextPopupWindow1();
            txtUnread.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("收藏夹")) {
            allTextPopupWindow1();
            txtFavorites.setTextColor(getResources().getColor(R.color.pink));
        }
    }

    public void judgePopupWindow2() {
        if (tempPopupWindow2String.equals("最近")) {
            allTextImagePopupWindow2();
            txtRecent.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempPopupWindow2String.equals("标题")) {
            allTextImagePopupWindow2();
            txtTitle.setTextColor(getResources().getColor(R.color.pink));
            imgTitle.setImageDrawable(changeImageColor(getResources().getColor(R.color.pink), getResources().getDrawable(R.drawable.positive_sequence_icon)));
        } else if (tempPopupWindow2String.equals("作者")) {
            allTextImagePopupWindow2();
            txtAuthor.setTextColor(getResources().getColor(R.color.pink));
            imgAuthor.setImageDrawable(changeImageColor(getResources().getColor(R.color.pink), getResources().getDrawable(R.drawable.reverse_order_icon)));
        }

    }

    public void event(View rootView) {
        imgSearch = rootView.findViewById(R.id.search_image);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }

    @Override
    public void onClickBookItem(String uri) {

    }
}
