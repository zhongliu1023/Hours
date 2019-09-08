package ours.china.hours.Fragment.PersonalTab;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import ours.china.hours.Activity.FavoritesActivity;
import ours.china.hours.Activity.NewsActivity;
import ours.china.hours.Activity.Personality.AboutActivity;
import ours.china.hours.Activity.Personality.FeedbackActivity;
import ours.china.hours.Activity.Personality.ProblemActivity;
import ours.china.hours.Activity.Personality.StatementActivity;
import ours.china.hours.Activity.Personality.UpdatePasswordActivity;
import ours.china.hours.Activity.Personality.UpdateinforActivity;
import ours.china.hours.Activity.Personality.UpdatemobileActivity;
import ours.china.hours.Activity.ProfileActivity;
import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.Adapter.ProfileAdapter;
import ours.china.hours.Dialog.OutDlg;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.Profile;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class PersonalFragment extends Fragment {

    public TextView tvFrPersonalFav,tvFrPersonalInfo,tvFrPersonalFeedback,tvFrPersonalProblem,tvFrPersonalStatement,
            tvFrPersonalAbout,tvFrPersonalMobile,tvFrPersonalPass,tvFrPersonalCheck,tvFrPersonalAccount;
    public Button btnFrPersonalOut;
    public OutDlg outDlg;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_tab, container, false);

        initUI(rootView);
        gotoActivity();

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void initUI(View view){

        tvFrPersonalFav = (TextView)view.findViewById(R.id.tvfrPersonalFav);
        tvFrPersonalInfo = (TextView)view.findViewById(R.id.tvfrPersonalInfo);
        tvFrPersonalFeedback = (TextView)view.findViewById(R.id.tvfrPersonalFeedback);
        tvFrPersonalProblem = (TextView)view.findViewById(R.id.tvfrPersonalProblem);
        tvFrPersonalStatement = (TextView)view.findViewById(R.id.tvfrPersonalStatement);
        tvFrPersonalAbout = (TextView)view.findViewById(R.id.tvfrPersonalAbout);
        tvFrPersonalMobile = (TextView)view.findViewById(R.id.tvfrPersonalMobile);
        tvFrPersonalPass = (TextView)view.findViewById(R.id.tvfrPersonalPass);
        tvFrPersonalCheck = (TextView)view.findViewById(R.id.tvfrPersonalCheck);
        tvFrPersonalAccount = (TextView)view.findViewById(R.id.tvfrPersonalAccount);
        btnFrPersonalOut = (Button)view.findViewById(R.id.btnfrPersonalOut);
    }


    private void gotoActivity(){

        tvFrPersonalFav.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentFav = new Intent(getContext(), FavoritesActivity.class);
                startActivity(intentFav);
            }
        });

        tvFrPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentInfo = new Intent(getContext(), UpdateinforActivity.class);
                startActivity(intentInfo);
            }
        });

        tvFrPersonalFeedback.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentFeed = new Intent(getContext(), FeedbackActivity.class);
                startActivity(intentFeed);
            }
        });

        tvFrPersonalProblem.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentProblem = new Intent(getContext(), ProblemActivity.class);
                startActivity(intentProblem);
            }
        });

        tvFrPersonalStatement.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentStatement = new Intent(getContext(), StatementActivity.class);
                startActivity(intentStatement);
            }
        });

        tvFrPersonalAbout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentAbout = new Intent(getContext(), AboutActivity.class);
                startActivity(intentAbout);
            }
        });

        tvFrPersonalMobile.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentMobile = new Intent(getContext(), UpdatemobileActivity.class);
                startActivity(intentMobile);
            }
        });

        tvFrPersonalPass.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentPass = new Intent(getContext(), UpdatePasswordActivity.class);
                startActivity(intentPass);
            }
        });

        tvFrPersonalCheck.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {


            }
        });

        btnFrPersonalOut.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
//                Toast.makeText(getActivity(), "dlg", Toast.LENGTH_SHORT).show();
                outDlg = new OutDlg(getActivity());
                outDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Window window = outDlg.getWindow();
                window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                outDlg.show();
            }
        });
    }

}