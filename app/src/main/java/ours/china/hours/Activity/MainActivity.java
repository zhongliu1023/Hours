package ours.china.hours.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ours.china.hours.BookLib.artifex.mupdf.fitz.StructuredText;
import ours.china.hours.BookLib.foobnix.android.utils.Dips;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.ext.CacheZipUtils;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.model.AppTemp;
import ours.china.hours.BookLib.foobnix.pdf.info.AppsConfig;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.tts.TTSNotification;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.foobnix.ui2.fragment.UIFragment;
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
import ours.china.hours.Fragment.PersonalTab.PersonalFragment;
import ours.china.hours.Fragment.PersonalTab.PersonalFragmentRoot;
import ours.china.hours.R;

public class MainActivity  extends AppCompatActivity {

    Fragment activityFragment;
    private BookFragment bookFragment;
    FragmentManager fragmentManager;
    private FrameLayout frameLayout;
    private HistoryFragment historyFragment;
    private HomeFragment homeFragment;
    private PersonalFragment personalFragment;


    public ImageView imgHomeTab, imgBookTab, imgHistoryTab, imgProfileTab;
    private LinearLayout linHome, linBook, linHistory, linProfile;
    private TextView txtHome, txtBook, txtHistory, txtProfile;

        private LinearLayout statusBarLayout;

        private SharedPreferencesManager sharedPreferencesManager;

        int selectedTabIndex = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

            init();
            setListener();

            // for the event of firstItem press in ProfileActivity
            Intent intent = getIntent();
            String tempMessage = intent.getStringExtra("from");

            if (tempMessage != null && tempMessage.equals("ProfileActivity")) {
                Toast.makeText(MainActivity.this, "From ProfileActivitpy", Toast.LENGTH_LONG).show();
            }


            AppDB.get().open(MainActivity.this, AppDB.DB_NAME);
            DocumentController.chooseFullScreen(this, AppState.get().fullScreenMainMode);
            TintUtil.updateAll();
            AppTemp.get().lastClosedActivity = MainActivity.class.getSimpleName();
            AppProfile.init(this);

            if (AppsConfig.MUPDF_VERSION == AppsConfig.MUPDF_1_12) {
                int initNative = StructuredText.initNative();
                LOG.d("initNative", initNative);
            }

            TTSNotification.initChannels(this);
            Dips.init(this);
            AppDB.get().open(this, AppProfile.getCurrent(this));

            CacheZipUtils.init(this);
            ExtUtils.init(this);
            IMG.init(this);
        }
        private void init(){
            imgHomeTab = (ImageView) findViewById(R.id.tab_home);
            imgBookTab = (ImageView) findViewById(R.id.tab_book);
            imgHistoryTab = (ImageView) findViewById(R.id.tab_history);
            imgProfileTab = (ImageView) findViewById(R.id.tab_profile);
            linHome = (LinearLayout) findViewById(R.id.linHome);
            linBook = (LinearLayout) findViewById(R.id.linBook);
            linHistory = (LinearLayout) findViewById(R.id.linHistory);
            linProfile = (LinearLayout) findViewById(R.id.linProfile);
            txtHome = (TextView) findViewById(R.id.txtHome);
            txtBook = (TextView) findViewById(R.id.txtBook);
            txtHistory = (TextView) findViewById(R.id.txtHistory);
            txtProfile = (TextView) findViewById(R.id.txtProfile);
            statusBarLayout = (LinearLayout) findViewById(R.id.statusBarLayout);

            homeFragment = new HomeFragment();
            bookFragment = new BookFragment();
            historyFragment = new HistoryFragment();
            personalFragment = new PersonalFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add( R.id.frame_content, homeFragment).commit();
            fragmentManager.beginTransaction().add( R.id.frame_content, bookFragment).hide(bookFragment).commit();
            fragmentManager.beginTransaction().add( R.id.frame_content, historyFragment).hide(historyFragment).commit();
            fragmentManager.beginTransaction().add( R.id.frame_content, personalFragment).hide(personalFragment).commit();
            activityFragment = homeFragment;
//            changedTabIcons(0);

        }

    @Override
    protected void onResume() {
        changedTabIcons(0);
        super.onResume();
    }

    private void setListener() {

            //Set su kien click
            linHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changedTabIcons(0);
                }
            });
            linBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changedTabIcons(1);
                }
            });
            linHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changedTabIcons(2);
                }
            });
            linProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changedTabIcons(3);
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
                    case 3:
                        if (f instanceof PersonalFragment) return true;
                        break;
                }
            }
            return false;
        }
        @Override
        public void onBackPressed() {

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

                case 1:
                    imgHomeTab.setImageDrawable(getResources().getDrawable(R.drawable.library2_icon));
                    imgBookTab.setImageDrawable(getResources().getDrawable(R.drawable.bookshelf_icon));
                    imgHistoryTab.setImageDrawable(getResources().getDrawable(R.drawable.state2_icon));
                    imgProfileTab.setImageDrawable(getResources().getDrawable(R.drawable.me2_icon));

                    txtHome.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtBook.setTextColor(getResources().getColor(R.color.pink));
                    txtHistory.setTextColor(getResources().getColor(R.color.alpa_90));
                    txtProfile.setTextColor(getResources().getColor(R.color.alpa_90));

                    fragmentManager.beginTransaction().hide(activityFragment).show(bookFragment).commit();
                    activityFragment = bookFragment;
                    bookFragment.getAllDataFromServer(0);

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

                    fragmentManager.beginTransaction().hide(activityFragment).show(historyFragment).commit();
                    activityFragment = historyFragment;
                    historyFragment.getDataFromLocalDB();
                    historyFragment.fetchBooksStatistics();

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


                    fragmentManager.beginTransaction().hide(activityFragment).show(personalFragment).commit();
                    activityFragment = personalFragment;

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

                    fragmentManager.beginTransaction().hide(activityFragment).show(homeFragment).commit();
                    activityFragment = homeFragment;

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
                case 3:
                    fragment = new PersonalFragment();
                    args.putInt(PersonalFragmentRoot.ARG_OBJECT, position + 1);
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
            return 4;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }

        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

    }

}