package ours.china.hours.Fragment.HomeTab;

import android.content.Context;
import android.content.Intent;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.Common.Utils.ItemOffsetDecoration;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class HomeFragment extends Fragment {

    ArrayList<Book> mBookList;
    HomeBookAdapter adapter;

    private RecyclerView recyclerBooksView;
    private RelativeLayout relTypeBook;
    ImageView imgSort;
    ImageView imgSearch;
    TextView txtTypeBook;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);

        init(rootView);
        popupWindowWork(inflater);
        event(rootView);

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
//        recyclerBooksView.setLayoutManager(gridLayoutManager);
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.temp_spaec);
//        recyclerBooksView.addItemDecoration(itemOffsetDecoration);
//        recyclerBooksView.setAdapter(adapter);

        // popupWindowWork.
        relTypeBook = view.findViewById(R.id.relTypeBook);
        imgSort = view.findViewById(R.id.imgSort);

        txtTypeBook = view.findViewById(R.id.txtTypeBook);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void popupWindowWork(LayoutInflater inflater) {

        // for popupWindow to float
        View view1 = inflater.inflate(R.layout.popup1, null);
        TextView allBooks = view1.findViewById(R.id.txtAllBooks);
        allBooks.setText("全都（" + 1000 + ")");
        final PopupWindow popupWindow1 = new PopupWindow(view1, 180, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        relTypeBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow1.showAsDropDown(view);
//            }
//        });

        View view2 = inflater.inflate(R.layout.popup2, null);
        final PopupWindow popupWindow2 = new PopupWindow(view2, 180, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.showAsDropDown(view);
            }
        });

        // for event when we pressed item in popupWindow.
        LinearLayout linAllBooks = view1.findViewById(R.id.linAllBooks);
        linAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("全都(1000)");
                popupWindow1.dismiss();
            }
        });

        LinearLayout linRecommended = view1.findViewById(R.id.linRecommended);
        linRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("推荐(5)");
                popupWindow1.dismiss();
            }
        });

        LinearLayout linNatural = view1.findViewById(R.id.linNatural);
        linNatural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("自然科学(9)");
                popupWindow1.dismiss();
            }
        });

        LinearLayout linHuman = view1.findViewById(R.id.linHuman);
        linHuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("人文社科(2)");
                popupWindow1.dismiss();
            }
        });

        LinearLayout linLiterature = view1.findViewById(R.id.linLiterature);
        linLiterature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("文学艺术(7)");
                popupWindow1.dismiss();
            }
        });

        RelativeLayout relFollow = view1.findViewById(R.id.relFollow);
        relFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("关注(5)");
                popupWindow1.dismiss();
            }
        });

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
    }


}