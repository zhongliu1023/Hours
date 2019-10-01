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

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;

import java.util.ArrayList;

import lecho.lib.hellocharts.model.Line;
import ours.china.hours.Activity.AttentionActivity;
import ours.china.hours.Activity.Auth.LoginOptionActivity;
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
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Dialog.OutDlg;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.Profile;
import ours.china.hours.Model.User;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class PersonalFragment extends Fragment {

    public TextView tvFrPersonalFav,tvFrPersonalInfo,tvFrPersonalFeedback,tvFrPersonalProblem,tvFrPersonalStatement,
            tvFrPersonalAbout,tvFrPersonalMobile,tvFrPersonalPass,tvFrPersonalCheck,tvFrPersonalAccount;
    public LinearLayout linFrPersonalFav,linFrPersonalInfo,linFrPersonalFeedback,linFrPersonalProblem,linFrPersonalStatement,
            linFrPersonalAbout,linFrPersonalMobile,linFrPersonalPass,linFrPersonalCheck,linFrPersonalAccount;
    public Button btnFrPersonalOut;

    SharedPreferencesManager sessionManager;
    User currentUser;
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
        sessionManager = new SharedPreferencesManager(getContext());
        currentUser = UsersManagement.getCurrentUser(sessionManager);

        tvFrPersonalFav = (TextView)view.findViewById(R.id.tvfrPersonalFav);
        tvFrPersonalInfo = (TextView)view.findViewById(R.id.tvfrPersonalInfo);
        tvFrPersonalFeedback = (TextView)view.findViewById(R.id.tvfrPersonalFeedback);
        tvFrPersonalProblem = (TextView)view.findViewById(R.id.tvfrPersonalProblem);
        tvFrPersonalStatement = (TextView)view.findViewById(R.id.tvfrPersonalStatement);
        tvFrPersonalAbout = (TextView)view.findViewById(R.id.tvfrPersonalAbout);
        tvFrPersonalMobile = (TextView)view.findViewById(R.id.tvfrPersonalMobile);
        tvFrPersonalMobile.setText(currentUser.mobile);
        tvFrPersonalPass = (TextView)view.findViewById(R.id.tvfrPersonalPass);
        tvFrPersonalCheck = (TextView)view.findViewById(R.id.tvfrPersonalCheck);
        tvFrPersonalAccount = (TextView)view.findViewById(R.id.tvfrPersonalAccount);
        btnFrPersonalOut = (Button)view.findViewById(R.id.btnfrPersonalOut);

        linFrPersonalFav = (LinearLayout)view.findViewById(R.id.linFrPersonalFav);
        linFrPersonalInfo = (LinearLayout)view.findViewById(R.id.linFrPersonalInfo);
        linFrPersonalFeedback = (LinearLayout)view.findViewById(R.id.linFrPersonalFeedback);
        linFrPersonalProblem = (LinearLayout)view.findViewById(R.id.linFrPersonalProblem);
        linFrPersonalStatement = (LinearLayout)view.findViewById(R.id.linFrPersonalStatement);
        linFrPersonalAbout = (LinearLayout)view.findViewById(R.id.linFrPersonalAbout);
        linFrPersonalMobile = (LinearLayout)view.findViewById(R.id.linFrPersonalMobile);
        linFrPersonalPass = (LinearLayout)view.findViewById(R.id.linFrPersonalPass);
        linFrPersonalCheck = (LinearLayout)view.findViewById(R.id.linFrPersonalCheck);
        linFrPersonalAccount = (LinearLayout)view.findViewById(R.id.linFrPersonalAccount);
    }


    private void gotoActivity(){

        linFrPersonalFav.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentFav = new Intent(getContext(), AttentionActivity.class);
                startActivity(intentFav);
            }
        });

        linFrPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentInfo = new Intent(getContext(), UpdateinforActivity.class);
                startActivity(intentInfo);
            }
        });

        linFrPersonalFeedback.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentFeed = new Intent(getContext(), FeedbackActivity.class);
                startActivity(intentFeed);
            }
        });

        linFrPersonalProblem.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentProblem = new Intent(getContext(), ProblemActivity.class);
                startActivity(intentProblem);
            }
        });

        linFrPersonalStatement.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentStatement = new Intent(getContext(), StatementActivity.class);
                startActivity(intentStatement);
            }
        });

        linFrPersonalAbout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentAbout = new Intent(getContext(), AboutActivity.class);
                startActivity(intentAbout);
            }
        });

        linFrPersonalMobile.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentMobile = new Intent(getContext(), UpdatemobileActivity.class);
                startActivity(intentMobile);
            }
        });

        linFrPersonalPass.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intentPass = new Intent(getContext(), UpdatePasswordActivity.class);
                startActivity(intentPass);
            }
        });

        linFrPersonalCheck.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {


            }
        });

        btnFrPersonalOut.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                new AlertView.Builder().setContext(getContext()).setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.confirm_out))
                        .setDestructive(getString(R.string.cancel))
                        .setOthers(new String[]{getString(R.string.confirm)})
                        .setStyle(AlertView.Style.Alert).setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 1){
                            sessionManager.setPrefernceValueString(SharedPreferencesKeys.CURRENT_USER, "");
                            sessionManager.setPrefernceValueString(SharedPreferencesKeys.FLEX_STRINGS, "");
                            sessionManager.setPrefernceValueString(SharedPreferencesKeys.FOCUSE_BOOK, "");

                            Intent intent = new Intent(getContext(), LoginOptionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);
                        }
                    }
                }).build().show();
            }
        });
    }

}