package ours.china.hours.Activity.Auth;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;

import ours.china.hours.Adapter.LoginOptionAdapter;
import ours.china.hours.Fragment.AuthFragment.FaceLoginFragment;
import ours.china.hours.Fragment.AuthFragment.PasswordLoginFragment;
import ours.china.hours.R;

public class LoginOptionActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoginOptionAdapter adapter;

    private FaceLoginFragment faceLoginFragment;
    private boolean isAlreadyStarted = false;

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

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.login_passwords)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.login_face)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    }


    private void addFragment(){
        faceLoginFragment = new FaceLoginFragment();
        adapter.addFragment(new PasswordLoginFragment(),getResources().getString(R.string.login_passwords));
        adapter.addFragment(faceLoginFragment, getResources().getString(R.string.login_face));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }


    private void setTextSize(){

//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            if (tab != null) {
//
//                TextView tabTextView = new TextView(this);
//                tab.setCustomView(tabTextView);
//
//                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//
//                tabTextView.setText(tab.getText());
//
//                if (i == 0) {
//                    tabTextView.setTextSize(14);
//                }
//            }
//        }
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1 && !isAlreadyStarted){
                    isAlreadyStarted = true;
                    faceLoginFragment.startScanFace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
//            @Override public void onTabSelected(TabLayout.Tab tab) {
//                ViewGroup viewGroup = (ViewGroup)tabLayout.getChildAt(0);
//                ViewGroup vgTab = (ViewGroup)viewGroup.getChildAt(tab.getPosition());
//                int count = vgTab.getChildCount();
//                for (int i = 0; i < count; i++){
//                    View tabViewChild = vgTab.getChildAt(i);
//                    if (tabViewChild instanceof  TextView){
//                        ((TextView)tabViewChild).setTextSize(32);
//                        ((TextView)tabViewChild).setTextColor(getResources().getColor(R.color.black));
//                    }
//                }
//
//            }
//
//            @Override public void onTabUnselected(TabLayout.Tab tab) {
//                ViewGroup viewGroup = (ViewGroup)tabLayout.getChildAt(0);
//                ViewGroup vgTab = (ViewGroup)viewGroup.getChildAt(tab.getPosition());
//                int count = vgTab.getChildCount();
//                for (int i = 0; i < count; i++){
//                    View tabViewChild = vgTab.getChildAt(i);
//                    if (tabViewChild instanceof  TextView){
//                        ((TextView)tabViewChild).setTextSize(32);
//                        ((TextView)tabViewChild).setTextColor(getResources().getColor(R.color.default_shadow_color));
//                    }
//                }
//            }
//
//            @Override public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//     https://stackoverflow.com/questions/44244469/how-to-change-selected-tab-title-textsize-in-android
    }
}
