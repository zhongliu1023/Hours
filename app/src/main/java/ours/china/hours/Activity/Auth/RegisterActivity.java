package ours.china.hours.Activity.Auth;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.kaopiz.kprogresshud.KProgressHUD;

import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Common.Utils.FontUtility;
import ours.china.hours.Management.SoftKeyboardHandle;
import ours.china.hours.R;

/**
 * Created by liujie on 1/6/18.
 */

public class RegisterActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initActionBar();
        init();
        setListener();
    }
    private void initActionBar(){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_default);
        getSupportActionBar().setElevation(0);
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        titleText.setText("");
        ImageView backImage = (ImageView) findViewById(R.id.backImageView);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void init(){

    }

    private void setListener(){

    }

}