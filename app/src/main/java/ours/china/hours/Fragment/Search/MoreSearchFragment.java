package ours.china.hours.Fragment.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.MoreBookAdapter;
import ours.china.hours.Model.MoreBook;
import ours.china.hours.R;

public class MoreSearchFragment extends Fragment {

    ArrayList<MoreBook> moreBooks;
    MoreBookAdapter adapter;
    RecyclerView moreSearchRecyclerView;

    TextView txtBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_book, container, false);

        recyclerViewInit(view);
        event(view);

        return view;
    }

    public void recyclerViewInit(View view) {
        moreSearchRecyclerView = view.findViewById(R.id.more_search_result);

        moreBooks = new ArrayList<>();
        moreBooks.add(new MoreBook("hello", "百年孤独", "马尔克斯"));
        moreBooks.add(new MoreBook("hello", "百年孤独", "马尔克斯"));
        moreBooks.add(new MoreBook("hello", "百年孤独", "马尔克斯"));
        moreBooks.add(new MoreBook("hello", "百年孤独", "马尔克斯"));
        moreBooks.add(new MoreBook("hello", "百年孤独", "马尔克斯"));

        adapter = new MoreBookAdapter(getActivity(), moreBooks);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        moreSearchRecyclerView.setLayoutManager(manager);

        moreSearchRecyclerView.setAdapter(adapter);
    }

    public void event(View rootView) {
        txtBack = rootView.findViewById(R.id.txtBack);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().popBackStack();

            }
        });
    }

}
