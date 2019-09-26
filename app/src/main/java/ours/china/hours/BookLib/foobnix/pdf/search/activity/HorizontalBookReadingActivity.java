package ours.china.hours.BookLib.foobnix.pdf.search.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.bumptech.glide.request.target.ThumbnailImageViewTarget;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.ebookdroid.core.codec.CodecPage;
import org.ebookdroid.droids.mupdf.codec.TextWord;
import org.ebookdroid.droids.mupdf.codec.exceptions.MuPdfPasswordException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.xm.weidongjian.popuphelper.PopupWindowHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ours.china.hours.Activity.Auth.ForgotPassActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.NoteActivity;
import ours.china.hours.Adapter.PageBookmarkAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.BaseItemLayoutAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.Dips;
import ours.china.hours.BookLib.foobnix.android.utils.IntegerResponse;
import ours.china.hours.BookLib.foobnix.android.utils.Keyboards;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.Objects;
import ours.china.hours.BookLib.foobnix.android.utils.ResultResponse;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.android.utils.Views;
import ours.china.hours.BookLib.foobnix.ext.CacheZipUtils;
import ours.china.hours.BookLib.foobnix.model.AppBook;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.model.AppTemp;
import ours.china.hours.BookLib.foobnix.pdf.CopyAsyncTask;
import ours.china.hours.BookLib.foobnix.pdf.info.Android6;
import ours.china.hours.BookLib.foobnix.pdf.info.BookmarksData;
import ours.china.hours.BookLib.foobnix.pdf.info.DictsHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.OutlineHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.PageUrl;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.pdf.info.UiSystemUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.Urls;
import ours.china.hours.BookLib.foobnix.pdf.info.model.BookCSS;
import ours.china.hours.BookLib.foobnix.pdf.info.model.OutlineLinkWrapper;
import ours.china.hours.BookLib.foobnix.pdf.info.presentation.BookmarksAdapter;
import ours.china.hours.BookLib.foobnix.pdf.info.presentation.OutlineAdapter;
import ours.china.hours.BookLib.foobnix.pdf.info.view.AlertDialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.AnchorHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.BookmarkPanel;
import ours.china.hours.BookLib.foobnix.pdf.info.view.BrightnessHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.CustomSeek;
import ours.china.hours.BookLib.foobnix.pdf.info.view.Dialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DialogsPlaylist;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DragingDialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DragingPopup;
import ours.china.hours.BookLib.foobnix.pdf.info.view.EditTextHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.HorizontallSeekTouchEventListener;
import ours.china.hours.BookLib.foobnix.pdf.info.view.HypenPanelHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.MyPopupMenu;
import ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.DraggbleTouchListener;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.FileInformationDialog;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.RecentUpates;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.MagicHelper;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.FlippingStart;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.FlippingStop;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.InvalidateMessage;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessageAutoFit;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessageEvent;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessagePageXY;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessegeBrightness;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MsgChangePaintWordsColor;
import ours.china.hours.BookLib.foobnix.pdf.search.view.CloseAppDialog;
import ours.china.hours.BookLib.foobnix.pdf.search.view.VerticalViewPager;
import ours.china.hours.BookLib.foobnix.sys.ClickUtils;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.tts.MessagePageNumber;
import ours.china.hours.BookLib.foobnix.tts.TTSControlsView;
import ours.china.hours.BookLib.foobnix.tts.TTSEngine;
import ours.china.hours.BookLib.foobnix.tts.TTSNotification;
import ours.china.hours.BookLib.foobnix.tts.TTSService;
import ours.china.hours.BookLib.foobnix.tts.TtsStatus;
import ours.china.hours.BookLib.foobnix.ui2.MainTabs2;
import ours.china.hours.BookLib.foobnix.ui2.MyContextWrapper;
import ours.china.hours.BookLib.nostra13.universalimageloader.core.ImageLoader;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.BookLib.nostra13.universalimageloader.utils.L;
import ours.china.hours.Common.ColorCollection;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Dialog.NoteDialog;
import ours.china.hours.Dialog.SearchContentDialog;
import ours.china.hours.FaceDetect.faceserver.CompareResult;
import ours.china.hours.FaceDetect.faceserver.FaceServer;
import ours.china.hours.FaceDetect.model.DrawInfo;
import ours.china.hours.FaceDetect.model.FacePreviewInfo;
import ours.china.hours.FaceDetect.util.ConfigUtil;
import ours.china.hours.FaceDetect.util.DrawHelper;
import ours.china.hours.FaceDetect.util.camera.CameraHelper;
import ours.china.hours.FaceDetect.util.camera.CameraListener;
import ours.china.hours.FaceDetect.util.face.FaceHelper;
import ours.china.hours.FaceDetect.util.face.FaceListener;
import ours.china.hours.FaceDetect.util.face.RequestFeatureStatus;
import ours.china.hours.FaceDetect.widget.FaceRectView;
import ours.china.hours.FaceDetect.widget.ShowFaceInfoAdapter;
import ours.china.hours.Fragment.HomeTab.HomeFragmentRoot;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.BookStatus;
import ours.china.hours.R;
import ours.china.hours.Utility.ConnectivityHelper;

public class HorizontalBookReadingActivity extends AppCompatActivity implements
        ViewTreeObserver.OnGlobalLayoutListener,
        PageBookmarkAdapter.OnClickBookItemLintener,
        SearchContentDialog.OnSelectContentListener,
        NoteDialog.OnAddNoteListener {

    // ------------- for document browser ----------------
    VerticalViewPager viewPager;
    View parentParent, overlay;
    LinearLayout actionBar, bottomBar;
    ImageView pagesBookmark, imgBookMark;
    TextView pagesCountIndicator, pagesReadingPercent, toolbar_title;
    SeekBar seekBar;
    FrameLayout anchor;
    ImageView anchorX, anchorY;

    Dialog rotatoinDialog;
    HorizontalModeController dc;
    CopyAsyncTask loadinAsyncTask;

    Handler handler = new Handler(Looper.getMainLooper());
    Handler flippingHandler = new Handler(Looper.getMainLooper());
    Handler handlerTimer = new Handler(Looper.getMainLooper());

    volatile Boolean isInitPosistion = null;
    volatile int isInitOrientation;

    String quickBookmark;
    TTSControlsView ttsActive;

    // my definition part. from here.
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ImageView catalogMenu, imgFont, imgBrightness, imgNote, imgShare;
    RelativeLayout relCatalogMenu, relFont, relBrightness, relNote, relShare;
    LinearLayout mainBottomBar, fontBottomBar, brightnessBottomBar;

    public static String fontImageClicked = "no";
    public static String brightnessImageClicked = "no";

    // for search
    ImageView imgSearch;

    // for brightness
    CustomSeek seekBarBrightness;
    CheckBox brightnessAutoSetting;
    int brightnessValue;

    // for font
    ImageView imgFontSizePlus, imgFontSizeMinus;
    TextView txtFontSize;
    int fontSizeValue;
    RelativeLayout relFontType;
    TextView txtFontType;
    static int max_fontSize = 70;
    static int min_fontSize = 10;

    // for hide camera
    TextView docBackground;
    RelativeLayout topLayout, bottomLayout;

    // for margin
    ImageView imgSmallMargin, imgMiddleMargin, imgBigMargin;
//    int imgMarginValue = 0;

    // for lineHeight
    ImageView imgBigLineHeight, imgMiddleLineHeight, imgSmallLineHeight;
//    int imgLineHeightValue = 0;

    // for doc background
    ImageView imgWhiteBrightness, imgBrownBrightness, imgGreenBrightness, imgBlackBrightness;

    // for drawerlayout.
    LinearLayout linCatalogBack, linCatalogCatalog, linCatalogBookmark, linCatalogRefresh;
    ImageView imgCatalogBack, imgCatalogCatalog, imgCatalogBookmark, imgCatalogRefresh;
    ListView contentList;
    RecyclerView recyclerContentList;
    PageBookmarkAdapter pageBookmarkAdapter;

    // for popupWindow
    RelativeLayout popupBack, popupYellow, popupOrange, popupBlue, popupPink, popupErase;
    RelativeLayout popupCopy, popupNote, popupSearch, popupTranslate, popupShare, popupColorPick;
    RelativeLayout popupSearchBook, popupSearchNet, popupSearchEncyclopedia;

    LinearLayout popupDefaultShow, popupSearchPart, popupColorPart;
    ImageView popupColorPickImage;
    RelativeLayout copyView;
//    int popupColorPickValue, tempColorPickValue;

    ColorCollection colorPickValue, tempColorPickValue;

    ClickUtils clickUtils;
    int flippingTimer = 0;

    // --------------------------- end ----------------------

    long faceDetectStartTime = 0;
    long faceDetectRemovedTime;
    long readTime = 0;
    long pageChangedTime;

    String maxPage;
    String currentPage;

    DBController db;

    TextView topReadTime, pageReadingPercent;
    ImageView imgBack;

    long displayedTime, tempDisplayedTime;

    public static boolean stateChangFlag = false;
    public long countTime;
    // --------------------------- for face detect --------------------------

    private static final String TAG = "RegisterAndRecognize";
    private static final int MAX_DETECT_NUM = 10;

    private static final int WAIT_LIVENESS_INTERVAL = 50;
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;

    private Integer rgbCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private FaceEngine faceEngine;
    private FaceHelper faceHelper;
    private List<CompareResult> compareResultList;
    private boolean livenessDetect = true;

    private int afCode = -1;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();

    private View previewView;
    private FaceRectView faceRectView;


    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final float SIMILAR_THRESHOLD = 0.8F;

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_NETWORK_STATE

    };

    //-----------------------------------------------


    private Book focusBook = null;
    SharedPreferencesManager sessionManager;

    PopupWindowHelper popupWindowHelper;
    SearchContentDialog searchContentDialog;
    NoteDialog noteDialog;
    int selectedNoteType = 0;
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (TTSNotification.ACTION_TTS.equals(intent.getAction())) {
            return;
        }

        if (!intent.filterEquals(getIntent())) {
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        quickBookmark = getString(R.string.fast_bookmark);
        flippingTimer = 0;

        long crateBegin = System.currentTimeMillis();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        BrightnessHelper.applyBrigtness(this);

        if (AppState.get().isDayNotInvert) {
            setTheme(R.style.StyledIndicatorsWhite);
        } else {
            setTheme(R.style.StyledIndicatorsBlack);
        }

        DocumentController.doRotation(this);
        clickUtils = new ClickUtils();

        super.onCreate(savedInstanceState);

        boolean isTextFomat = ExtUtils.isTextFomat(getIntent());

        // AppTemp.get().isCut = false;
        PageImageState.get().isShowCuttingLine = false;
        PageImageState.get().cleanSelectedWords();

        setContentView(R.layout.activity_horizontal_bookreading);

        if (!Android6.canWrite(this)) {
            Android6.checkPermissions(this, true);
            return;
        }

        // in case there are no data in database, insert primary data into database
        db = new DBController(HorizontalBookReadingActivity.this);
        sessionManager = new SharedPreferencesManager(this);
        focusBook = BookManagement.getFocuseBook(sessionManager);
        if (focusBook.bookStatus == null || db.getBookStateData(focusBook.bookId) == null){
            db.insertBookStateData(new BookStatus(), focusBook.bookId);
        }
        focusBook.bookStatus = db.getBookStateData(focusBook.bookId);

        // for read time
        topReadTime = findViewById(R.id.topReadTime);
        pageReadingPercent = findViewById(R.id.pagesReadingPercent);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    HorizontalBookReadingActivity.this.finish();
                } else {
                    HorizontalBookReadingActivity.super.onBackPressed();
                }
            }
        });

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        viewPager = (VerticalViewPager) findViewById(R.id.pager);
        parentParent = findViewById(R.id.parentParent);

        overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);

        actionBar = findViewById(R.id.actionBar);
        bottomBar = findViewById(R.id.bottomBar);

        topLayout = findViewById(R.id.topLayout);
        bottomLayout = findViewById(R.id.bottomLayout);
        docBackground = findViewById(R.id.docBackground);
        pagesReadingPercent = findViewById(R.id.pagesReadingPercent);
        toolbar_title = findViewById(R.id.toolbar_title);
        pagesCountIndicator = findViewById(R.id.pagesCountIndicator);
        topReadTime = findViewById(R.id.topReadTime);

        anchor = findViewById(R.id.anchor);
        anchorX = findViewById(R.id.anchorX);
        anchorY = findViewById(R.id.anchorY);


        TintUtil.setTintImageWithAlpha(anchorX, AppState.get().isDayNotInvert ? Color.BLUE : Color.YELLOW, 150);
        TintUtil.setTintImageWithAlpha(anchorY, AppState.get().isDayNotInvert ? Color.BLUE : Color.YELLOW, 150);

        anchorX.setVisibility(View.GONE);
        anchorY.setVisibility(View.GONE);

        DraggbleTouchListener touch1 = new DraggbleTouchListener(anchorX, (View) anchorX.getParent());
        DraggbleTouchListener touch2 = new DraggbleTouchListener(anchorY, (View) anchorY.getParent());

        final Runnable onMoveActionOnce = new Runnable() {

            @Override
            public void run() {
                float x = anchorX.getX() + anchorX.getWidth();
                float y = anchorX.getY() + anchorX.getHeight() / 2;

                float x1 = anchorY.getX();
                float y1 = anchorY.getY();
                EventBus.getDefault().post(new MessagePageXY(MessagePageXY.TYPE_SELECT_TEXT, viewPager.getCurrentItem(), x, y, x1, y1));
            }
        };

        final Runnable onMoveAction = new Runnable() {

            @Override
            public void run() {
                handler.removeCallbacks(onMoveActionOnce);
                handler.postDelayed(onMoveActionOnce, 150);

            }
        };

        Runnable onMoveFinish = new Runnable() {

            @Override
            public void run() {
                onMoveAction.run();
                if (AppState.get().isRememberDictionary) {
                    DictsHelper.runIntent(dc.getActivity(), AppState.get().selectedText);
                } else {
//                    DragingDialogs.selectTextMenu(anchor, dc, true, onRefresh);
//                    DragingDialogs.myPopup(anchor, dc);
                    showPopupWindow();
                }

            }
        };

        touch1.setOnMoveFinish(onMoveFinish);
        touch2.setOnMoveFinish(onMoveFinish);

        touch1.setOnMove(onMoveAction);
        touch2.setOnMove(onMoveAction);

        updateSeekBarColorAndSize();

        seekBar = findViewById(R.id.seekBar);
        if (AppState.get().isRTL) {
            if (Build.VERSION.SDK_INT >= 11) {
                seekBar.setRotation(180);
            }
        }

        // for full screen

        Keyboards.hideNavigationOnCreate(HorizontalBookReadingActivity.this);

        loadinAsyncTask = new CopyAsyncTask() {
            private boolean isCancelled = false;
            long start = 0;

            @Override
            protected void onPreExecute() {
                start = System.currentTimeMillis();
                Global.showLoading(HorizontalBookReadingActivity.this,"generate_report");
            }

            @Override
            protected Object doInBackground(Object... params) {
                try {
                    LOG.d("doRotation(this)", AppState.get().orientation, HorizontalBookReadingActivity.this.getRequestedOrientation());
                    try {
                        while (viewPager.getHeight() == 0) {
                            Thread.sleep(250);
                        }
                        int count = 0;
                        if (AppState.get().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || AppState.get().orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                            while (viewPager.getHeight() > viewPager.getWidth() && count < 20) {
                                Thread.sleep(250);
                                count++;
                            }
                        } else if (AppState.get().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || AppState.get().orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
                            while (viewPager.getWidth() > viewPager.getHeight() && count < 20) {
                                Thread.sleep(250);
                                count++;
                            }
                        }

                        HorizontalBookReadingActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
//                                updateBannnerTop();
                            }
                        });

                    } catch (InterruptedException e) {
                    }
                    LOG.d("viewPager", viewPager.getHeight() + "x" + viewPager.getWidth());
                    initAsync(viewPager.getWidth(), viewPager.getHeight());
                } catch (MuPdfPasswordException e) {
                    return -1;
                } catch (RuntimeException e) {
                    LOG.e(e);
                    return -2;
                }
                return 0;
            }

            @Override
            protected void onCancelled() {
                Global.hideLoading();
            }


            @Override
            protected void onPostExecute(Object result) {

                Global.hideLoading();
                if (isCancelled) {
                    LOG.d("Cancelled");
                    finish();
                    return;
                }
                if ((Integer) result == -2) {
//                    Toast.makeText(HorizontalBookReadingActivity.this, R.string.msg_unexpected_error, Toast.LENGTH_SHORT).show();
                    AppState.get().isEditMode = true;
                    hideShow();

                    return;
                }

                if ((Integer) result == -1) {
                    final EditText input = new EditText(HorizontalBookReadingActivity.this);
                    input.setSingleLine(true);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(HorizontalBookReadingActivity.this);
                    dialog.setTitle(R.string.enter_password);
                    dialog.setView(input);
                    dialog.setCancelable(false);
                    dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (dc != null) {
                                dc.onCloseActivityFinal(null);
                            } else {
                                HorizontalBookReadingActivity.this.finish();
                            }
                        }

                    });
                    dialog.setPositiveButton(R.string.open_file, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String txt = input.getText().toString();
                            if (TxtUtils.isNotEmpty(txt)) {
                                dialog.dismiss();

                                final Runnable runnable = () -> {
                                    HorizontalBookReadingActivity.this.finish();
                                    getIntent().putExtra(HorizontalModeController.EXTRA_PASSWORD, txt);
                                    startActivity(getIntent());
                                };
                                if (dc != null) {
                                    dc.onCloseActivityFinal(runnable);
                                } else {
                                    runnable.run();
                                }

                            } else {
                                if (dc == null) {
                                    HorizontalBookReadingActivity.this.finish();
                                } else {
                                    dc.onCloseActivityFinal(null);
                                }
                            }
                        }
                    });
                    AlertDialog show = dialog.show();

                } else {

                    seekBar.setVisibility(View.VISIBLE);

                    AppTemp.get().lastClosedActivity = HorizontalBookReadingActivity.class.getSimpleName();
                    AppTemp.get().lastMode = HorizontalBookReadingActivity.class.getSimpleName();
                    LOG.d("lasta save", AppTemp.get().lastClosedActivity);

                    PageImageState.get().isAutoFit = PageImageState.get().needAutoFit;

                    if (ExtUtils.isTextFomat(getIntent())) {
                        PageImageState.get().isAutoFit = true;
                    } else if (AppState.get().isLockPDF) {
                        // moveCenter.setVisibility(View.VISIBLE);
                        AppTemp.get().isLocked = true;
                    }

                    loadUI();
//                    if (Global.pageNumber != 0) {
//                        dc.onGoToPage(Global.pageNumber);
//                        Global.pageNumber = 0;
//                    }

                    // AppState.get().isEditMode = false; //remember last
                    int pageFromUri = dc.getCurentPage();
                    updateUI(pageFromUri);
                    hideShow();

                    EventBus.getDefault().post(new MessageAutoFit(pageFromUri));
                    seekBar.setOnSeekBarChangeListener(onSeek);

                    testScreenshots();

                    isInitPosistion = Dips.screenHeight() > Dips.screenWidth();
                    isInitOrientation = AppState.get().orientation;

                }

            }

        };

        loadinAsyncTask.executeOnExecutor(Executors.newSingleThreadExecutor());
//        updateIconMode();
        BrightnessHelper.updateOverlay(overlay);

        tinUI();
        LOG.d("INIT end", (float) (System.currentTimeMillis() - crateBegin) / 1000);

        anchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= 11) {
                    if (anchor.getX() < 0) {
                        anchor.setX(0);
                    }
                    if (anchor.getY() < 0) {
                        anchor.setY(0);
                    }
                }
            }

        });

        // -- my definition. --
        colorPickValue = ColorCollection.yellow;
        tempColorPickValue = colorPickValue;


        uiInit();
        event();
        drawerLayoutWork();

        faceInitView();

        if (Global.imgMarginValue == 2) {
            imgBigMargin.setImageResource(R.drawable.margin_big2_icon);
            imgMiddleMargin.setImageResource(R.drawable.margin_middle_icon);
            imgSmallMargin.setImageResource(R.drawable.margin_small_icon);
        } else if (Global.imgMarginValue == 1) {
            imgBigMargin.setImageResource(R.drawable.margin_big_icon);
            imgMiddleMargin.setImageResource(R.drawable.margin_middle2_icon);
            imgSmallMargin.setImageResource(R.drawable.margin_small_icon);
        } else {
            imgBigMargin.setImageResource(R.drawable.margin_big_icon);
            imgMiddleMargin.setImageResource(R.drawable.margin_middle_icon);
            imgSmallMargin.setImageResource(R.drawable.margin_small2_icon);
        }

        if (Global.imgLineHeightValue == 2) {
            imgBigLineHeight.setImageResource(R.drawable.spacing_big2_icon);
            imgMiddleLineHeight.setImageResource(R.drawable.spacing_middle_icon);
            imgSmallLineHeight.setImageResource(R.drawable.spacing_small_icon);
        } else if (Global.imgLineHeightValue == 1) {
            imgBigLineHeight.setImageResource(R.drawable.spacing_big_icon);
            imgMiddleLineHeight.setImageResource(R.drawable.spacing_middle2_icon);
            imgSmallLineHeight.setImageResource(R.drawable.spacing_small_icon);
        } else {
            imgBigLineHeight.setImageResource(R.drawable.spacing_big_icon);
            imgMiddleLineHeight.setImageResource(R.drawable.spacing_middle_icon);
            imgSmallLineHeight.setImageResource(R.drawable.spacing_small2_icon);
        }

        if (Global.imgBrightnessValue == 0) {
            imgWhiteBrightness.setImageResource(R.drawable.brightness_white2_icon);
            imgBrownBrightness.setImageResource(R.drawable.brightness_brown_icon);
            imgGreenBrightness.setImageResource(R.drawable.brightness_green_icon);
            imgBlackBrightness.setImageResource(R.drawable.brightness_black_icon);
        } else if (Global.imgBrightnessValue == 1){
            imgWhiteBrightness.setImageResource(R.drawable.brightness_white_icon);
            imgBrownBrightness.setImageResource(R.drawable.brightness_brown2_icon);
            imgGreenBrightness.setImageResource(R.drawable.brightness_green_icon);
            imgBlackBrightness.setImageResource(R.drawable.brightness_black_icon);
        } else if (Global.imgBrightnessValue == 2){
            imgWhiteBrightness.setImageResource(R.drawable.brightness_white_icon);
            imgBrownBrightness.setImageResource(R.drawable.brightness_brown_icon);
            imgGreenBrightness.setImageResource(R.drawable.brightness_green2_icon);
            imgBlackBrightness.setImageResource(R.drawable.brightness_black_icon);
        } else if (Global.imgBrightnessValue == 3){
            imgWhiteBrightness.setImageResource(R.drawable.brightness_white_icon);
            imgBrownBrightness.setImageResource(R.drawable.brightness_brown_icon);
            imgGreenBrightness.setImageResource(R.drawable.brightness_green_icon);
            imgBlackBrightness.setImageResource(R.drawable.brightness_black2_icon);
        }

        if (db.getBookStateData(focusBook.bookId) != null && !db.getBookStateData(focusBook.bookId).time.equals("")) {

            displayedTime = Long.parseLong(db.getBookStateData(focusBook.bookId).time);
            tempDisplayedTime = displayedTime;

        } else {
            displayedTime = 0;
            tempDisplayedTime = 0;
        }
        Log.i("HorizontalBookReading", "displayedTime => " + displayedTime);
        Log.i("HorizontalBookReading", "tempDisplayedTime => " + tempDisplayedTime);

        Log.i("horizontalbookreading", "onCreate => end");
    }

    public void updateSeekBarColorAndSize() {
    }

    public void uiInit() {
        Log.i("horizontalbookreading", "uiInit => start");

        imgFont = findViewById(R.id.imgFont);
        imgBrightness = findViewById(R.id.imgBrightness);
        imgNote = findViewById(R.id.imgNote);
        imgShare = findViewById(R.id.imgShare);

        relFont = findViewById(R.id.relFont);
        relBrightness = findViewById(R.id.relBrightness);
        relNote = findViewById(R.id.relNote);
        relShare = findViewById(R.id.relShare);

        mainBottomBar = findViewById(R.id.mainBottomBar);
        fontBottomBar = findViewById(R.id.fontBottomBar);
        brightnessBottomBar = findViewById(R.id.brightnessBottomBar);

        // for drawerLayout
        linCatalogBack = findViewById(R.id.lin_catalog_back);
        linCatalogCatalog = findViewById(R.id.lin_catalog_catalog);
        linCatalogBookmark = findViewById(R.id.lin_catalog_bookmark);
        linCatalogRefresh = findViewById(R.id.lin_catalog_refresh);

        imgCatalogBack = findViewById(R.id.img_catalog_back);
        imgCatalogCatalog = findViewById(R.id.img_catalog_catalog);
        imgCatalogBookmark = findViewById(R.id.img_catalog_bookmark);
        imgCatalogRefresh = findViewById(R.id.img_catalog_refresh);

        imgWhiteBrightness = findViewById(R.id.imgWhiteBrightness);
        imgBrownBrightness = findViewById(R.id.imgBrownBrightness);
        imgGreenBrightness = findViewById(R.id.imgGreenBrightness);
        imgBlackBrightness = findViewById(R.id.imgBlackBrightness);

        contentList = findViewById(R.id.contentList);
        recyclerContentList = findViewById(R.id.recycler_contentList);

        Log.i("horizontalbookreading", "uiInit => end");
    }

    public void drawerLayoutWork() {
        // for drawerlayout work
        linCatalogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
            }
        });
        linCatalogCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgCatalogCatalog.setImageResource(R.drawable.catalog_catalog_icon);
                imgCatalogBookmark.setImageResource(R.drawable.catalog_bookmark_icon);
                imgCatalogRefresh.setImageResource(R.drawable.catalog_renovate_icon);
                displayContentList();
            }
        });

        linCatalogBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentList.setVisibility(View.GONE);
                recyclerContentList.setVisibility(View.VISIBLE);

                imgCatalogCatalog.setImageResource(R.drawable.catalog_catalog2_icon);
                imgCatalogBookmark.setImageResource(R.drawable.catalog_bookmark3_icon);
                imgCatalogRefresh.setImageResource(R.drawable.catalog_renovate_icon);

                Log.i("HorizontalBookReading", "catalog bookmark");

                ArrayList<AppBookmark> objects = new ArrayList<>();
                ArrayList<AppBookmark> tmpobjects = new ArrayList<>();
                objects.addAll(BookmarksData.get().getBookmarksByBook(dc.getCurrentBook()));
                for (AppBookmark appBookmark : objects){
                    if (appBookmark.isF == true){
                        tmpobjects.add(appBookmark);
                    }
                }

                Log.i("HorizontalBookReading", "bookmark data => " + objects);
                pageBookmarkAdapter = new PageBookmarkAdapter(HorizontalBookReadingActivity.this,  tmpobjects, dc, HorizontalBookReadingActivity.this);
                recyclerContentList.setLayoutManager(new LinearLayoutManager(HorizontalBookReadingActivity.this));
                recyclerContentList.setAdapter(pageBookmarkAdapter);
            }
        });

        linCatalogRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentList.setVisibility(View.GONE);
                recyclerContentList.setVisibility(View.GONE);

                imgCatalogCatalog.setImageResource(R.drawable.catalog_catalog2_icon);
                imgCatalogBookmark.setImageResource(R.drawable.catalog_bookmark_icon);
                imgCatalogRefresh.setImageResource(R.drawable.catalog_renovate2_icon);
            }
        });
    }

    public void displayContentList() {
        contentList.setVisibility(View.VISIBLE);
        recyclerContentList.setVisibility(View.GONE);
        final Runnable showOutline = new Runnable() {
            @Override
            public void run() {
                if (dc != null) {
                    dc.getOutline(new ResultResponse<List<OutlineLinkWrapper>>() {
                        @Override
                        public boolean onResultRecive(List<OutlineLinkWrapper> outline) {

                            contentList.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (outline != null && outline.size() > 0) {
                                        contentList.clearChoices();
                                        OutlineLinkWrapper currentByPageNumber = OutlineHelper.getCurrentChapter(dc);
                                        final OutlineAdapter adapter = new OutlineAdapter(dc.getActivity(), outline, currentByPageNumber, dc.getPageCount());
                                        contentList.setAdapter(adapter);
                                        contentList.setOnItemClickListener(onClickContent);
                                        contentList.setSelection(adapter.getItemPosition(currentByPageNumber) - 3);
                                    }
                                 }
                            });

                            return false;
                        }
                    }, true);
                }
            }
        };

        contentList.postDelayed(showOutline, 50);
    }

    final AdapterView.OnItemClickListener onClickContent = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final OutlineLinkWrapper link = (OutlineLinkWrapper) parent.getItemAtPosition(position);

            if (link.targetPage != -1) {
                int pageCount = dc.getPageCount();
                if (link.targetPage < 1 || link.targetPage > pageCount) {
                    Toast.makeText(anchor.getContext(), "no", Toast.LENGTH_SHORT).show();
                } else {
                    dc.onGoToPage(link.targetPage);
                    // ((ListView) parent).requestFocusFromTouch();
                    // ((ListView) parent).setSelection(position);

                }

                mDrawerLayout.closeDrawers();
                return;
            }

        }
    };

    public void showPopupWindow() {

        View popView;
        popView = LayoutInflater.from(HorizontalBookReadingActivity.this).inflate(R.layout.dragging_popup, null);

        // for popupWindow
        popupBack = popView.findViewById(R.id.popupBack);
        popupColorPickImage = popView.findViewById(R.id.popupColorPickImage);

        popupColorPart = popView.findViewById(R.id.popupColorPart);
        popupDefaultShow = popView.findViewById(R.id.popupDefaultShow);
        popupSearchPart = popView.findViewById(R.id.popupSearchPart);

        popupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupBack.setVisibility(View.GONE);
                popupErase.setVisibility(View.GONE);

                if (colorPickValue == ColorCollection.yellow) {
                    popupColorPickImage.setImageResource(R.drawable.read_yellow_icon);
                }

                if (colorPickValue == ColorCollection.orange) {
                    popupColorPickImage.setImageResource(R.drawable.read_orange_icon);
                }
                if (colorPickValue == ColorCollection.blue) {
                    popupColorPickImage.setImageResource(R.drawable.read_blue_icon);
                }
                if (colorPickValue == ColorCollection.pink) {
                    popupColorPickImage.setImageResource(R.drawable.read_pink_icon);
                }

                popupColorPart.setVisibility(View.GONE);
                popupSearchPart.setVisibility(View.GONE);
                popupDefaultShow.setVisibility(View.VISIBLE);
            }
        });

        popupColorPick = popView.findViewById(R.id.popupColorPick);
        popupColorPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupBack.setVisibility(View.VISIBLE);

                popupColorPart.setVisibility(View.VISIBLE);
                popupDefaultShow.setVisibility(View.GONE);
                popupSearchPart.setVisibility(View.GONE);
            }
        });

        popupErase = popView.findViewById(R.id.popupErase);
        popupErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteType = 4;
                colorPickValue = tempColorPickValue;
                popupErase.setVisibility(View.GONE);
            }
        });

        popupYellow = popView.findViewById(R.id.popupYellow);
        popupYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteType = 0;
                popupErase.setVisibility(View.VISIBLE);

                tempColorPickValue = colorPickValue;
                colorPickValue = ColorCollection.yellow;

                EventBus.getDefault().post(new MsgChangePaintWordsColor(colorPickValue));
            }
        });

        popupOrange = popView.findViewById(R.id.popupOrange);
        popupOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteType = 1;
                popupErase.setVisibility(View.VISIBLE);

                tempColorPickValue = colorPickValue;
                colorPickValue = ColorCollection.orange;

                EventBus.getDefault().post(new MsgChangePaintWordsColor(colorPickValue));

            }
        });

        popupBlue = popView.findViewById(R.id.popupBlue);
        popupBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteType = 2;
                popupErase.setVisibility(View.VISIBLE);

                tempColorPickValue = colorPickValue;
                colorPickValue = ColorCollection.blue;

                EventBus.getDefault().post(new MsgChangePaintWordsColor(colorPickValue));

            }
        });

        popupPink = popView.findViewById(R.id.popupPink);
        popupPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteType = 3;
                popupErase.setVisibility(View.VISIBLE);

                tempColorPickValue = colorPickValue;
                colorPickValue = ColorCollection.pink;

                EventBus.getDefault().post(new MsgChangePaintWordsColor(colorPickValue));

            }
        });

        popupErase = popView.findViewById(R.id.popupErase);
        popupErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupErase.setVisibility(View.GONE);

                colorPickValue = tempColorPickValue;

                ColorCollection temp = ColorCollection.erase;
                EventBus.getDefault().post(new MsgChangePaintWordsColor(temp));
            }
        });

        copyView = findViewById(R.id.copyView);
        popupCopy = popView.findViewById(R.id.popupCopy);
        popupCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowHelper.dismiss();
                String copiedText = AppState.get().selectedText;

                dc.clearSelectedText();
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) HorizontalBookReadingActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(copiedText);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) HorizontalBookReadingActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", copiedText);
                    clipboard.setPrimaryClip(clip);
                }
                copyView.setVisibility(View.VISIBLE);
                copyView.animate()
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                copyView.setVisibility(View.GONE);
                            }
                        });

            }
        });
        popupShare = popView.findViewById(R.id.popupShare);
        popupShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowHelper.dismiss();
                String copiedText = AppState.get().selectedText;
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String txt = "\"" + copiedText.trim() + "\" (" + dc.getBookFileMetaName() + ")";
                intent.putExtra(Intent.EXTRA_TEXT, txt);
                startActivity(Intent.createChooser(intent, dc.getString(R.string.share)));
            }
        });
        popupTranslate = popView.findViewById(R.id.popupTranslate);
        popupTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowHelper.dismiss();
                String translateUrl = String.format("https://fanyi.baidu.com/?aldtype=85#en/zh/%s",AppState.get().selectedText.trim());

                Urls.open(HorizontalBookReadingActivity.this, translateUrl);
            }
        });
        popupSearch = popView.findViewById(R.id.popupSearch);
        popupSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupBack.setVisibility(View.VISIBLE);
                popupColorPart.setVisibility(View.GONE);
                popupDefaultShow.setVisibility(View.GONE);
                popupSearchPart.setVisibility(View.VISIBLE);
//                searchMenu(anchor, dc, AppState.get().selectedText);
            }
        });
        popupSearchBook = popView.findViewById(R.id.popupSearchBook);
        popupSearchBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowHelper.dismiss();
                searchMenu(anchor, dc, AppState.get().selectedText);
            }
        });
        popupSearchNet = popView.findViewById(R.id.popupSearchNet);
        popupSearchNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowHelper.dismiss();
                Urls.open(anchor.getContext(), "https://www.baidu.com/s?wd=" + AppState.get().selectedText.trim());

            }
        });
        popupSearchEncyclopedia = popView.findViewById(R.id.popupSearchEncyclopedia);
        popupSearchEncyclopedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowHelper.dismiss();
                Urls.open(anchor.getContext(), "https://baike.baidu.com/item/" + AppState.get().selectedText.trim());

            }
        });
        popupNote= popView.findViewById(R.id.popupNote);
        popupNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowHelper.dismiss();

                noteDialog = new NoteDialog(HorizontalBookReadingActivity.this, R.style.AppTheme_Alert, "添加笔记", "", HorizontalBookReadingActivity.this);
                noteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                noteDialog.show();
            }
        });
        // for note work
        final String selectedText = AppState.get().selectedText;
        Log.i("HorizontalBookReading", "selected text => " + selectedText);
        noteWork(selectedText);


        popupWindowHelper = new PopupWindowHelper(popView);
        popupWindowHelper.showAsPopUp(anchorX);
    }

    @Override
    public void addNote(String str) {
        OutlineHelper.Info info = OutlineHelper.getForamtingInfo(dc, false);
        String selectedString = AppState.get().selectedText;
        if (selectedString != null && !selectedString.trim().equals("")) {
            final AppBookmark bookmark = new AppBookmark(dc.getCurrentBook().getPath(), selectedString, dc.getPercentage());
            bookmark.setNote(str);
            bookmark.setSubTitle(info.chText);
            bookmark.isF = false;
            bookmark.type = selectedNoteType;
            BookmarksData.get().add(bookmark);
        }
    }
    public void searchMenu(final FrameLayout anchor, final HorizontalModeController controller, final String text) {
        if (controller == null) {
            return;
        }
        searchContentDialog = new SearchContentDialog("search", anchor, 300, 400);
        searchContentDialog.initDialog(this, HorizontalBookReadingActivity.this, controller, text);
        searchContentDialog.show("SearchContent");
        Window rootWindow = getWindow();
        Rect displayRect = new Rect();
        rootWindow.getDecorView().getWindowVisibleDisplayFrame(displayRect);
//        searchContentDialog.().setLayout(800, 1000);
//        searchContentDialog.setCanceledOnTouchOutside(false);

    }
    List<AppBookmark> objects = new ArrayList<>();
    BookmarksAdapter bookmarksAdapter;
    public void noteWork(String selectedText) {

        if (selectedText != null && !selectedText.trim().equals("")) {
            final AppBookmark bookmark = new AppBookmark(dc.getCurrentBook().getPath(), selectedText, dc.getPercentage());
            bookmark.isF = false;
//            BookmarksData.get().add(bookmark);

            Log.i("HorizontalBookReading", "BookmarksData => " + BookmarksData.get().getBookmarksByBook(dc.getCurrentBook()));
//            if (objects != null) {
//                objects.add(0, bookmark);
//            }
//            if (bookmark != null) {
//                bookmarksAdapter.notifyDataSetChanged();
//            }

        }
    }

    public void event() {
        Log.i("horizontalbookreading", "event => start");

        imgSearch = findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMenu(anchor, dc, AppState.get().selectedText);
            }
        });

        // for bookmark
        pagesBookmark = findViewById(R.id.pagesBookmark);
        pagesBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextWord[][] text = dc.codeDocument.getPage(dc.currentPage).getText();
                String bookMarkText = "";
                int limit = 10;
                for (TextWord[] textWords : text){
                    for (TextWord textWord : textWords){
                        limit --;
                        bookMarkText += textWord.w;
                    }
                    if (limit < 0){
                        break;
                    }
                }
                String selectedText = bookMarkText+" ...";

                OutlineHelper.Info info = OutlineHelper.getForamtingInfo(dc, false);
                AppBookmark bookmark = new AppBookmark(dc.getCurrentBook().getPath(), selectedText, dc.getPercentage());
                bookmark.setSubTitle(info.chText);
                if (!BookmarksData.get().hasBookmark(dc.getCurrentBook().getPath(), bookmark.getPage(Integer.parseInt(maxPage))
                        , Integer.parseInt(maxPage))) {
                    pagesBookmark.setImageResource(R.drawable.read_bookmark4_icon);
                    bookmark.isF = true;
                    BookmarksData.get().add(bookmark);
                } else {
                    AppBookmark appBookmark = BookmarksData.get().getBookMark(dc.getCurrentBook(), bookmark.getPage(Integer.parseInt(maxPage)), Integer.parseInt(maxPage));
                    pagesBookmark.setImageResource(R.drawable.catalog_bookmark2_icon);
                    BookmarksData.get().remove(appBookmark);
                }

                Log.i("HorizontalBookReading", "Bookmark page number => " + dc.getCurrentPage());
                updateBookmarksState(String.valueOf(dc.getCurrentPage()));
            }
        });

        imgBookMark = findViewById(R.id.imgBookmark);
        imgBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextWord[][] text = dc.codeDocument.getPage(dc.currentPage).getText();
                String bookMarkText = "";
                int limit = 10;
                for (TextWord[] textWords : text){
                    for (TextWord textWord : textWords){
                        limit --;
                        bookMarkText += textWord.w;
                    }
                    if (limit < 0){
                        break;
                    }
                }
                String selectedText = bookMarkText+" ...";

                OutlineHelper.Info info = OutlineHelper.getForamtingInfo(dc, false);
                AppBookmark bookmark = new AppBookmark(dc.getCurrentBook().getPath(), selectedText, dc.getPercentage());
                bookmark.setSubTitle(info.chText);
                if (!BookmarksData.get().hasBookmark(dc.getCurrentBook().getPath(), bookmark.getPage(Integer.parseInt(maxPage))
                        , Integer.parseInt(maxPage))) {
                    imgBookMark.setImageResource(R.drawable.read_bookmark4_icon);
                    bookmark.isF = true;
                    BookmarksData.get().add(bookmark);
                } else {
                    AppBookmark appBookmark = BookmarksData.get().getBookMark(dc.getCurrentBook(), bookmark.getPage(Integer.parseInt(maxPage)), Integer.parseInt(maxPage));
                    imgBookMark.setImageResource(R.drawable.catalog_bookmark2_icon);
                    BookmarksData.get().remove(appBookmark);
                }
                updateBookmarksState(String.valueOf(dc.getCurrentPage()));
            }
        });
        // to show navigation view.
        catalogMenu = findViewById(R.id.imgCatalogMenu);
        relCatalogMenu = findViewById(R.id.relCatalogMenu);
        relCatalogMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat. START);
                displayContentList();
            }
        });

        relFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fontImageClicked = "yes";
                AppState.get().isEditMode = !AppState.get().isEditMode;
                hideShow();
            }
        });

        // for font size adjustment.
        imgFontSizePlus = findViewById(R.id.imgFontSizePlus);
        imgFontSizeMinus = findViewById(R.id.imgFontSizeMinus);
        txtFontSize = findViewById(R.id.txtFontSize);

        Log.i("HorizontalBookReading", "primary fontsize value => " + BookCSS.get().fontSizeSp);
        fontSizeValue = BookCSS.get().fontSizeSp;
        txtFontSize.setText(String.valueOf(fontSizeValue));

        imgFontSizePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempFontSizeValue = fontSizeValue;

                if (fontSizeValue < max_fontSize) {
                    fontSizeValue = fontSizeValue + 1;
                }

                txtFontSize.setText(String.valueOf(fontSizeValue));
                BookCSS.get().fontSizeSp = fontSizeValue;

                if (tempFontSizeValue != fontSizeValue) {

                    if (onRefresh != null) {
                        onRefresh.run();
                    }

                    Log.i("HorizontalBookReading", "Here operation is crashing.");
                    dc.restartActivity();
                }
            }
        });

        imgFontSizeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempFontSizeValue = fontSizeValue;

                if (fontSizeValue > min_fontSize) {
                    fontSizeValue = fontSizeValue - 1;
                }

                txtFontSize.setText(String.valueOf(fontSizeValue));
                BookCSS.get().fontSizeSp = fontSizeValue;

                if (tempFontSizeValue != fontSizeValue) {
                    if (onRefresh != null) {
                        onRefresh.run();
                    }
                    dc.restartActivity();
                }
            }
        });

        // for font type
        relFontType = findViewById(R.id.relFontType);
        txtFontType = findViewById(R.id.txtFontType);
        relFontType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<BookCSS.FontPack> fontPacks = BookCSS.get().getAllFontsPacks();
                MyPopupMenu popup = new MyPopupMenu(dc.getActivity(), view);
                for (final BookCSS.FontPack pack : fontPacks) {
                    LOG.d("pack.normalFont", pack.normalFont);
                    popup.getMenu().add(pack.dispalyName, pack.normalFont).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            BookCSS.get().resetAll(pack);
                            TxtUtils.underline(txtFontType, BookCSS.get().displayFontName);

                            dc.restartActivity();
                            return false;
                        }
                    });
                }
                popup.show();
            }
        });

        TxtUtils.underline(txtFontType, BookCSS.get().displayFontName);

        // for margin
        imgBigMargin = findViewById(R.id.imgBigMargin);
        imgBigMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgMarginValue = 2;

                BookCSS.get().marginTop = 20;
                BookCSS.get().marginBottom = 20;
                BookCSS.get().marginLeft = 20;
                BookCSS.get().marginRight = 20;

                dc.restartActivity();
            }
        });

        imgMiddleMargin = findViewById(R.id.imgMiddleMargin);
        imgMiddleMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgMarginValue = 1;

                BookCSS.get().marginTop = 15;
                BookCSS.get().marginBottom = 15;
                BookCSS.get().marginLeft = 15;
                BookCSS.get().marginRight = 15;

                dc.restartActivity();
            }
        });

        imgSmallMargin = findViewById(R.id.imgSmallMargin);
        imgSmallMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgMarginValue = 0;

                BookCSS.get().marginTop = 10;
                BookCSS.get().marginBottom = 10;
                BookCSS.get().marginLeft = 10;
                BookCSS.get().marginRight = 10;

                dc.restartActivity();
            }
        });

        // for lineHeight
        imgBigLineHeight = findViewById(R.id.imgBigLineHeight);
        imgBigLineHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgLineHeightValue = 2;
                BookCSS.get().lineHeight = 20;

                nullAdapter();
                dc.restartActivity();
            }
        });

        imgMiddleLineHeight = findViewById(R.id.imgMiddleLineHeight);
        imgMiddleLineHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgLineHeightValue = 1;

                BookCSS.get().lineHeight = 15;

                nullAdapter();
                dc.restartActivity();
            }
        });

        imgSmallLineHeight = findViewById(R.id.imgSmallLineHeight);
        imgSmallLineHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgLineHeightValue = 0;

                BookCSS.get().lineHeight = 12;

                nullAdapter();
                dc.restartActivity();
            }
        });

        // for brightness
        relBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brightnessImageClicked = "yes";
                AppState.get().isEditMode = !AppState.get().isEditMode;
                hideShow();
            }
        });

        seekBarBrightness = (CustomSeek) findViewById(R.id.seekbarBrightness);
        brightnessAutoSetting = (CheckBox) findViewById(R.id.brightnessAutoSetting);

        brightnessValue = 0;
        AppState.get().blueLightColor = Color.parseColor("#000000");
        final int systemBrightnessInt = BrightnessHelper.getSystemBrigtnessInt(HorizontalBookReadingActivity.this);

        if (AppState.get().appBrightness == AppState.AUTO_BRIGTNESS) {
            brightnessValue = systemBrightnessInt;
        } else {
            brightnessValue = AppState.get().isEnableBlueFilter ? AppState.get().blueLightAlpha * -1 : AppState.get().appBrightness;
        }

        Log.i("HorizontalBookReading", "brightnessValue => " + brightnessValue);

        seekBarBrightness.init(-100, 100, brightnessValue);
        seekBarBrightness.setOnSeekChanged(new IntegerResponse() {
            @Override
            public boolean onResultRecive(int result) {
                seekBarBrightness.setValueText("" + result);
//                EventBus.getDefault().post(new MessegeBrightness(result));
                brightnessOperation(result);
                return false;
            }
        });
        brightnessAutoSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                if (isChecked) { //auto
                    seekBarBrightness.setEnabled(false);
                    seekBarBrightness.reset(systemBrightnessInt);
//                    EventBus.getDefault().post(new MessegeBrightness(AppState.AUTO_BRIGTNESS));
                    brightnessOperation(AppState.AUTO_BRIGTNESS);

                    Log.i("HorizontalBookReading", "enable : false ==>> systemBrightnessInt => " + systemBrightnessInt);
                } else {
                    seekBarBrightness.setEnabled(true);
//                    EventBus.getDefault().post(new MessegeBrightness(systemBrightnessInt));
                    brightnessOperation(systemBrightnessInt);

                    Log.i("HorizontalBookReading", "enable : true ==>> systemBrightnessInt => " + systemBrightnessInt);
                }

            }
        });
        brightnessAutoSetting.setChecked(AppState.get().appBrightness == AppState.AUTO_BRIGTNESS);
        seekBarBrightness.setEnabled(AppState.get().appBrightness != AppState.AUTO_BRIGTNESS);


        // for doc background

        imgWhiteBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgBrightnessValue = 0;

                int bg = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.white)));
                int text = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.black)));
                if (dc.isTextFormat() || AppState.get().isCustomizeBgAndColors) {
                    AppState.get().colorDayText = text;
                }
                AppState.get().colorDayBg = bg;
                AppState.get().isUseBGImageDay = false;

                dc.restartActivity();
                dc.restartActivity();
            }
        });

        imgBrownBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgBrightnessValue = 1;

                int bg = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.brown)));
                int text = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.black)));
                if (dc.isTextFormat() || AppState.get().isCustomizeBgAndColors) {
                    AppState.get().colorDayText = text;
                }

                AppState.get().colorDayBg = bg;
                AppState.get().isUseBGImageDay = false;

                dc.restartActivity();


            }
        });

        imgGreenBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgBrightnessValue = 2;

                int bg = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.green)));
                int text = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.black)));
                if (dc.isTextFormat() || AppState.get().isCustomizeBgAndColors) {
                    AppState.get().colorDayText = text;
                }
                AppState.get().colorDayBg = bg;
                AppState.get().isUseBGImageDay = false;

                dc.restartActivity();
            }
        });

        // for day or night.
        imgBlackBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.imgBrightnessValue = 3;

                int bg = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.black)));
                int text = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(HorizontalBookReadingActivity.this, R.color.white)));
                if (dc.isTextFormat() || AppState.get().isCustomizeBgAndColors) {
                    AppState.get().colorDayText = text;
                }
                AppState.get().colorDayBg = bg;
                AppState.get().isUseBGImageDay = false;

                dc.restartActivity();
            }
        });

        relNote = findViewById(R.id.relNote);
        relNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HorizontalBookReadingActivity.this, NoteActivity.class);

                ArrayList<AppBookmark> tmpobjects = new ArrayList<>();
                ArrayList<AppBookmark> objects = new ArrayList<>();
                DocumentController controller = (DocumentController) dc;
                objects.addAll(BookmarksData.get().getBookmarksByBook(controller.getCurrentBook()));
                for (AppBookmark appBookmark : objects){
                    if (appBookmark.isF == false){
                        tmpobjects.add(appBookmark);
                    }
                }
                Global.objects = tmpobjects;
                Log.i("HorizontalBookReading", "sending parameter" + objects);

                startActivity(intent);
            }
        });
        relShare = findViewById(R.id.relShare);
        relShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String txt = focusBook.bookName + "(" + focusBook.bookNameUrl + ")";
                intent.putExtra(Intent.EXTRA_TEXT, txt);
                startActivity(Intent.createChooser(intent, dc.getString(R.string.share)));
            }
        });

    }

    public void brightnessOperation(int value) {
        if (value == AppState.AUTO_BRIGTNESS) {
            AppState.get().isEnableBlueFilter = false;
            // AppState.get().blueLightAlpha = 0;
            AppState.get().appBrightness = AppState.AUTO_BRIGTNESS;

        } else if (value < 0) {
            AppState.get().isEnableBlueFilter = true;
            AppState.get().blueLightAlpha = Math.abs(value);
            AppState.get().appBrightness = 0;

        } else {
            AppState.get().isEnableBlueFilter = false;
            // AppState.get().blueLightAlpha = 0;
            AppState.get().appBrightness = value;
        }

        BrightnessHelper.applyBrigtness(HorizontalBookReadingActivity.this);
        BrightnessHelper.updateOverlay(overlay);
    }

    public void initAsync(int w, int h) {
        Log.i("horizontalbookreading", "initAsync => start");

        dc = new HorizontalModeController(this, w, h) {
            @Override
            public void onGoToPageImpl(int page) {
                updateUI(page);
                EventBus.getDefault().post(new InvalidateMessage());
            }

            @Override
            public void notifyAdapterDataChanged() {
            }

            @Override
            public void showInterstialAndClose() {
            }

        };
        // dc.init(this);
        dc.initAnchor(anchor);

        // added code to insert totalpage to localDB.
        OutlineHelper.Info info = OutlineHelper.getForamtingInfo(dc, false);
        maxPage = info.textPage;

        focusBook.pageCount = maxPage;
        db.updateBookTotalPage(focusBook);
        Global.globalDC = dc;

        Log.i("horizontalbookreading", "initAsync => end");
    }

    private void tinUI() {
        Log.i("horizontalbookreading", "tinUI => start");
        TintUtil.setBgSimple(actionBar, AppState.get().colorDayBg);
        TintUtil.setBgSimple(bottomBar, AppState.get().colorDayBg);
        TintUtil.setBgSimple(topLayout, AppState.get().colorDayBg);
        TintUtil.setBgSimple(bottomLayout, AppState.get().colorDayBg);
        TintUtil.setBgSimple(docBackground, AppState.get().colorDayBg);

        TintUtil.setTextColorSimple(pagesReadingPercent, AppState.get().colorDayText);
        TintUtil.setTextColorSimple(pagesCountIndicator, AppState.get().colorDayText);
        TintUtil.setTextColorSimple(topReadTime, AppState.get().colorDayText);
        TintUtil.setTextColorSimple(toolbar_title, AppState.get().colorDayText);

        TintUtil.setStatusBarColor(this);

        Log.i("horizontalbookreading", "tinUI => end");
    }

    public void makeFullScreen() {
//        DocumentController.showFullScreenPopup(dc.getActivity(), v, id -> {
//            AppState.get().fullScreenMode = id;
//            DocumentController.chooseFullScreen(HorizontalBookReadingActivity.this, AppState.get().fullScreenMode);
//            if (dc.isTextFormat()) {
//                if (onRefresh != null) {
//                    onRefresh.run();
//                }
//                nullAdapter();
//                dc.restartActivity();
//            }
//            return true;
//        }, AppState.get().fullScreenMode);

    }

    UpdatableFragmentPagerAdapter pagerAdapter;

    public boolean prev = true;
    public void hideShow() {
        hideShow(true);
    }

    public void updateUI(int page) {
        Log.i("horizontalbookreading", "updateUI => start");

        if (dc == null || viewPager == null || viewPager.getAdapter() == null) {
            return;
        }

        if (page <= viewPager.getAdapter().getCount() - 1) {
            viewPager.setCurrentItem(page, false);
        }

        OutlineHelper.Info info = OutlineHelper.getForamtingInfo(dc, false);
        maxPage = info.textPage;
        currentPage = info.textMax;
        pagesCountIndicator.setText(info.chText);

        seekBar.setProgress(page);
        if (dc != null) {
            dc.currentPage = page;
        }

        int myLevel = UiSystemUtils.getPowerLevel(this);
        if (myLevel == -1) {
        }
        if (TxtUtils.isNotEmpty(dc.getCurrentChapter())) {
//            chapterView.setText(dc.getCurrentChapter());
//            chapterView.setVisibility(View.VISIBLE);
        } else {
//            chapterView.setVisibility(View.GONE);
        }

        LOG.d("_PAGE", "Update UI", page);
        dc.saveCurrentPage();

        if (dc.floatingBookmark != null) {
            dc.floatingBookmark.p = dc.getPercentage();
//            floatingBookmarkTextView.setText("{" + dc.getCurentPageFirst1() + "}");
//            floatingBookmarkTextView.setVisibility(View.VISIBLE);

            BookmarksData.get().add(dc.floatingBookmark);
//            showPagesHelper();
        } else {
//            floatingBookmarkTextView.setVisibility(View.GONE);
        }

        // for top read time
        if (db.getBookStateData(focusBook.bookId) != null && !db.getBookStateData(focusBook.bookId).time.equals("") && db.getBookStateData(focusBook.bookId).time != null) {
            int seconds = Integer.parseInt(db.getBookStateData(focusBook.bookId).time)/1000;
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            topReadTime.setText(getString(R.string.readTimeStop, hours, minutes, seconds));

            Log.i("HorizontalBookReading", "readTime display => " + db.getBookStateData(focusBook.bookId).time);
        }


        // for percent
//        int pageCount;
//        if (db.getBookStateData(focuse.bookId).getTotalPage() != null && !db.getBookStateData(focuse.bookId).getTotalPage().equals("")) {
//            if (db.getBookStateData(focuse.bookId).getPagesArray().equals("") || db.getBookStateData(focuse.bookId).getPagesArray() == null) {
//                pageCount = 0;
//            } else {
//                pageCount = db.getBookStateData(focuse.bookId).getPagesArray().split(",").length;
//            }
//            int percent = (int) (pageCount / Integer.parseInt(db.getBookStateData(focuse.bookId).getTotalPage()) * 100);
//            pageReadingPercent.setText(getString(R.string.percentBook, String.valueOf(percent)));
//            Log.i("HorizontalBookReading", "percent => " + String.valueOf(percent));
//        }


        if (Integer.parseInt(maxPage) != 0) {
            int pagePercent = page * 100 / Integer.parseInt(maxPage);
            pagesReadingPercent.setText(getString(R.string.book_reading_percent, Integer.toString(pagePercent)) + "%");
            Log.i("horizontalbookreading", "updateUI => end");
        }

    }


    public void testScreenshots() {
        Log.i("horizontalbookreading", "testScreenshots => start");

        if (getIntent().hasExtra("id1")) {
            DragingDialogs.thumbnailDialog(anchor, dc);

        }
        if (getIntent().hasExtra("id2")) {
            DragingDialogs.showContent(anchor, dc);
        }
        if (getIntent().hasExtra("id3")) {
            findViewById(R.id.bookPref).performClick();
        }

        if (getIntent().hasExtra("id4")) {
//            DragingDialogs.selectTextMenu(anchor, dc, true, onRefresh);
//            DragingDialogs.myPopup(anchor, dc);
            showPopupWindow();
        }

        if (getIntent().hasExtra("id5")) {
            DragingDialogs.textToSpeachDialog(anchor, dc);
        }

        if (getIntent().hasExtra("id6")) {
            DragingDialogs.moreBookSettings(anchor, dc, null, null);
        }
        if (getIntent().hasExtra("id7")) {
            FileInformationDialog.showFileInfoDialog(dc.getActivity(), new File(dc.getBookPath()), null);
        }

        if (false) {
            AppState.get().isEditMode = getIntent().getBooleanExtra("isEditMode", false);
            hideShow();
        }

        Log.i("horizontalbookreading", "testScreenshots => end");
    }



    public void loadUI() {
        Log.i("horizontalbookreading", "loadUI => start");
//        titleTxt.setText(dc.getTitle());
//        pannelBookTitle.setText(dc.getTitle());
        createAdapter();

        viewPager.addOnPageChangeListener(onViewPagerChangeListener);
        viewPager.setCurrentItem(dc.getCurentPage(), false);

        seekBar.setMax(dc.getPageCount() - 1);
        seekBar.setProgress(dc.getCurentPage());

//        bottomIndicators.setOnTouchListener(new HorizontallSeekTouchEventListener(onSeek, dc.getPageCount(), false));
//        progressDraw.setOnTouchListener(new HorizontallSeekTouchEventListener(onSeek, dc.getPageCount(), false));
//        bottomIndicators.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (AppState.get().tapZoneBottom == AppState.TAP_DO_NOTHING) {
//                    // do nothing
//                } else if (AppState.get().tapZoneBottom == AppState.TAP_NEXT_PAGE) {
//                    nextPage();
//                } else if (AppState.get().tapZoneBottom == AppState.TAP_PREV_PAGE) {
//                    prevPage();
//                }
//
//            }
//        });

//        updateLockMode();

        tinUI();

        onViewPagerChangeListener.onPageSelected(dc.getCurentPage());

//        progressDraw.updatePageCount(dc.getPageCount());

        dc.getOutline(new ResultResponse<List<OutlineLinkWrapper>>() {

            @Override
            public boolean onResultRecive(List<OutlineLinkWrapper> result) {
//                onClose.setVisibility(View.VISIBLE);
//                progressDraw.updateDivs(result);
                updateUI(dc.getCurrentPage());
                if (TxtUtils.isListEmpty(result)) {
//                    TintUtil.setTintImageWithAlpha(outline, Color.LTGRAY);
                }
//                showPagesHelper();
                return false;
            }
        }, false);

        showHelp();

        Log.i("horizontalbookreading", "loadUI => end");
    }

    private void doShowHideWrapperControlls() {
        Log.i("horizontalbookreading", "doShowHideWrapperControlls => start");

        AppState.get().isEditMode = !AppState.get().isEditMode;
        hideShow();

        Log.i("horizontalbookreading", "doShowHideWrapperControlls => end");
    }

    long lastClick = 0;
    long lastClickMaxTime = 300;

    public synchronized void prevPage() {
        Log.i("horizontalbookreading", "prevPage => start");

        flippingTimer = 0;

        boolean isAnimate = AppState.get().isScrollAnimation;
        if (System.currentTimeMillis() - lastClick < lastClickMaxTime) {
            isAnimate = false;
        }
        lastClick = System.currentTimeMillis();
        viewPager.setCurrentItem(dc.getCurentPage() - 1, isAnimate);
        dc.checkReadingTimer();

        Log.i("horizontalbookreading", "prevPage => end");
    }

    public synchronized void nextPage() {
        Log.i("horizontalbookreading", "nextPage => start");

        flippingTimer = 0;

        boolean isAnimate = AppState.get().isScrollAnimation;
        long lx = System.currentTimeMillis() - lastClick;
        LOG.d("lastClick", lx);
        if (lx < lastClickMaxTime) {
            isAnimate = false;
        }
        lastClick = System.currentTimeMillis();
        viewPager.setCurrentItem(dc.getCurentPage() + 1, isAnimate);
        dc.checkReadingTimer();

        Log.i("horizontalbookreading", "nextPage => end");
    }

    private volatile boolean isMyKey = false;
    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        Log.i("horizontalbookreading", "onKeyUp => start");

        if (isMyKey) {
            return true;
        }
        if (anchor != null && anchor.getVisibility() == View.GONE) {
            if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
                dc.onGoToPage(keyCode - KeyEvent.KEYCODE_1 + 1);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_0) {
                DragingDialogs.thumbnailDialog(anchor, dc);
                return true;
            }

            if (KeyEvent.KEYCODE_MENU == keyCode || KeyEvent.KEYCODE_M == keyCode) {
                doShowHideWrapperControlls();
                return true;
            }
            if (KeyEvent.KEYCODE_F == keyCode) {
                dc.alignDocument();
                return true;
            }

            if (KeyEvent.KEYCODE_S == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode) {
//                showSearchDialog();
                return true;
            }
        }

        Log.i("horizontalbookreading", "onKeyUp => end");
        return super.onKeyUp(keyCode, event);
    }

    long keyTimeout = 0;

    @Override
    public boolean onKeyDown(final int keyCode1, final KeyEvent event) {
        Log.i("horizontalbookreading", "onKeyDown => start");

        int keyCode = event.getKeyCode();
        if (keyCode == 0) {
            keyCode = event.getScanCode();
        }

        isMyKey = false;

        if (AppState.get().isUseVolumeKeys) {

            int repeatCount = event.getRepeatCount();
            if (repeatCount >= 1 && repeatCount < DocumentController.REPEAT_SKIP_AMOUNT) {
                isMyKey = true;
                return true;
            }
            if (repeatCount == 0 && System.currentTimeMillis() - keyTimeout < 250) {
                LOG.d("onKeyDown timeout", System.currentTimeMillis() - keyTimeout);
                isMyKey = true;
                return true;
            }

            keyTimeout = System.currentTimeMillis();


            if (AppState.get().isZoomInOutWithVolueKeys) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    dc.onZoomInc();
                    isMyKey = true;
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    dc.onZoomDec();
                    isMyKey = true;
                    return true;
                }

            }

            LOG.d("onKeyDown", keyCode, repeatCount, System.currentTimeMillis());

            if (AppState.get().isUseVolumeKeys && KeyEvent.KEYCODE_HEADSETHOOK == keyCode) {
                if (TTSEngine.get().isPlaying()) {
                    if (AppState.get().isFastBookmarkByTTS) {
                        TTSEngine.get().fastTTSBookmakr(dc);
                    } else {
//                        TTSEngine.get().stop();
                    }
                } else {
                    //FTTSEngine.get().playCurrent();
                    TTSService.playPause(dc.getActivity(), dc);

                    anchor.setTag("");
                }
                //TTSNotification.showLast();
                DragingDialogs.textToSpeachDialog(anchor, dc);
                return true;
            }

            if (!TTSEngine.get().isPlaying()) {
                if (AppState.get().getNextKeys().contains(keyCode)) {
                    if (closeDialogs()) {
                        isMyKey = true;
                        return true;
                    }
                    if (PageImageState.get().hasSelectedWords()) {
                        dc.clearSelectedText();
                        isMyKey = true;
                        return true;
                    }
                    nextPage();
                    flippingTimer = 0;
                    isMyKey = true;
                    return true;
                } else if (AppState.get().getPrevKeys().contains(keyCode)) {
                    if (closeDialogs()) {
                        isMyKey = true;
                        return true;
                    }
                    if (PageImageState.get().hasSelectedWords()) {
                        dc.clearSelectedText();
                        isMyKey = true;
                        return true;
                    }
                    prevPage();
                    flippingTimer = 0;
                    isMyKey = true;
                    return true;
                }
            }


        }

        Log.i("horizontalbookreading", "onKeyDown => end");
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
        Log.i("horizontalbookreading", "onKeyLongPress => start");

        // Toast.makeText(this, "onKeyLongPress", Toast.LENGTH_SHORT).show();
        if (CloseAppDialog.checkLongPress(this, event)) {
            CloseAppDialog.showOnLongClickDialog(HorizontalBookReadingActivity.this, null, dc);
            return true;
        }

        Log.i("horizontalbookreading", "onKeyLongPress => start");
        return super.onKeyLongPress(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.i("horizontalbookreading", "onBackPressed => start");
        // Toast.makeText(this, "onBackPressed", Toast.LENGTH_SHORT).show();

        if (dc != null && dc.floatingBookmark != null) {
            dc.floatingBookmark = null;
            onRefresh.run();
            return;
        }

        if (anchor != null && anchor.getChildCount() > 0 && anchor.getVisibility() == View.VISIBLE) {
            dc.clearSelectedText();
            try {
                findViewById(R.id.closePopup).performClick();
            } catch (Exception e) {
                LOG.e(e);
            }

            return;
        }

        if (dc != null && !dc.getLinkHistory().isEmpty()) {
            dc.onLinkHistory();
//            showHideHistory();
            return;
        }

        if (AppState.get().isShowLongBackDialog) {

            AppTemp.get().lastClosedActivity = null;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            nullAdapter();

            if (dc != null) {
                dc.saveCurrentPageAsync();
                dc.onCloseActivityFinal(null);
                dc.closeActivity();
            } else {
                finish();
            }
        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            // the read time is calculated in onPause override function when you click back button
            super.onBackPressed();
        }

        Log.i("horizontalbookreading", "onBackPressed => end");
    }

    public void createAdapter() {
        Log.i("horizontalbookreading", "createAdapter => start");

        LOG.d("createAdapter");
        nullAdapter();
        pagerAdapter = null;
        final int count = dc.getPageCount();
        pagerAdapter = new UpdatableFragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public Fragment getItem(final int position) {
                final ImagePageFragment imageFragment = new ImagePageFragment();

                final Bundle b = new Bundle();
                b.putInt(ImagePageFragment.POS, position);
                b.putBoolean(ImagePageFragment.IS_TEXTFORMAT, dc.isTextFormat());
                b.putString(ImagePageFragment.PAGE_PATH, dc.getPageUrl(position).toString());

                imageFragment.setArguments(b);
                return imageFragment;
            }

            @Override
            public Parcelable saveState() {
                try {
                    return super.saveState();
                } catch (Exception e) {
//                    Toast.makeText(HorizontalBookReadingActivity.this, R.string.msg_unexpected_error, Toast.LENGTH_LONG).show();
                    LOG.e(e);
                    return null;
                }
            }

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {
                try {
                    super.restoreState(arg0, arg1);
                } catch (Exception e) {
//                    Toast.makeText(HorizontalBookReadingActivity.this, R.string.msg_unexpected_error, Toast.LENGTH_LONG).show();
                    LOG.e(e);
                }
            }

        };
        int pagesInMemory = AppState.get().pagesInMemory;
        if (pagesInMemory == 1 || pagesInMemory == 0) {
            pagesInMemory = 0;
        } else if (pagesInMemory == 3) {
            pagesInMemory = 1;
        } else if (pagesInMemory == 5) {
            pagesInMemory = 2;
        } else {
            pagesInMemory = 1;
        }
        viewPager.setOffscreenPageLimit(pagesInMemory);
        LOG.d("setOffscreenPageLimit", pagesInMemory);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setSaveEnabled(false);
        viewPager.setSaveFromParentEnabled(false);

        Log.i("horizontalbookreading", "createAdapter => end");
    }

    public void showHelp() {
        Log.i("horizontalbookreading", "showHelp => start");

        if (AppTemp.get().isFirstTimeHorizontal) {
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    AppTemp.get().isFirstTimeHorizontal = false;
                    AppState.get().isEditMode = true;
                    hideShow();
//                    Views.showHelpToast(lockModelImage);

                }
            }, 1000);
        }

        Log.i("horizontalbookreading", "showHelp => end");
    }

    @Override
    protected void attachBaseContext(Context context) {
        Log.i("horizontalbookreading", "attachBaseContext => start");

        AppProfile.init(context);
        closeDialogs();
        super.attachBaseContext(MyContextWrapper.wrap(context));

        Log.i("horizontalbookreading", "attachBaseContext => end");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("horizontalbookreading", "onRequestPermissionsResult => start");

        Android6.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

        // for face detect.
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                initEngine();
                initCamera();
                if (cameraHelper != null) {
                    cameraHelper.start();
                    updateBookLastTimeState();  // update lastTime

                }
            } else {
//                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }

        Log.i("horizontalbookreading", "onRequestPermissionsResult => end");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("horizontalbookreading", "onStart => start");

        // Analytics.onStart(this);
        EventBus.getDefault().register(HorizontalBookReadingActivity.this);

        Log.i("horizontalbookreading", "onStart => end");
    }

    @Override
    public void onStop() {
        Log.i("horizontalbookreading", "onStop => start");

        EventBus.getDefault().unregister(this);
        super.onStop();
        // Analytics.onStop(this);
        if (flippingHandler != null) {
            flippingHandler.removeCallbacksAndMessages(null);
        }

        RecentUpates.updateAll(this);

        Log.i("horizontalbookreading", "onStop => end");
    }

    @Override
    protected void onDestroy() {
        Log.i("horizontalbookreading", "onDestroy => start");

        if (loadinAsyncTask != null) {
            try {
                loadinAsyncTask.cancel(true);
                loadinAsyncTask = null;
            } catch (Exception e) {
                LOG.e(e);
            }
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (flippingHandler != null) {
            flippingHandler.removeCallbacksAndMessages(null);
        }
        nullAdapter();

        // AppTemp.get().isCut = false;
        PageImageState.get().clearResouces();

        // ----------- for face detect ----------

        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }

        //faceHelper中可能会有FR耗时操作仍在执行，加锁防止crash
        if (faceHelper != null) {
            synchronized (faceHelper) {
                unInitEngine();
            }
            ConfigUtil.setTrackId(this, faceHelper.getCurrentTrackId());
            faceHelper.release();
        } else {
            unInitEngine();
        }
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.dispose();
            getFeatureDelayedDisposables.clear();
        }
        FaceServer.getInstance().unInit();

        Log.i("horizontalbookreading", "onDestroy => end");
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("horizontalbookreading", "onResume => start");

        countTime = System.currentTimeMillis();       // this part is my definition for real time communication with api.
        faceDetectStartTime = 0;

        if (db.getBookStateData(focusBook.bookId) != null && !db.getBookStateData(focusBook.bookId).time.equals("")) {

            displayedTime = Long.parseLong(db.getBookStateData(focusBook.bookId).time);
            tempDisplayedTime = displayedTime;

        } else {
            displayedTime = 0;
            tempDisplayedTime = 0;
        }

        DocumentController.chooseFullScreen(this, AppState.get().fullScreenMode);
        DocumentController.doRotation(this);

        if (clickUtils != null) {
            clickUtils.init();
        }

        if (dc != null) {
            dc.onResume();
        }

        if (dc != null) {
            dc.goToPageByTTS();
        }


        handler.removeCallbacks(closeRunnable);
//        handlerTimer.post(updateTimePower);

        if(AppState.get().inactivityTime!=-1) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            LOG.d("FLAG addFlags", "FLAG_KEEP_SCREEN_ON", "add",AppState.get().inactivityTime);

        }

//        if (ttsActive != null) {
//            ttsActive.setVisibility(TxtUtils.visibleIf(TTSEngine.get().isTempPausing()));
//        }
        Log.i("horizontalbookreading", "onResume => end");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("horizontalbookreading", "onConfigurationChanged => start");

        TempHolder.isActiveSpeedRead.set(false);
        clickUtils.init();
        if (isInitPosistion == null) {
            return;
        }
        handler.removeCallbacksAndMessages(null);

        final boolean currentPosistion = Dips.screenHeight() > Dips.screenWidth();
        if (ExtUtils.isTextFomat(getIntent()) && isInitOrientation == AppState.get().orientation) {

            if (rotatoinDialog != null) {
                try {
                    rotatoinDialog.dismiss();
                } catch (Exception e) {
                    LOG.e(e);
                }
            }

            if (isInitPosistion != currentPosistion) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setCancelable(false);
                dialog.setMessage(R.string.apply_a_new_screen_orientation_);
                dialog.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRotateScreen();
                        isInitPosistion = currentPosistion;
                    }
                });
                rotatoinDialog = dialog.show();
                rotatoinDialog.getWindow().setLayout((int) (Dips.screenMinWH() * 0.8f), ActionBar.LayoutParams.WRAP_CONTENT);
            }
        } else {
            Keyboards.hideNavigationOnCreate(this);
            dc.udpateImageSize(dc.isTextFormat(), viewPager.getWidth(), viewPager.getHeight());
            onRotateScreen();
        }

        isInitOrientation = AppState.get().orientation;

        Log.i("horizontalbookreading", "onConfigurationChanged => start");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onRotateScreen() {
        // ADS.activate(this, adView);
//        activateAds();

        AppProfile.save(this);
        if (ExtUtils.isTextFomat(getIntent())) {
            nullAdapter();
            dc.restartActivity();
        } else {
            if (viewPager != null) {
                authoFit();
            }
        }
    }

    public void authoFit() {
        if (handler == null) {
            return;
        }
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                PageImageState.get().isAutoFit = true;
                if (dc != null) {
                    dc.cleanImageMatrix();
                }
                EventBus.getDefault().post(new MessageAutoFit(viewPager.getCurrentItem()));
            }
        }, 50);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppProfile.save(this);
        TempHolder.isSeaching = false;
        TempHolder.isActiveSpeedRead.set(false);
        handler.postDelayed(closeRunnable, AppState.APP_CLOSE_AUTOMATIC);

        // when this activity is paused, must calculate read time.
        if (faceDetectStartTime != 0) {
            readTime = System.currentTimeMillis() - faceDetectStartTime;
            faceDetectStartTime = 0;
            updateBookReadTimeState(String.valueOf(readTime));
//
//            int seconds = Integer.parseInt(db.getBookStateData(focuse.bookId).getReadTime()) / 1000;
//            int minutes = seconds / 60;
//            topReadTime.setText(getString(R.string.readTime, String.valueOf(minutes), String.valueOf(seconds)));
//            topReadTime.setTextColor(getResources().getColor(R.color.black));
            Log.i("HorizontalBookReading", "readTime display => " + db.getBookStateData(focusBook.bookId).time);
        }

    }

    public static String fontImageClickTemp = "no";
    public static String brightnessImageClickTemp = "no";

    public void hideShow(boolean animated) {
//        updateBannnerTop();
//        showPagesHelper();

        if (prev == AppState.get().isEditMode) {
            return;
        }
        prev = AppState.get().isEditMode;

        if (!animated || AppState.get().appTheme == AppState.THEME_INK) {
            actionBar.setVisibility(AppState.get().isEditMode ? View.VISIBLE : View.GONE);
            bottomBar.setVisibility(AppState.get().isEditMode ? View.VISIBLE : View.GONE);
//            adFrame.setVisibility(AppState.get().isEditMode ? View.VISIBLE : View.GONE);

            DocumentController.chooseFullScreen(this, AppState.get().fullScreenMode);
            return;
        }

        final TranslateAnimation hideActionBar = new TranslateAnimation(0, 0, 0, -actionBar.getHeight());
        final TranslateAnimation hideBottomBar = new TranslateAnimation(0, 0, 0, bottomBar.getHeight());

        final TranslateAnimation showActoinBar = new TranslateAnimation(0, 0, -actionBar.getHeight(), 0);
        final TranslateAnimation showBottomBar = new TranslateAnimation(0, 0, bottomBar.getHeight(), 0);

        updateAnimation(hideActionBar);
        updateAnimation(hideBottomBar);

        updateAnimation(showActoinBar);
        updateAnimation(showBottomBar);

        if (AppState.get().isEditMode) {
            DocumentController.turnOnButtons(this);

            actionBar.startAnimation(showActoinBar);
            bottomBar.startAnimation(showBottomBar);

            showBottomBar.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(final Animation animation) {
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    actionBar.setVisibility(View.VISIBLE);
                    bottomBar.setVisibility(View.VISIBLE);

                    Keyboards.invalidateEink(parentParent);

                }
            });

        } else {
            DocumentController.turnOffButtons(this);

            actionBar.startAnimation(hideActionBar);
            bottomBar.startAnimation(hideBottomBar);

            hideBottomBar.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(final Animation animation) {
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    actionBar.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.GONE);

                    Keyboards.invalidateEink(parentParent);

                }

            });

        }

        if (pagerAdapter != null) {
            DocumentController.chooseFullScreen(this, AppState.get().fullScreenMode);
            pagerAdapter.notifyDataSetChanged();
        }

        mainBottomBar.setVisibility(View.GONE);
        if (fontImageClicked.equals("yes")) {

            fontImageClicked = "no";
            fontImageClickTemp = "yes";
            AppState.get().isEditMode = !AppState.get().isEditMode;
            hideShow();
        } else if (fontImageClickTemp.equals("yes")) {

            mainBottomBar.setVisibility(View.GONE);
            fontBottomBar.setVisibility(View.VISIBLE);
            brightnessBottomBar.setVisibility(View.GONE);

//            fontImageClickTemp = "no";
        } else if (brightnessImageClicked.equals("yes")) {

            brightnessImageClicked = "no";
            brightnessImageClickTemp = "yes";
            AppState.get().isEditMode = !AppState.get().isEditMode;
            hideShow();
        } else if (brightnessImageClickTemp.equals("yes")) {

            mainBottomBar.setVisibility(View.GONE);
            fontBottomBar.setVisibility(View.GONE);
            brightnessBottomBar.setVisibility(View.VISIBLE);

            brightnessImageClickTemp = "no";
        } else {
            mainBottomBar.setVisibility(View.VISIBLE);
            fontBottomBar.setVisibility(View.GONE);
            brightnessBottomBar.setVisibility(View.GONE);
        }

    }

    private void updateAnimation(final TranslateAnimation a) {
        a.setDuration(250);
    }

    public void nullAdapter() {
        if (viewPager != null) {
            try {

                ImageLoader.getInstance().clearAllTasks();
                closeDialogs();
                viewPager.setAdapter(null);
            } catch (Exception e) {
                LOG.e(e);
            }
        }
    }

    private boolean closeDialogs() {
        if (dc == null) {
            return false;
        }
        return dc.closeDialogs();
    }

    Runnable onRefresh = new Runnable() {

        @Override
        public void run() {
            dc.saveCurrentPageAsync();
            updateUI(viewPager.getCurrentItem());
//            showHideInfoToolBar();
            updateSeekBarColorAndSize();
            BrightnessHelper.updateOverlay(overlay);
            hideShow();
//            TTSEngine.get().stop();
//            showPagesHelper();

        }
    };

    public View.OnClickListener onBookmarks = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            DragingDialogs.showBookmarksDialog(anchor, dc, new Runnable() {

                @Override
                public void run() {
//                    showHideHistory();
//                    showPagesHelper();
                    updateUI(dc.getCurrentPage());
                }
            });
        }
    };

    View.OnLongClickListener onBookmarksLong = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(final View arg0) {
            DragingDialogs.addBookmarksLong(anchor, dc);
//            showPagesHelper();
            return true;
        }
    };


    Runnable closeRunnable = new Runnable() {

        @Override
        public void run() {
            LOG.d("Close App");
            if (dc != null) {
                dc.saveCurrentPageAsync();
                dc.onCloseActivityAdnShowInterstial();
                dc.closeActivity();
            } else {
                finish();
            }
            MainTabs2.closeApp(HorizontalBookReadingActivity.this);
        }
    };

    private int currentScrollState;
    Runnable clearFlags = new Runnable() {

        @Override
        public void run() {
            try {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                LOG.d("FLAG clearFlags", "FLAG_KEEP_SCREEN_ON","clear");
            } catch (Exception e) {
                LOG.e(e);
            }
        }
    };

    ViewPager.OnPageChangeListener onViewPagerChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int pos) {
            if (BookmarksData.get().hasBookmark(dc.getCurrentBook().getPath(), pos + 1, Integer.parseInt(maxPage))) {
                pagesBookmark.setImageResource(R.drawable.read_bookmark4_icon);
                imgBookMark.setImageResource(R.drawable.read_bookmark4_icon);
            } else {
                pagesBookmark.setImageResource(R.drawable.catalog_bookmark2_icon);
                imgBookMark.setImageResource(R.drawable.catalog_bookmark2_icon);
            }


            PageImageState.currentPage = pos;
            dc.setCurrentPage(viewPager.getCurrentItem());
            updateUI(pos);

            if (PageImageState.get().isAutoFit) {
                EventBus.getDefault().post(new MessageAutoFit(pos));
            }


            if (AppState.get().inactivityTime > 0) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                LOG.d("FLAG addFlags", "FLAG_KEEP_SCREEN_ON", "add",AppState.get().inactivityTime);
                handler.removeCallbacks(clearFlags);
                handler.postDelayed(clearFlags, TimeUnit.MINUTES.toMillis(AppState.get().inactivityTime));
            }

            LOG.d("onPageSelected", pos);

//            progressDraw.updateProgress(pos);

            EventBus.getDefault().post(new MessagePageXY(MessagePageXY.TYPE_HIDE));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

            currentScrollState = arg0;

            pageChangedTime = System.currentTimeMillis();
            if (pageChangedTime > faceDetectStartTime && faceDetectStartTime != 0) {
                updateBookPageNumbersState(currentPage);
//
//                int percent = (int) (db.getBookStateData(focuse.bookId).getPagesArray().split(",").length / Integer.parseInt(db.getBookStateData(focuse.bookId).getTotalPage()) * 100);
//                pageReadingPercent.setText(getString(R.string.percentBook, String.valueOf(percent)));
//                Log.i("HorizontalBookReading", "percent => " + String.valueOf(percent));
            }
//            Toast.makeText(HorizontalBookReadingActivity.this, "page changed Time" + currentPage, Toast.LENGTH_SHORT).show();
        }
    };


    SeekBar.OnSeekBarChangeListener onSeek = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(final SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
            // updateUI(progress);
            viewPager.setCurrentItem(progress, false);
            flippingTimer = 0;

            if (AppState.get().isEditMode && !fromUser) {
                AppState.get().isEditMode = false;
                hideShow();
            }
        }
    };


    Runnable flippingRunnable = new Runnable() {

        @Override
        public void run() {

            if (flippingTimer >= AppState.get().flippingInterval) {
                flippingTimer = 0;
                if (dc.getCurentPage() == dc.getPageCount() - 1) {
                    if (AppState.get().isLoopAutoplay) {
                        dc.onGoToPage(1);
                    } else {
                        onFlippingStop(null);
                        return;
                    }
                } else {
                    nextPage();
                }
            }

            flippingTimer += 1;
//            flippingIntervalView.setText("{" + (AppState.get().flippingInterval - flippingTimer + 1) + "}");
//            flippingIntervalView.setVisibility(AppState.get().isShowToolBar ? View.VISIBLE : View.GONE);
            flippingHandler.postDelayed(flippingRunnable, 1000);

        }
    };

    @Subscribe
    public void showHideTextSelectors(MessagePageXY event) {
        if (event.getType() == MessagePageXY.TYPE_HIDE) {
            anchorX.setVisibility(View.GONE);
            anchorY.setVisibility(View.GONE);

        }
        if (event.getType() == MessagePageXY.TYPE_SHOW) {
            anchorX.setVisibility(View.VISIBLE);
            anchorY.setVisibility(View.VISIBLE);

            float x = event.getX() - anchorX.getWidth();
            float y = event.getY() - anchorX.getHeight() / 2;

            AnchorHelper.setXY(anchorX, x, y);
            AnchorHelper.setXY(anchorY, event.getX1(), event.getY1());

        }
        if (event.getType() == MessagePageXY.TYPE_SELECT_TEXT) {
            float x = event.getX() - anchorX.getWidth();
            float y = event.getY() - anchorX.getHeight() / 2;
            AnchorHelper.setXY(anchorX, x, y);
            AnchorHelper.setXY(anchorY, event.getX1(), event.getY1());

        }
    }

    @Subscribe
    public void onMessegeBrightness(MessegeBrightness msg) {
        BrightnessHelper.applyBrigtness(HorizontalBookReadingActivity.this);
        BrightnessHelper.updateOverlay(overlay);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTTSStatus(TtsStatus status) {
        try {
//            ttsActive.setVisibility(TxtUtils.visibleIf(!TTSEngine.get().isShutdown()));
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageNumber(final MessagePageNumber event) {
        try {
//            ttsActive.setVisibility(View.VISIBLE);
            dc.onGoToPage(event.getPage() + 1);
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    boolean isFlipping = false;

    @Subscribe
    public void onFlippingStart(FlippingStart event) {
        isFlipping = true;
        flippingTimer = 0;
        flippingHandler.removeCallbacks(flippingRunnable);
        flippingHandler.post(flippingRunnable);
//        flippingIntervalView.setText("");
//        flippingIntervalView.setVisibility(AppState.get().isShowToolBar ? View.VISIBLE : View.GONE);

        if (AppState.get().isEditMode) {
            AppState.get().isEditMode = false;
            hideShow();
        }

//        onPageFlip1.setVisibility(View.VISIBLE);
//        onPageFlip1.setImageResource(R.drawable.glyphicons_37_file_pause);
    }

    @Subscribe
    public void onFlippingStop(FlippingStop event) {
        isFlipping = false;
        flippingHandler.removeCallbacks(flippingRunnable);
        flippingHandler.removeCallbacksAndMessages(null);
//        flippingIntervalView.setVisibility(View.GONE);
//        onPageFlip1.setImageResource(R.drawable.glyphicons_37_file_play);

    }

    Runnable doShowHideWrapperControllsRunnable = new Runnable() {

        @Override
        public void run() {
            doShowHideWrapperControlls();
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Subscribe
    public void onEvent(MessageEvent ev) {

        if(currentScrollState != ViewPager.SCROLL_STATE_IDLE){
            LOG.d("Skip event");
            return;
        }

        if (fontImageClickTemp.equals("yes")) {

            fontImageClickTemp = "no";
        }


        clickUtils.init();
        LOG.d("MessageEvent", ev.getMessage(), ev.getX(), ev.getY());
        if (ev.getMessage().equals(MessageEvent.MESSAGE_CLOSE_BOOK)) {
//            showInterstial();
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_CLOSE_BOOK_APP)) {
            dc.onCloseActivityFinal(new Runnable() {

                @Override
                public void run() {
                    MainTabs2.closeApp(dc.getActivity());
                }
            });
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_PERFORM_CLICK)) {
            boolean isOpen = closeDialogs();
            if (isOpen) {
                return;
            }

            int x = (int) ev.getX();
            int y = (int) ev.getY();
            if (clickUtils.isClickRight(x, y) && AppState.get().tapZoneRight != AppState.TAP_DO_NOTHING) {
                if (AppState.get().tapZoneRight == AppState.TAP_NEXT_PAGE) {
                    nextPage();
                } else {
                    prevPage();
                }
            } else if (clickUtils.isClickLeft(x, y) && AppState.get().tapZoneLeft != AppState.TAP_DO_NOTHING) {
                if (AppState.get().tapZoneLeft == AppState.TAP_PREV_PAGE) {
                    prevPage();
                } else {
                    nextPage();
                }
            } else if (clickUtils.isClickTop(x, y) && AppState.get().tapZoneTop != AppState.TAP_DO_NOTHING) {
                if (AppState.get().tapZoneTop == AppState.TAP_PREV_PAGE) {
                    prevPage();
                } else {
                    nextPage();
                }

            } else if (clickUtils.isClickBottom(x, y) && AppState.get().tapZoneBottom != AppState.TAP_DO_NOTHING) {
                if (AppState.get().tapZoneBottom == AppState.TAP_NEXT_PAGE) {
                    nextPage();
                } else {
                    prevPage();
                }

            } else {
                LOG.d("Click-center!", x, y);
                handler.removeCallbacks(doShowHideWrapperControllsRunnable);
                handler.postDelayed(doShowHideWrapperControllsRunnable, 250);
                // Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
            }
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_DOUBLE_TAP)) {
            handler.removeCallbacks(doShowHideWrapperControllsRunnable);
//            updateLockMode();
            // Toast.makeText(this, "DB", Toast.LENGTH_SHORT).show();
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_PLAY_PAUSE)) {
            TTSService.playPause(HorizontalBookReadingActivity.this, dc);
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_SELECTED_TEXT)) {
            if (dc.isTextFormat() && TxtUtils.isFooterNote(AppState.get().selectedText)) {
                DragingDialogs.showFootNotes(anchor, dc, new Runnable() {

                    @Override
                    public void run() {
//                        showHideHistory();
                    }
                });
            } else {

                if (AppState.get().isRememberDictionary) {
                    final String text = AppState.get().selectedText;
                    DictsHelper.runIntent(dc.getActivity(), text);
                    dc.clearSelectedText();
                } else {
//                    DragingDialogs.selectTextMenu(anchor, dc, true, onRefresh);
//                    DragingDialogs.myPopup(anchor, dc);
                    showPopupWindow();
                }
            }
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_GOTO_PAGE_BY_LINK)) {
            if (ev.getPage() == -1 && TxtUtils.isNotEmpty(ev.getBody())) {
                AlertDialogs.openUrl(this, ev.getBody());
            } else {
                dc.getLinkHistory().add(dc.getCurentPage() + 1);
                dc.onGoToPage(ev.getPage() + 1);
//                showHideHistory();
            }
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_GOTO_PAGE_SWIPE)) {
            if (ev.getPage() > 0) {
                nextPage();
            } else {
                prevPage();
            }
        } else if (ev.getMessage().equals(MessageEvent.MESSAGE_AUTO_SCROLL)) {
            if (isFlipping) {
                onFlippingStop(null);
            } else {
                onFlippingStart(null);
            }

        }

    }

    private void faceInitView() {
        previewView = findViewById(R.id.texture_preview);
        //在布局结束后才做初始化操作
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        faceRectView = findViewById(R.id.face_rect_view);

        compareResultList = new ArrayList<>();

    }

    /**
     * 初始化引擎
     */
    private void initEngine() {
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);

        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(this, getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
        FaceServer.getInstance().init(this);
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {

        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFail: " + e.getMessage());
                faceDetectStartTime = 0;
            }
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId) {
                if (faceFeature != null) {
                    searchFace(faceFeature, requestId);
                }
                else {
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                }
            }

        };


        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror, false, false);
                Log.i(TAG, "onCameraOpened: " + drawHelper.toString());
                faceHelper = new FaceHelper.Builder()
                        .faceEngine(faceEngine)
                        .frThreadNum(MAX_DETECT_NUM)
                        .previewSize(previewSize)
                        .faceListener(faceListener)
                        .currentTrackId(ConfigUtil.getTrackId(HorizontalBookReadingActivity.this.getApplicationContext()))
                        .build();
            }


            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }

                List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21);
                if (facePreviewInfoList != null && faceRectView != null && drawHelper != null) {
                    drawPreviewInfo(facePreviewInfoList);
                }

//                Toast.makeText(HorizontalBookReadingActivity.this, "here real time operation", Toast.LENGTH_SHORT).show();
                long tempTime = System.currentTimeMillis();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("horizontalbookreading", "time interval => " + (tempTime - countTime));

                        if (faceDetectStartTime != 0) {
                            long currentTempTime = System.currentTimeMillis();
                            long deltaTime = currentTempTime- faceDetectStartTime;

                            displayedTime = tempDisplayedTime + deltaTime;

                            int seconds = (int) displayedTime / 1000;
                            int minutes = seconds / 60;
                            int hours = minutes / 60;
                            seconds = seconds % 60;
//                            topReadTime.setText(getString(R.string.readTime, String.valueOf(hours), String.valueOf(minutes), String.valueOf(seconds)));
                            topReadTime.setText(getString(R.string.readTime, hours, minutes, seconds));
                            Log.i("HorizontalBookReading", "displayedTime => " + displayedTime);
                            Log.i("HorizontalBookReading", "tempDisplayedTime => " + tempDisplayedTime);
                        }else{
                            tempDisplayedTime = displayedTime;
                            int seconds = (int) displayedTime/1000;
                            int minutes = seconds / 60;
                            int hours = minutes / 60;
                            seconds = seconds % 60;
                            topReadTime.setText(getString(R.string.readTimeStop, hours, minutes, seconds));
                        }

                        if (tempTime - countTime > 20000 && stateChangFlag) {
                            bookStateChangeApiOperation();

                            stateChangFlag = false;
                            countTime = System.currentTimeMillis();

                        }
                    }
                });


                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        if (livenessDetect) {
                            livenessMap.put(facePreviewInfoList.get(i).getTrackId(), facePreviewInfoList.get(i).getLivenessInfo().getLiveness());
                        }
                        /**
                         * 对于每个人脸，若状态为空或者为失败，则请求FR（可根据需要添加其他判断以限制FR次数），
                         * FR回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer)}中回传
                         */
                        if (requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null
                                || requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == RequestFeatureStatus.FAILED) {
                            requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                            faceHelper.requestFaceFeature(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());
//                            Log.i(TAG, "onPreview: fr start = " + System.currentTimeMillis() + " trackId = " + facePreviewInfoList.get(i).getTrackId());
                        }
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(rgbCameraID != null ? rgbCameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
        updateBookLastTimeState();      // update last time.
    }

    private void drawPreviewInfo(List<FacePreviewInfo> facePreviewInfoList) {
        List<DrawInfo> drawInfoList = new ArrayList<>();
        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            String name = faceHelper.getName(facePreviewInfoList.get(i).getTrackId());
            Integer liveness = livenessMap.get(facePreviewInfoList.get(i).getTrackId());
            drawInfoList.add(new DrawInfo(drawHelper.adjustRect(facePreviewInfoList.get(i).getFaceInfo().getRect()), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE,
                    liveness == null ? LivenessInfo.UNKNOWN : liveness,
                    name == null ? String.valueOf(facePreviewInfoList.get(i).getTrackId()) : name));
        }
        drawHelper.draw(faceRectView, drawInfoList);
    }

    private void searchFace(final FaceFeature frFace, final Integer requestId) {
        Observable.create(new ObservableOnSubscribe<CompareResult>() {
            @Override
            public void subscribe(ObservableEmitter<CompareResult> emitter) {
                CompareResult compareResult = FaceServer.getInstance().getTopOfFaceLib(frFace);
                if (compareResult == null) {
                    emitter.onError(null);
                } else {
                    emitter.onNext(compareResult);
                }
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        if (compareResult == null || compareResult.getUserName() == null) {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "VISITOR " + requestId);
                            return;
                        }
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {

                            faceDetectStartTime = System.currentTimeMillis();

                            boolean isAdded = false;
                            if (compareResultList == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                faceHelper.addName(requestId, "VISITOR " + requestId);
                                return;
                            }
                            for (CompareResult compareResult1 : compareResultList) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (!isAdded) {
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper.addName(requestId, compareResult.getUserName());

                        } else {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "VISITOR " + requestId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }
    }

    public void bookStateChangeApiOperation() {

        if (ConnectivityHelper.isConnectedToNetwork(HorizontalBookReadingActivity.this)) {

            Log.e("horizontalbookreading", "HHHHHHHH");
            Log.i("horizontalbookreading", Global.KEY_token + ":" + Global.access_token);
            Log.i("horizontalbookreading", "bookId" + ":" + focusBook.bookId);
            Log.i("horizontalbookreading", "page" + ":" + db.getBookStateData(focusBook.bookId).pages);
            Log.i("horizontalbookreading", "time" + ":" + db.getBookStateData(focusBook.bookId).time);
            Log.i("horizontalbookreading", "endPoint" + ":" + db.getBookStateData(focusBook.bookId).lastRead);

            focusBook.bookStatus = db.getBookStateData(focusBook.bookId);
            Ion.with(HorizontalBookReadingActivity.this)
                    .load(Url.bookStateChangeOperation)
                    .setTimeout(10000)
                    .setBodyParameter("access_token", Global.access_token)
                    .setBodyParameter("bookId", focusBook.bookId)
                    .setBodyParameter("pages", focusBook.bookStatus.pages)
                    .setBodyParameter("time", focusBook.bookStatus.time)
                    .setBodyParameter("bookmarks", focusBook.bookStatus.bookmarks)
                    .setBodyParameter("lastRead", focusBook.bookStatus.lastRead)
                    .setBodyParameter("progress", focusBook.bookStatus.progress)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Log.i("horizontalbookreading", "real time api result" + result);
                            String temp;

                            if (e == null) {
                                JSONObject resObj = null;
                                try {
                                    resObj = new JSONObject(result.toString());
                                    temp = resObj.getString("res");         // if operation is done successfully, get "succe".  if not, get "false"

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }
                    });

        } else {
//            Toast.makeText(HorizontalBookReadingActivity.this, "Network is disconnected.", Toast.LENGTH_SHORT).show();
        }


    }

    public void updateBookPageNumbersState(String pageNumber) {
        String pages = focusBook.bookStatus.pages;
        if (pages.equals("")) {
            focusBook.bookStatus.pages = pageNumber;
            db.updateBookPageNumbersState(focusBook.bookStatus, focusBook.bookId);

            stateChangFlag = true;
        } else {

            if (pages.contains(pageNumber)) {
//                    one.setPagesArray(pages);
            } else {
                focusBook.bookStatus.pages = pages + "," + pageNumber;
                db.updateBookPageNumbersState(focusBook.bookStatus, focusBook.bookId);

                stateChangFlag = true;
            }
        }

        String[] pageArray = focusBook.bookStatus.pages.split(",");
        int percent = 0;
        if (focusBook.pageCount.isEmpty() || focusBook.pageCount.equals("0")){
            percent = 0;
        }else{
            percent = pageArray.length * 100 / Integer.parseInt(focusBook.pageCount);
        }
        focusBook.bookStatus.progress = Integer.toString(percent);
        db.updateBookProgressState(focusBook.bookStatus, focusBook.bookId);
        Log.i("horizontalbookreading", "pageNumberState flag=>" + stateChangFlag);

    }

    public void updateBookReadTimeState(String readTime) {
        String tempReadTime = focusBook.bookStatus.time;
        if (tempReadTime.equals("")) {
            focusBook.bookStatus.time = readTime;

        } else {
            long saveReadTime = Long.valueOf(tempReadTime) + Long.valueOf(readTime);
            focusBook.bookStatus.time = String.valueOf(saveReadTime);
        }
        db.updateBookReadTimeState(focusBook.bookStatus, focusBook.bookId);
        stateChangFlag = true;

        Log.i("horizontalbookreading", "updateBookReadTimeState" + stateChangFlag);

    }

    public void updateBookLastTimeState() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yy-MM-dd ");
        String lastTime = mdformat.format(calendar.getTime());

        focusBook.bookStatus.lastRead = lastTime;
        db.updateBookLastTimeState(focusBook.bookStatus, focusBook.bookId);
    }

    public void updateBookmarksState(String pageNumber) {
        String bookmarks = focusBook.bookStatus.bookmarks;

        if (bookmarks.equals("")) {
            focusBook.bookStatus.bookmarks = pageNumber;
        } else {

            if (bookmarks.contains(pageNumber)) {
                String[] pageArray = bookmarks.split(",");
                if (pageArray.length == 1) {
                    focusBook.bookStatus.bookmarks = "";
                } else {
                    int index = bookmarks.indexOf(pageNumber);
                    String tempFrontStr = bookmarks.substring(0, index - 1);
                    String lastPageNumber = bookmarks.substring(bookmarks.lastIndexOf(",") + 1);

                    // in case of last character.
                    if (pageNumber.equals(lastPageNumber)) {
                        focusBook.bookStatus.bookmarks = tempFrontStr;
                    } else {
                        Log.i("HorizontalBookReading", "length => middle index");
                        String tempEndStr = bookmarks.substring(index + pageNumber.length());
                        focusBook.bookStatus.bookmarks = tempFrontStr + tempEndStr;
                    }
                }

            } else {
                // add bookmarks
                Log.i("HorizontalBookReading", "insert pageNumber");
                focusBook.bookStatus.bookmarks = bookmarks + "," + pageNumber;
            }
        }

        Log.i("HorizontalBookReading", "bookmarks => " + focusBook.bookStatus.bookmarks);
        db.updateBookmarksState(focusBook.bookStatus, focusBook.bookId);
        stateChangFlag = true;

    }


    @Override
    public void selectedBookItem(String pageNumber) {
        if (!pageNumber.isEmpty()){
            dc.onGoToPage(Integer.parseInt(pageNumber));
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onSelectedContent(int page) {
        dc.onGoToPage(page);
        searchContentDialog.closeDialog();
    }

    public void onDismissSearchDialog() {
        TempHolder.isSeaching = false;
    }

}
