package ours.china.hours.Fragment.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Activity.Global;
import ours.china.hours.Adapter.LibraryBookAdapter;
import ours.china.hours.Adapter.MyShelfBookAdapter;
import ours.china.hours.Fragment.FragmentUtil;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;

public class SearchResultFragment extends Fragment {

    ArrayList<Book> mLibraryBooks;
    ArrayList<Book> myShelfBooks;

    private RecyclerView libraryRecyclerView, myShelfRecyclerView;
    private LibraryBookAdapter libraryBookAdapter;
    private MyShelfBookAdapter myShelfBookAdapter;

    TextView txtMoreSearch;
    String keyWords = "";

    public SearchResultFragment(String keywords){
        keyWords = keywords;
    }
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

        getAllLibraryBooksFromServer();
        getAllShelfBooksFromServer();

        return view;
    }

    public void init(View rootView) {
        mLibraryBooks = new ArrayList<>();
        myShelfBooks = new ArrayList<>();

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
                    fragment = new MoreSearchFragment(keyWords);
                }

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, fragment, "moreSearchFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                FragmentUtil.printActivityFragmentList(getActivity().getSupportFragmentManager());
            }
        });
    }
    private void getAllLibraryBooksFromServer() {
        mLibraryBooks = new ArrayList<>();

        Global.showLoading(getContext(),"generate_report");
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("keyword", Collections.singletonList(keyWords));

        Ion.with(getActivity())
                .load(Url.searchAllBookwithMobile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameters(params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        Log.i("HomeFragment", "result => " + result);
                        Global.hideLoading();

                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").toLowerCase().equals("success")) {

                                    JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<Book>>() {}.getType();
                                    mLibraryBooks = gson.fromJson(dataArray.toString(), type);
                                    libraryBookAdapter.reloadBookList(mLibraryBooks);
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
    private void getAllShelfBooksFromServer() {
        myShelfBooks = new ArrayList<>();

        Ion.with(getActivity())
                .load(Url.searchMyBookwithMobile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("statusOnly", "0")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        Log.i("HomeFragment", "result => " + result);
                        Global.hideLoading();

                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").toLowerCase().equals("success")) {

                                    JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<Book>>() {}.getType();
                                    myShelfBooks = gson.fromJson(dataArray.toString(), type);
                                    myShelfBookAdapter.reloadBookList(myShelfBooks, keyWords);

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
}
