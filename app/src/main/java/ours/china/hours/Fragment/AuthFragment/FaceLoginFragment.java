package ours.china.hours.Fragment.AuthFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ours.china.hours.R;

public class FaceLoginFragment extends Fragment {

    public static FaceLoginFragment newInstance(){
        return  new FaceLoginFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_login, container, false);
        return view;
    }
}
