package ours.china.hours.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.Objects;

import ours.china.hours.Common.ActivityResults.ActivityResultBus;
import ours.china.hours.Common.ActivityResults.ActivityResultEvent;
import ours.china.hours.Common.FragmentsBus.FragmentsBus;
import ours.china.hours.Common.FragmentsBus.FragmentsEvents;
import ours.china.hours.Common.FragmentsBus.FragmentsEventsKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Common.Utils.ActivityCodes;
import ours.china.hours.Common.Utils.FragmentmanagerUtils;
import ours.china.hours.CustomView.NonSwipeableViewPager;
import ours.china.hours.Fragment.BookTab.BookFragment;
import ours.china.hours.Fragment.BookTab.BookFragmentRoot;
import ours.china.hours.Fragment.HistoryTab.HistoryFragment;
import ours.china.hours.Fragment.HistoryTab.HistoryFragmentRoot;
import ours.china.hours.Fragment.HomeTab.HomeFragment;
import ours.china.hours.Fragment.HomeTab.HomeFragmentRoot;
import ours.china.hours.R;

public class MainActivity  extends FragmentActivity {
    public ImageView imgHomeTab, imgBookTab, imgHistoryTab, imgProfileTab;
    private LinearLayout linHome, linBook, linHistory, linProfile;
    private TextView txtHome, txtBook, txtHistory, txtProfile;
        private SectionsPagerAdapter mSectionsPagerAdapter;
        private NonSwipeableViewPager mViewPager;

        private LinearLayout statusBarLayout;

        private SharedPreferencesManager sharedPreferencesManager;

        int selectedTabIndex = 0;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

            init();
            setListener();

            // for the event of firstItem press in ProfileActivity
            Intent intent = getIntent();
            String tempMessage = intent.getStringExtra("from");

            if (tempMessage != null && tempMessage.equals("ProfileActivity")) {
                Toast.makeText(MainActivity.this, "From ProfileActivitpy", Toast.LENGTH_LONG).show();
            }

        }
        private void init(){
            imgHomeTab = (ImageView) findViewById(R.id.tab_home);
            imgBookTab = (ImageView) findViewById(R.id.tab_book);
            imgHistoryTab = (ImageView) findViewById(R.id.tab_history);
            imgProfileTab = (ImageView) findViewById(R.id.tab_profile);

            linHome = findViewById(R.id.linHome);
            linBook = findViewById(R.id.linBook);
            linHistory = findViewById(R.id.linHistory);
            linProfile = findViewById(R.id.linProfile);

            txtHome = findViewById(R.id.txtHome);
            txtBook =findViewById(R.id.txtBook);
            txtHistory = findViewById(R.id.txtHistory);
            txtProfile = findViewById(R.id.txtProfile);

            statusBarLayout = (LinearLayout) findViewById(R.id.statusBarLayout);

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            // Set up the ViewPager with the sections adapter.
            mViewPager = (NonSwipeableViewPager) findViewById(R.id.frame_content);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setOffscreenPageLimit(3);


            mViewPager.setCurrentItem(0);
            changedTabIcons(0);
        }

        private void setListener() {

            //Set su kien click
            linHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(0);
                    changedTabIcons(0);
                }
            });
            linBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(1);
                    changedTabIcons(1);
                }
            });
            linHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(2);
                    changedTabIcons(2);
                }
            });
            linProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
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
                    changedTabIcons(position);
                }

                // Called when the scroll state changes:
                // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
                @Override
                public void onPageScrollStateChanged(int state) {
                    // Code goes here
                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case ActivityCodes.CROP_IMAGE:
                    case ActivityCodes.CROP_GALLERY:
                    case ActivityCodes.REQUEST_IMAGE_CAPTURE:
                    case ActivityCodes.REQUEST_IMAGE_GALLERY:
                    case ActivityCodes.REQUEST_IMAGE_CROP:
                        ActivityResultBus.getInstance().postQueue(
                                new ActivityResultEvent(requestCode, resultCode, data));
                        break;
                }
            }
        }
        private boolean isFirstPage(List fragmentList){
            for(Object f : fragmentList) {
                switch (selectedTabIndex){
                    case 0:
                        if(f instanceof HomeFragment) return true;
                        break;
                    case 1:
                        if(f instanceof BookFragment) return true;
                        break;
                    case 2:
                        if(f instanceof HistoryFragment) return true;
                        break;
                }
            }
            return false;
        }
        @Override
        public void onBackPressed() {
            switch (selectedTabIndex){
                case 0:
                    if (isFirstPage(FragmentmanagerUtils.getFragmentManagerHome().getFragments())){
                        super.onBackPressed();
                        break;
                    }
                case 1:
                    if (isFirstPage(FragmentmanagerUtils.getFragmentManagerIdentify().getFragments())){
                        mViewPager.setCurrentItem(0);
                        break;
                    }
                case 2:
                    if (isFirstPage(FragmentmanagerUtils.getFragmentManagerSearch().getFragments())){
                        mViewPager.setCurrentItem(0);
                        break;
                    }
                case 3:
                    if (isFirstPage(FragmentmanagerUtils.getFragmentManagerReports().getFragments())){
                        mViewPager.setCurrentItem(0);
                        break;
                    }
                default:
                    FragmentsBus.getInstance().postQueue(
                            new FragmentsEvents(FragmentsEventsKeys.BACKPRESSED));
                    break;
            }
        }
        private Drawable changeImageColor(int color, Drawable mDrawable){
            Drawable changedDrawable = mDrawable;
            changedDrawable.setColorFilter(new
                    PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            return changedDrawable;
        }
        private boolean isHomePage(){
            if (FragmentmanagerUtils.getFragmentManagerHome() != null){
                List fragmentList = FragmentmanagerUtils.getFragmentManagerHome().getFragments();
                for(Object f : fragmentList) {
                    if(f instanceof HomeFragment) {
                        return true;
                    }
                }
            }
            return false;
        }
        public void changeDarkTheme(){
            statusBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        public void changeLightTheme(){
            statusBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        private void changedTabIcons(int position){
            if (position == 0 && isHomePage()) {
//                changeDarkTheme();
            } else {
//                changeLightTheme();
            }
            selectedTabIndex = position;
            switch (position) {
                case 0:
                    imgHomeTab.setImageDrawable(getResources().getDrawable(R.drawable.library_icon));
                    imgBookTab.setImageDrawable(getResources().getDrawable(R.drawable.bookshelf2_icon));
                    imgHistoryTab.setImageDrawable(getResources().getDrawable(R.drawable.state2_icon));
                    imgProfileTab.setImageDrawable(getResources().getDrawable(R.drawable.me2_icon));

                    txtHome.setTextColor(getResources().getColor(R.color.pink));
                    txtBook.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtHistory.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtProfile.setTextColor(getResources().getColor(R.color.alpa_90));
                    break;
                case 1:
                    imgHomeTab.setImageDrawable(getResources().getDrawable(R.drawable.library2_icon));
                    imgBookTab.setImageDrawable(getResources().getDrawable(R.drawable.bookshelf_icon));
                    imgHistoryTab.setImageDrawable(getResources().getDrawable(R.drawable.state2_icon));
                    imgProfileTab.setImageDrawable(getResources().getDrawable(R.drawable.me2_icon));

                    txtHome.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtBook.setTextColor(getResources().getColor(R.color.pink));
                    txtHistory.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtProfile.setTextColor(getResources().getColor(R.color.alpa_90));
                    break;
                case 2:
                    imgHomeTab.setImageDrawable(getResources().getDrawable(R.drawable.library2_icon));
                    imgBookTab.setImageDrawable(getResources().getDrawable(R.drawable.bookshelf2_icon));
                    imgHistoryTab.setImageDrawable(getResources().getDrawable(R.drawable.state_icon));
                    imgProfileTab.setImageDrawable(getResources().getDrawable(R.drawable.me2_icon));

                    txtHome.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtBook.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtHistory.setTextColor(getResources().getColor(R.color.pink));
                    txtProfile.setTextColor(getResources().getColor(R.color.alpa_90));
                    break;
                case 3:
                    imgHomeTab.setImageDrawable(getResources().getDrawable(R.drawable.library2_icon));
                    imgBookTab.setImageDrawable(getResources().getDrawable(R.drawable.bookshelf2_icon));
                    imgHistoryTab.setImageDrawable(getResources().getDrawable(R.drawable.state2_icon));
                    imgProfileTab.setImageDrawable(getResources().getDrawable(R.drawable.me_icon));

                    txtHome.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtBook.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtHistory.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtProfile.setTextColor(getResources().getColor(R.color.pink));
                    break;

                default:
                    imgHomeTab.setImageDrawable(getResources().getDrawable(R.drawable.library_icon));
                    imgBookTab.setImageDrawable(getResources().getDrawable(R.drawable.bookshelf2_icon));
                    imgHistoryTab.setImageDrawable(getResources().getDrawable(R.drawable.state2_icon));
                    imgProfileTab.setImageDrawable(getResources().getDrawable(R.drawable.me2_icon));

                    txtHome.setTextColor(getResources().getColor(R.color.pink));
                    txtBook.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtHistory.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtProfile.setTextColor(getResources().getColor(R.color.alpa_90));
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
                    fragment = new HomeFragmentRoot();
                    args.putInt(HomeFragmentRoot.ARG_OBJECT, position + 1);
                    break;
                case 1:
                    fragment = new BookFragmentRoot();
                    args.putInt(BookFragmentRoot.ARG_OBJECT, position + 1);
                    break;
                case 2:
                    fragment = new HistoryFragmentRoot();
                    args.putInt(HistoryFragmentRoot.ARG_OBJECT, position + 1);
                    break;

                default:
                    fragment = new HomeFragment();
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}