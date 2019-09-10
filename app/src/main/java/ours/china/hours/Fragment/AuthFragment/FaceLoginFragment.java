package ours.china.hours.Fragment.AuthFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ours.china.hours.R;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Auth.FaceLoginActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Management.Url;
import ours.china.hours.R;

public class FaceLoginFragment extends Fragment {

    TextView txtGo;
    ImageView faceImage;
    ProgressBar progressBar;

    private static int requestNumber = 1;

    public static FaceLoginFragment newInstance(){
        return  new FaceLoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_login, container, false);

        init(view);
        event(view);
        return view;
    }

    public void init(View rootView) {
        faceImage = rootView.findViewById(R.id.faceImage);
        progressBar = rootView.findViewById(R.id.progressbar);

        progressBar.setVisibility(View.INVISIBLE);
    }

    public void event(View view) {
        txtGo = view.findViewById(R.id.txtGo);
        txtGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FaceLoginActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    public void displayImage() {
        Glide.with(getActivity())
                .load(Global.faceImageLocalUrl)
                .placeholder(R.drawable.face_logo)
                .into(faceImage);
    }

    public void getDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(getActivity())
                .load(Url.faceLogin)
                .setTimeout(10000)
//                .setBodyParameter("name", name)
//                .setBodyParameter("studId", studId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {
                                    displayImage();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }, 1000);

                                } else {
                                    Toast.makeText(getActivity(), "Extra error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), "Api error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();

        if (requestCode == requestNumber) {
            if (!Global.faceImageLocalUrl.equals("")) {
                txtGo.setVisibility(View.INVISIBLE);
//            getDataFromServer();
                displayImage();
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }
}
