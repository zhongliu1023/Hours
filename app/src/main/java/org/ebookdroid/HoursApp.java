package org.ebookdroid;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.multidex.MultiDexApplication;

import ours.china.hours.BookLib.artifex.mupdf.fitz.StructuredText;
import ours.china.hours.BookLib.foobnix.android.utils.Apps;
import ours.china.hours.BookLib.foobnix.android.utils.Dips;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.ext.CacheZipUtils;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.pdf.info.AppsConfig;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.tts.TTSNotification;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;

import org.ebookdroid.common.bitmaps.BitmapManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LibreraApp extends MultiDexApplication {

    static {
        System.loadLibrary("mypdf");
        System.loadLibrary("mobi");
        System.loadLibrary("antiword");
    }

    public static Context context;

    @Override
    public void onCreate() {

//        if (BuildConfig.DEBUG) {
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }

        super.onCreate();

        //new FileMeta().equals() remember!!!

        if (!AppsConfig.checkIsProInstalled(this)) {

        }

        context = getApplicationContext();

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

        LOG.d("Build", "Build.MANUFACTURER", Build.MANUFACTURER);
        LOG.d("Build", "Build.PRODUCT", Build.PRODUCT);
        LOG.d("Build", "Build.DEVICE", Build.DEVICE);
        LOG.d("Build", "Build.BRAND", Build.BRAND);
        LOG.d("Build", "Build.MODEL", Build.MODEL);

        LOG.d("Build", "Build.screenWidth", Dips.screenWidthDP(), Dips.screenWidth());

        LOG.d("Build.Context", "Context.getFilesDir()", getFilesDir());
        LOG.d("Build.Context", "Context.getCacheDir()", getCacheDir());
        LOG.d("Build.Context", "Context.getExternalCacheDir", getExternalCacheDir());
        LOG.d("Build.Context", "Context.getExternalFilesDir(null)", getExternalFilesDir(null));
        LOG.d("Build.Context", "Environment.getExternalStorageDirectory()", Environment.getExternalStorageDirectory());
        LOG.d("Build.Height", Dips.screenHeight());


        if (false) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, final Throwable e) {
                    LOG.e(e);
                    e.printStackTrace();
                    try {

                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        String log = errors.toString();
                        log = log + "/n";
                        log = log + Build.MANUFACTURER + "/n";
                        log = log + Build.PRODUCT + "/n";
                        log = log + Build.DEVICE + "/n";
                        log = log + Build.BRAND + "/n";
                        log = log + Build.BRAND + "/n";
                        log = log + Build.MODEL + "/n";
                        log = log + Build.VERSION.SDK_INT + "/n";
                        Apps.onCrashEmail(context, log, context.getString(R.string.application_error_please_send_this_report_by_emial));

                        System.exit(1);

                    } catch (Exception e1) {
                        LOG.e(e1);
                    }
                }
            });
        }


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LOG.d("AppState save onLowMemory");
        IMG.clearMemoryCache();
        BitmapManager.clear("on Low Memory: ");
        TintUtil.clean();
    }

}
