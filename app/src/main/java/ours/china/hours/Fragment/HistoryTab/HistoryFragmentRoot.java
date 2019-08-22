package ours.china.hours.Fragment.HistoryTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ours.china.hours.Common.Utils.FragmentmanagerUtils;
import ours.china.hours.R;

/**
 * Created by liujie on 1/24/18.
 */

public class HistoryFragmentRoot extends Fragment {
    public static final String ARG_OBJECT = "object";//testing

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_tab_root, container, false);
        Bundle args = getArguments();
        int position = args.getInt(ARG_OBJECT);

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentmanagerUtils.setFragmentManagerSearch(fragmentManager);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.history_root_frame, new HistoryFragment());

        transaction.commit();

        return rootView;
    }
}