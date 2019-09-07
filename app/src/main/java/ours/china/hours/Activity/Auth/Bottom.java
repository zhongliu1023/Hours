package ours.china.hours.Activity.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import ours.china.hours.R;

public class Bottom extends AppCompatActivity implements WheelPicker.OnItemSelectedListener, View.OnClickListener{

    private WheelPicker wheelLeft;
    private WheelPicker wheelCenter;
    private WheelPicker wheelRight;
    private ImageButton btnPerfectDown, btnPerfectUp;

    private Button gotoBtn, btnDown, btnUp, btnPerfectDone;
    private Integer gotoBtnItemIndex;

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_copy);


        wheelCenter = (WheelPicker) findViewById(R.id.main_wheel_center);
        wheelCenter.setOnItemSelectedListener(this);

        btnPerfectDone = (Button) findViewById(R.id.btnPerfectDone);
//        randomlySetGotoBtnIndex();
////        gotoBtn.setOnClickListener(this);
        btnPerfectDone.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Toast.makeText(Bottom.this, wheelCenter.getData().get(wheelCenter.getCurrentItemPosition()).toString(), Toast.LENGTH_SHORT).show();
            }
        });


        btnPerfectDown = (ImageButton)findViewById(R.id.btnPerfectDown);
        btnPerfectDown.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                gotoBtnItemIndex = wheelCenter.getCurrentItemPosition();
                wheelCenter.setSelectedItemPosition(gotoBtnItemIndex + 1);
            }
        });

        btnPerfectUp = (ImageButton)findViewById(R.id.btnPerfectUp);
        btnPerfectUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                gotoBtnItemIndex = wheelCenter.getCurrentItemPosition();
                wheelCenter.setSelectedItemPosition(gotoBtnItemIndex - 1);
            }
        });

//        btnDown = (Button)findViewById(R.id.btnDown);
//        btnDown.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View view) {
//                gotoBtnItemIndex = wheelCenter.getCurrentItemPosition();
//                wheelCenter.setSelectedItemPosition(gotoBtnItemIndex + 1);
//            }
//        });

    }


    private void randomlySetGotoBtnIndex() {
        gotoBtnItemIndex = (int) (Math.random() * wheelCenter.getData().size());
        gotoBtn.setText("Goto '" + wheelCenter.getData().get(gotoBtnItemIndex) + "'");
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        String text = "";
        switch (picker.getId()) {
            case R.id.main_wheel_center:
                text = "Center:";
                break;
        }
        Toast.makeText(this, text + String.valueOf(data), Toast.LENGTH_SHORT).show();
        data = data;
    }

    @Override
    public void onClick(View v) {
//        wheelCenter.setSelectedItemPosition(gotoBtnItemIndex);
//        randomlySetGotoBtnIndex();
    }

}
