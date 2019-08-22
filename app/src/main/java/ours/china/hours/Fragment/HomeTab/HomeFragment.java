package ours.china.hours.Fragment.HomeTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);

        initController(rootView);
        setListener(rootView);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onReloadInfomation();
    }
    public void onReloadInfomation(){

        initUpdateValue();
    }
    private void initController(View rootView){

    }

    private void setListener(final View rootView){

    }
    private void initUpdateValue(){

    }

}