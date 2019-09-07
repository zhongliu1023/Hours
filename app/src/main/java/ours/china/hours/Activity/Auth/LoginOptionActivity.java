package ours.china.hours.Activity.Auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.google.android.material.tabs.TabLayout;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Activity.ProfileActivity;
import ours.china.hours.Adapter.LoginOptionAdapter;
import ours.china.hours.CustomView.NonSwipeableViewPager;
import ours.china.hours.FaceDetect.common.Constants;
import ours.china.hours.FaceDetect.util.ConfigUtil;
import ours.china.hours.Fragment.AuthFragment.FaceLoginFragment;
import ours.china.hours.Fragment.AuthFragment.PasswordLoginFragment;
import ours.china.hours.Fragment.BookTab.BookFragmentRoot;
import ours.china.hours.Fragment.HistoryTab.HistoryFragmentRoot;
import ours.china.hours.Fragment.HomeTab.HomeFragment;
import ours.china.hours.Fragment.HomeTab.HomeFragmentRoot;
import ours.china.hours.R;

public class LoginOptionActivity extends FragmentActivity {

    private LoginOptionActivity.SectionsPagerAdapter mSectionsPagerAdapter;
    private NonSwipeableViewPager mViewPager;
    private TextView login_pass, login_face, login_pass_bottom, login_face_bottom;
    private RelativeLayout relLoginPass, relLoginFace;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();

        setListener();
    }


    private void initUI(){

        login_pass = (TextView)findViewById(R.id.login_pass);
        login_pass_bottom = (TextView)findViewById(R.id.login_pass_bottom);
        login_face = (TextView)findViewById(R.id.login_face);
        login_face_bottom = (TextView)findViewById(R.id.login_face_bottom);
        relLoginFace = (RelativeLayout)findViewById(R.id.relLoginFace);
        relLoginPass = (RelativeLayout)findViewById(R.id.relLoginPass);

        mSectionsPagerAdapter = new LoginOptionActivity.SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.frame_content);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setCurrentItem(0);

        changedTab(0);
    }


    private void setListener() {

        relLoginPass.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mViewPager.setCurrentItem(0);
                changedTab(0);
            }
        });

        relLoginFace.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mViewPager.setCurrentItem(1);
                changedTab(1);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changedTab(position);
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }


    private void changedTab(int position){

        switch (position) {
            case 0:
                login_pass.setTextColor(getResources().getColor(android.R.color.black));
                login_pass.setTextSize(getResources().getDimension(R.dimen.font_size_10));
                login_face.setTextColor(getResources().getColor(R.color.textcolor_light));
                login_face.setTextSize(getResources().getDimension(R.dimen.font_size_8));
                login_pass_bottom.setVisibility(View.VISIBLE);
                login_face_bottom.setVisibility(View.INVISIBLE);
                break;

            case 1:
                login_face.setTextColor(getResources().getColor(android.R.color.black));
                login_face.setTextSize(getResources().getDimension(R.dimen.font_size_10));
                login_pass.setTextColor(getResources().getColor(R.color.textcolor_light));
                login_pass.setTextSize(getResources().getDimension(R.dimen.font_size_8));
                login_pass_bottom.setVisibility(View.INVISIBLE);
                login_face_bottom.setVisibility(View.VISIBLE);
                break;

            default:
                login_pass.setTextColor(getResources().getColor(android.R.color.black));
                login_pass.setTextSize(getResources().getDimension(R.dimen.font_size_10));
                login_pass.setTextColor(getResources().getColor(R.color.textcolor_light));
                login_pass.setTextSize(getResources().getDimension(R.dimen.font_size_8));
                login_pass_bottom.setVisibility(View.VISIBLE);
                login_face_bottom.setVisibility(View.INVISIBLE);
                break;
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    fragment = new PasswordLoginFragment();
                    break;
                case 1:
                    fragment = new FaceLoginFragment();
                    break;

                default:
                    fragment = new PasswordLoginFragment();
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

}
