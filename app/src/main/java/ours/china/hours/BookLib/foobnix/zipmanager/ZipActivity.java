package ours.china.hours.BookLib.foobnix.zipmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ours.china.hours.BookLib.foobnix.OpenerActivity;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.R;

import org.ebookdroid.BookType;

public class ZipActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppState.get().isDayNotInvert) {
            setTheme(R.style.StyledIndicatorsWhite);
        } else {
            setTheme(R.style.StyledIndicatorsBlack);
        }
        super.onCreate(savedInstanceState);


        LOG.d("ZipActivity", getIntent());
        LOG.d("ZipActivity Data", getIntent().getData());
        LOG.d("ZipActivity Path", getIntent().getData().getPath());
        LOG.d("ZipActivity Scheme", getIntent().getScheme());
        LOG.d("ZipActivity Mime", getIntent().getType());


        final String path = getIntent().getData().getPath();
        final String contentName = OpenerActivity.getContentName(this);

        LOG.d("ZipActivity contentName", contentName);

        if ((BookType.isSupportedExtByPath(path) || BookType.isSupportedExtByPath(contentName) ||
                BookType.getByMimeType(getIntent().getType()) != null) && !BookType.ZIP.is(path)) {
            final Intent intent = new Intent(this, OpenerActivity.class);
            intent.setData(getIntent().getData());
            startActivity(intent);
            finish();
            return;
        }

        ZipDialog.show(this, getIntent().getData(), new Runnable() {

            @Override
            public void run() {
                // finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
