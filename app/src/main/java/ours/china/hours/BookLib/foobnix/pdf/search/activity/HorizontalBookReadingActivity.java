package ours.china.hours.BookLib.foobnix.pdf.search.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
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
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import ours.china.hours.BookLib.foobnix.android.utils.Dips;
import ours.china.hours.BookLib.foobnix.android.utils.Keyboards;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.ResultResponse;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.android.utils.Views;
import ours.china.hours.BookLib.foobnix.ext.CacheZipUtils;
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
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.pdf.info.UiSystemUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.model.OutlineLinkWrapper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.AlertDialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.AnchorHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.BookmarkPanel;
import ours.china.hours.BookLib.foobnix.pdf.info.view.BrightnessHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.Dialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DialogsPlaylist;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DragingDialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.HorizontallSeekTouchEventListener;
import ours.china.hours.BookLib.foobnix.pdf.info.view.HypenPanelHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.DraggbleTouchListener;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.FileInformationDialog;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.RecentUpates;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.FlippingStart;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.FlippingStop;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.InvalidateMessage;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessageAutoFit;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessageEvent;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessagePageXY;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessegeBrightness;
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
import ours.china.hours.DB.DBController;
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
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.BookStatus;
import ours.china.hours.R;
import ours.china.hours.Utility.ConnectivityHelper;

public class HorizontalBookReadingActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    // ------------- for document browser ----------------
    VerticalViewPager viewPager;
    View parentParent, pagesBookmark, overlay;
    LinearLayout actionBar, bottomBar;

    TextView pagesCountIndicator;
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
    LinearLayout mainBottomBar, fontBottomBar, brightnessBottomBar;

    public static String fontImageClicked = "no";
    public static String brightnessImageClicked = "no";

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
    /**
     * 当FR成功，活体未成功时，FR等待活体的时间
     *
     * When the FR is successful and the living body is unsuccessful, the FR waits for the living time.
     */
    private static final int WAIT_LIVENESS_INTERVAL = 50;
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    /**
     * 优先打开的摄像头，本界面主要用于单目RGB摄像头设备，因此默认打开前置
     *
     * Priority open camera, this interface is mainly used for monocular RGB camera devices, so the default is to open the front
     */
    private Integer rgbCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private FaceEngine faceEngine;
    private FaceHelper faceHelper;
    private List<CompareResult> compareResultList;
    private ShowFaceInfoAdapter adapter;
    /**
     * 活体检测的开关  -->  Live detection switch
     */
    private boolean livenessDetect = true;

    /**
     * 注册人脸状态码，准备注册   -->   Register face status code, ready to register
     */
    private static final int REGISTER_STATUS_READY = 0;
    /**
     * 注册人脸状态码，注册中     -->   Register face status code, registering
     */
    private static final int REGISTER_STATUS_PROCESSING = 1;
    /**
     * 注册人脸状态码，注册结束（无论成功失败）   -->   Register face status code, registration ends (regardless of success)
     */
    private static final int REGISTER_STATUS_DONE = 2;

    private int registerStatus = REGISTER_STATUS_DONE;

    private int afCode = -1;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     *
     * The camera preview shows the controls, which can be SurfaceView or TextureView
     */
    private View previewView;
    /**
     * 绘制人脸框的控件     -->     a control that draws a face frame
     */
    private FaceRectView faceRectView;

    private Switch switchLivenessDetect;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final float SIMILAR_THRESHOLD = 0.8F;
    /**
     * 所需的所有权限信息    -->     All required permission information]
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE

    };

    //-----------------------------------------------


    private Book focusBook = null;
    SharedPreferencesManager sessionManager;


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
                    DragingDialogs.selectTextMenu(anchor, dc, true, onRefresh);
                }

            }
        };

        touch1.setOnMoveFinish(onMoveFinish);
        touch2.setOnMoveFinish(onMoveFinish);

        touch1.setOnMove(onMoveAction);
        touch2.setOnMove(onMoveAction);

        pagesCountIndicator = (TextView) findViewById(R.id.pagesCountIndicator);

        updateSeekBarColorAndSize();

        seekBar = findViewById(R.id.seekBar);
        if (AppState.get().isRTL) {
            if (Build.VERSION.SDK_INT >= 11) {
                seekBar.setRotation(180);
            }
        }

        pagesBookmark = findViewById(R.id.pagesBookmark);
        pagesBookmark.setOnClickListener(onBookmarks);
        pagesBookmark.setOnLongClickListener(onBookmarksLong);

        // for full screen


        // for day or night.
        ImageView dayNightButton = findViewById(R.id.imgBlackBrightness);
        dayNightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dc == null) {
                    return;
                }

                view.setEnabled(false);
                AppState.get().isDayNotInvert = !AppState.get().isDayNotInvert;
                nullAdapter();
                dc.restartActivity();
            }
        });

        Keyboards.hideNavigationOnCreate(HorizontalBookReadingActivity.this);

        loadinAsyncTask = new CopyAsyncTask() {
            AlertDialog dialog;
            private boolean isCancelled = false;
            long start = 0;

            @Override
            protected void onPreExecute() {
                start = System.currentTimeMillis();
                TempHolder.get().loadingCancelled = false;
                dialog = Dialogs.loadingBook(HorizontalBookReadingActivity.this, new Runnable() {

                    @Override
                    public void run() {
                        isCancelled = true;
                        TempHolder.get().loadingCancelled = true;
                        CacheZipUtils.removeFiles(CacheZipUtils.CACHE_BOOK_DIR.listFiles());
                        finish();
                    }
                });
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
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                }
                isCancelled = true;
            }


            @Override
            protected void onPostExecute(Object result) {

                try {
                    // onClose.setVisibility(View.VISIBLE);
                    LOG.d("RESULT", result);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                }
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

        // my definition.

        uiInit();
        event();


        FaceServer.getInstance().init(this);
        faceInitView();

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

        mainBottomBar = findViewById(R.id.mainBottomBar);
        fontBottomBar = findViewById(R.id.fontBottomBar);
        brightnessBottomBar = findViewById(R.id.brightnessBottomBar);

        Log.i("horizontalbookreading", "uiInit => end");
    }

    public void event() {
        Log.i("horizontalbookreading", "event => start");

        // to show navigation view.
        catalogMenu = findViewById(R.id.imgCatalogMenu);
        catalogMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat. START);
            }
        });

        imgFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fontImageClicked = "yes";
                AppState.get().isEditMode = !AppState.get().isEditMode;
                hideShow();
            }
        });

        imgBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brightnessImageClicked = "yes";
                AppState.get().isEditMode = !AppState.get().isEditMode;
                hideShow();
            }
        });

        Log.i("horizontalbookreading", "event => end");
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

        Log.i("horizontalbookreading", "initAsync => end");
    }

    private void tinUI() {
        Log.i("horizontalbookreading", "tinUI => start");

        TintUtil.setTintBgSimple(actionBar, AppState.get().transparencyUI);
        TintUtil.setTintBgSimple(bottomBar, AppState.get().transparencyUI);
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
            int seconds = Integer.parseInt(db.getBookStateData(focusBook.bookId).time) / 1000;
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            topReadTime.setText(getString(R.string.readTimeStop, hours, minutes, seconds));

            topReadTime.setTextColor(getResources().getColor(R.color.black));
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




        Log.i("horizontalbookreading", "updateUI => end");
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
            DragingDialogs.selectTextMenu(anchor, dc, true, onRefresh);
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

    }

    @Subscribe
    public void onMessegeBrightness(MessegeBrightness msg) {
//        BrightnessHelper.onMessegeBrightness(handler, msg, toastBrightnessText, overlay);
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
                    DragingDialogs.selectTextMenu(anchor, dc, true, onRefresh);
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
        switchLivenessDetect = findViewById(R.id.switch_liveness_detect);
        switchLivenessDetect.setChecked(livenessDetect);
        switchLivenessDetect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                livenessDetect = isChecked;
            }
        });
        RecyclerView recyclerShowFaceInfo = findViewById(R.id.recycler_view_person);
        compareResultList = new ArrayList<>();
        adapter = new ShowFaceInfoAdapter(compareResultList, this);
        recyclerShowFaceInfo.setAdapter(adapter);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int spanCount = (int) (dm.widthPixels / (getResources().getDisplayMetrics().density * 100 + 0.5f));
        recyclerShowFaceInfo.setLayoutManager(new GridLayoutManager(this, spanCount));
        recyclerShowFaceInfo.setItemAnimator(new DefaultItemAnimator());
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
        Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);

        if (afCode != ErrorInfo.MOK) {
//            Toast.makeText(this, getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
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
            }

            //请求FR的回调
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId) {
                //FR成功
                if (faceFeature != null) {
//                    Log.i(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);

                    //不做活体检测的情况，直接搜索
                    if (!livenessDetect) {
                        searchFace(faceFeature, requestId);
                    }
                    //活体检测通过，搜索特征
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        searchFace(faceFeature, requestId);
                    }
                    //活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }
                    //活体检测失败
                    else {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.NOT_ALIVE);
                    }

                }
                //FR 失败
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

                registerFace(nv21, facePreviewInfoList);
                clearLeftFace(facePreviewInfoList);

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
                            topReadTime.setTextColor(getResources().getColor(R.color.black));

                            Log.i("HorizontalBookReading", "displayedTime => " + displayedTime);
                            Log.i("HorizontalBookReading", "tempDisplayedTime => " + tempDisplayedTime);
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


    private void registerFace(final byte[] nv21, final List<FacePreviewInfo> facePreviewInfoList) {
        if (registerStatus == REGISTER_STATUS_READY && facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
            registerStatus = REGISTER_STATUS_PROCESSING;
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {
                    boolean success = FaceServer.getInstance().registerNv21(HorizontalBookReadingActivity.this, nv21.clone(), previewSize.width, previewSize.height,
                            facePreviewInfoList.get(0).getFaceInfo(), "registered " + faceHelper.getCurrentTrackId());

                    emitter.onNext(success);
                }
            })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean success) {
                            String result = success ? "register success!" : "register failed!";
//                            Toast.makeText(HorizontalBookReadingActivity.this, result, Toast.LENGTH_SHORT).show();
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onError(Throwable e) {
//                            Toast.makeText(HorizontalBookReadingActivity.this, "register failed!", Toast.LENGTH_SHORT).show();
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
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

    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {

        Set<Integer> keySet = requestFeatureStatusMap.keySet();

        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!keySet.contains(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
                    adapter.notifyItemRemoved(i);

                    faceDetectRemovedTime = System.currentTimeMillis();
                    if (faceDetectStartTime != 0) {
                        readTime = faceDetectRemovedTime - faceDetectStartTime;
                        faceDetectStartTime = 0;
                        updateBookReadTimeState(String.valueOf(readTime));

//                        Toast.makeText(this, "Here, detected face is removed." + readTime, Toast.LENGTH_SHORT).show();

                        tempDisplayedTime = displayedTime;

                        int seconds = (int) displayedTime / 1000;
                        int minutes = seconds / 60;
                        int hours = minutes / 60;
                        seconds = seconds % 60;
//                        topReadTime.setText(getString(R.string.readTimeStop, String.valueOf(hours), String.valueOf(minutes), String.valueOf(seconds)));

                        topReadTime.setText(getString(R.string.readTimeStop, hours, minutes, seconds));
                        topReadTime.setTextColor(getResources().getColor(R.color.black));

//                        // for r
//                        int seconds = Integer.parseInt(db.getBookStateData(focuse.bookId).getReadTime()) / 1000;
//                        int minutes = seconds / 60;
//                        topReadTime.setText(getString(R.string.readTime, String.valueOf(minutes), String.valueOf(seconds)));
//                        topReadTime.setTextColor(getResources().getColor(R.color.black));
                        Log.i("HorizontalBookReading", "readTime display => " + db.getBookStateData(focusBook.bookId).time);
                    } else {
//                        Toast.makeText(this, "Here, detected face is removed. faceDetectStartTime=" + faceDetectStartTime, Toast.LENGTH_SHORT).show();
                    }



                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            requestFeatureStatusMap.clear();
            livenessMap.clear();
            return;
        }

        for (Integer integer : keySet) {
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == integer) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(integer);
                livenessMap.remove(integer);
            }
        }

    }

    private void searchFace(final FaceFeature frFace, final Integer requestId) {
        Observable.create(new ObservableOnSubscribe<CompareResult>() {
            @Override
            public void subscribe(ObservableEmitter<CompareResult> emitter) {
//                        Log.i(TAG, "subscribe: fr search start = " + System.currentTimeMillis() + " trackId = " + requestId);
                CompareResult compareResult = FaceServer.getInstance().getTopOfFaceLib(frFace);
//                        Log.i(TAG, "subscribe: fr search end = " + System.currentTimeMillis() + " trackId = " + requestId);

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
//                        Toast.makeText(HorizontalBookReadingActivity.this, "Help1", Toast.LENGTH_SHORT).show();

                        if (compareResult == null || compareResult.getUserName() == null) {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "VISITOR " + requestId);
                            return;
                        }

//                        Log.i(TAG, "onNext: fr search get result  = " + System.currentTimeMillis() + " trackId = " + requestId + "  similar = " + compareResult.getSimilar());
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {

                            faceDetectStartTime = System.currentTimeMillis();
//                            Toast.makeText(HorizontalBookReadingActivity.this, "Here, face detect started " +faceDetectStartTime, Toast.LENGTH_SHORT).show();

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
                                //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                                if (compareResultList.size() >= MAX_DETECT_NUM) {
                                    compareResultList.remove(0);
                                    adapter.notifyItemRemoved(0);
                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);
                                adapter.notifyItemInserted(compareResultList.size() - 1);

//                                String tempByteStr = new String(frFace.getFeatureData());
//                                alerDialogWork(tempByteStr);
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


    /**
     * 将准备注册的状态置为{@link #REGISTER_STATUS_READY}
     *
     * @param view 注册按钮
     */
    public void register(View view) {
        if (registerStatus == REGISTER_STATUS_DONE) {
            registerStatus = REGISTER_STATUS_READY;
        }
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

            Ion.with(HorizontalBookReadingActivity.this)
                    .load(Url.bookStateChangeOperation)
                    .setTimeout(10000)
                    .setBodyParameter("access_token", Global.access_token)
                    .setBodyParameter("bookId", focusBook.bookId)
                    .setBodyParameter("page", db.getBookStateData(focusBook.bookId).pages)
                    .setBodyParameter("time", db.getBookStateData(focusBook.bookId).time)
                    .setBodyParameter("endPoint", db.getBookStateData(focusBook.bookId).lastRead)
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


}
