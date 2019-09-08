package ours.china.hours.Fragment.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.LibraryBookAdapter;
import ours.china.hours.Adapter.MyShelfBookAdapter;
import ours.china.hours.Fragment.FragmentUtil;
import ours.china.hours.Model.LibraryBook;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;

public class SearchResultFragment extends Fragment {

    ArrayList<LibraryBook> mLibraryBooks;
    ArrayList<MyShelfBook> myShelfBooks;

    private RecyclerView libraryRecyclerView, myShelfRecyclerView;
    private LibraryBookAdapter libraryBookAdapter;
    private MyShelfBookAdapter myShelfBookAdapter;

    TextView txtMoreSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        init(view);
        event(view);

        return view;
    }

    public void init(View rootView) {
        mLibraryBooks = new ArrayList<>();
        myShelfBooks = new ArrayList<>();

        mLibraryBooks.add(new LibraryBook("hello", "百年孤独"));
        mLibraryBooks.add(new LibraryBook("hello", "百年孤独"));
        mLibraryBooks.add(new LibraryBook("hello", "百年孤独"));
        mLibraryBooks.add(new LibraryBook("hello", "百年孤独"));

        myShelfBooks.add(new MyShelfBook("hello", "downloaded", "百年孤独", "马尔克斯"));
        myShelfBooks.add(new MyShelfBook("hello", "no downloaded", "百年孤独", "马尔克斯"));
        myShelfBooks.add(new MyShelfBook("hello", "downloaded", "百年孤独", "马尔克斯"));
        myShelfBooks.add(new MyShelfBook("hello", "no downloaded", "百年孤独", "马尔克斯"));
        myShelfBooks.add(new MyShelfBook("hello", "downloaded", "百年孤独", "马尔克斯"));
        myShelfBooks.add(new MyShelfBook("hello", "no downloaded", "百年孤独", "马尔克斯"));


        libraryRecyclerView = rootView.findViewById(R.id.library_search_result);
        myShelfRecyclerView = rootView.findViewById(R.id.my_bookshelf_search_result);

        libraryBookAdapter = new LibraryBookAdapter(getActivity(), mLibraryBooks);
        myShelfBookAdapter = new MyShelfBookAdapter(getActivity(), myShelfBooks);

        RecyclerView.LayoutManager libraryManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        libraryRecyclerView.setLayoutManager(libraryManager);
        libraryRecyclerView.setAdapter(libraryBookAdapter);

        RecyclerView.LayoutManager myShelfManager = new LinearLayoutManager(getActivity());
        myShelfRecyclerView.setLayoutManager(myShelfManager);
        myShelfRecyclerView.setAdapter(myShelfBookAdapter);
    }

    public void event(View view) {
        txtMoreSearch = view.findViewById(R.id.txtMoreSearch);
        txtMoreSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = FragmentUtil.getFragmentByTagName(getActivity().getSupportFragmentManager(), "moreSearchFragment");
                if (fragment == null) {
                    fragment = new MoreSearchFragment();
                }

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, fragment, "moreSearchFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                FragmentUtil.printActivityFragmentList(getActivity().getSupportFragmentManager());
            }
        });
    }

}
