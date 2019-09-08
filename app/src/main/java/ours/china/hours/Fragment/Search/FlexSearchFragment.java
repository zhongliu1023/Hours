package ours.china.hours.Fragment.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.FlexboxAdapter;
import ours.china.hours.R;

public class FlexSearchFragment extends Fragment {

    public String[] flexStrings = {"迷人的材料", "乌合之众", "把时间浪费在美好的事物上", "自然哲学的数学原理", "长安十二时辰", "上帝与黄金"};
    public RecyclerView flexRecyclerView;
    public FlexboxAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flexsearch, container, false);
        init(view);

        return view;
    }

    public void init(View rootView) {
        flexRecyclerView = rootView.findViewById(R.id.flexbox_recyclerview);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        flexRecyclerView.setLayoutManager(flexboxLayoutManager);
        adapter = new FlexboxAdapter(flexStrings, getActivity());
        flexRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
