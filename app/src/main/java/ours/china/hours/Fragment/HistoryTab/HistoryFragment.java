package ours.china.hours.Fragment.HistoryTab;

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

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_tab, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}