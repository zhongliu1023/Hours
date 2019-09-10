package ours.china.hours.BookLib.foobnix.tts;

import android.app.Activity;
import android.os.Bundle;

import ours.china.hours.BookLib.foobnix.model.AppProfile;

public class TTSActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppProfile.init(this);
        TTSService.playLastBook();
        finish();

    }

}
