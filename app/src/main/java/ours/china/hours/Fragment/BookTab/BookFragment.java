package ours.china.hours.Fragment.BookTab;

import android.os.Bundle;
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

import java.util.ArrayList;

import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Common.Utils.ItemOffsetDecoration;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class BookFragment extends Fragment {
    ArrayList<Book> mBookList;
    HomeBookAdapter adapter;
    private RecyclerView recyclerBooksView;
    private RelativeLayout relTypeBook;
    ImageView imgSort;

    private SharedPreferencesManager sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_tab, container, false);
        init(rootView);
        popupWindowWork(inflater);

        return rootView;
    }

    public void init(View view) {

        // recyclerViewWork.
        recyclerBooksView = view.findViewById(R.id.recycler_books);

        mBookList = new ArrayList<>();
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));
        mBookList.add(new Book("hello", "百年孤独", "downloaded", "nonRead"));

        adapter = new HomeBookAdapter(mBookList, getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerBooksView.setLayoutManager(gridLayoutManager);
        recyclerBooksView.setAdapter(adapter);

        // popupWindowWork.
        relTypeBook = view.findViewById(R.id.relTypeBook);
        imgSort = view.findViewById(R.id.imgSort);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void popupWindowWork(LayoutInflater inflater) {

        View view1 = inflater.inflate(R.layout.popup3, null);
        TextView allBooks = view1.findViewById(R.id.txtAllBooks);
        allBooks.setText("全都（" + 1000 + ")");
        final PopupWindow popupWindow1 = new PopupWindow(view1, 180, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        relTypeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow1.showAsDropDown(view);
            }
        });

        View view2 = inflater.inflate(R.layout.popup2, null);
        final PopupWindow popupWindow2 = new PopupWindow(view2, 180, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.showAsDropDown(view);
            }
        });
    }
}
