package ours.china.hours.BookLib.foobnix.android.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import ours.china.hours.BookLib.foobnix.model.AppState;

import org.ebookdroid.HoursApp;

import java.util.Objects;

public class Vibro {

    public static void vibrate() {
        vibrate(100);
    }

    @TargetApi(26)
    public static void vibrate(long time) {
//        if (AppState.get().isVibration) {
//            if (Build.VERSION.SDK_INT >= 26) {
//                ((Vibrator) Objects.requireNonNull(HoursApp.context.getSystemService(Context.VIBRATOR_SERVICE))).vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
//            } else {
//                ((Vibrator) Objects.requireNonNull(HoursApp.context.getSystemService(Context.VIBRATOR_SERVICE))).vibrate(time);
//            }
//        }
        LOG.d("Vibro", "vibrate", time);
    }

}
