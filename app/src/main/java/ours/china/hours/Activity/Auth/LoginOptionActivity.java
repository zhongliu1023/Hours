package ours.china.hours.Activity.Auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

public class LoginOptionActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoginOptionAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        addFragment();
        setTextSize();
    }


    private void initUI(){

        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager)findViewById(R.id.viewpager_id);
        adapter = new LoginOptionAdapter(getSupportFragmentManager());
    }


    private void addFragment(){
        adapter.addFragment(new PasswordLoginFragment(),getResources().getString(R.string.login_passwords));
        adapter.addFragment(new FaceLoginFragment(), getResources().getString(R.string.login_face));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setTextSize(){

        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(this);
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                tabTextView.setText(tab.getText());

                if (i == 0) {
                    tabTextView.setTextSize(14);
                }
            }

        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                ViewGroup viewGroup = (ViewGroup)tabLayout.getChildAt(0);
                ViewGroup vgTab = (ViewGroup)viewGroup.getChildAt(tab.getPosition());
                int count = vgTab.getChildCount();
                for (int i = 0; i < count; i++){
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof  TextView){
                        ((TextView)tabViewChild).setTextSize(16);
                        ((TextView)tabViewChild).setTextColor(getResources().getColor(R.color.black));
                    }
                }

            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {
                ViewGroup viewGroup = (ViewGroup)tabLayout.getChildAt(0);
                ViewGroup vgTab = (ViewGroup)viewGroup.getChildAt(tab.getPosition());
                int count = vgTab.getChildCount();
                for (int i = 0; i < count; i++){
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof  TextView){
                        ((TextView)tabViewChild).setTextSize(14);
                        ((TextView)tabViewChild).setTextColor(getResources().getColor(R.color.default_shadow_color));
                    }
                }
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//     https://stackoverflow.com/questions/44244469/how-to-change-selected-tab-title-textsize-in-android
    }
}
