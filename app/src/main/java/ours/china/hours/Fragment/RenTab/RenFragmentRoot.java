package ours.china.hours.Fragment.RenTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ours.china.hours.Common.Utils.FragmentmanagerUtils;
import ours.china.hours.Fragment.HomeTab.HomeFragment;
import ours.china.hours.R;

/**
 * Created by liujie on 1/24/18.
 */

public class RenFragmentRoot extends Fragment {
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
        View rootView = inflater.inflate(R.layout.fragment_ren_tab_root, container, false);
        Bundle args = getArguments();
        int position = args.getInt(ARG_OBJECT);

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentmanagerUtils.setFragmentManagerHome(fragmentManager);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.ren_root_frame, new RenFragment());

        transaction.commit();
        return rootView;
    }
}