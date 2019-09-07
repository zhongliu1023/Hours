package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import ours.china.hours.R;

public class UpdateinforActivity extends AppCompatActivity {

    private ImageView imgUpdateInfoBack;
    private TextView tvUpdateInfoComplete, tvUpdateInfoIdentify, tvUpdateInfoFace, tvUpdateInfoClass;
    private EditText edUpdateInfoName, edUpdateInfoStudId;
    private BottomSheetDialog bottomSheetDialog;
    public ImageButton btnUpdateInfoUp, btnUpdateInfoDown;
    public Button btnDone;
    private WheelPicker wheelCenter;
    private Integer gotoBtnItemIndex;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfor);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        showBottomSheetDialog();
    }


    private void initUI(){

        imgUpdateInfoBack = (ImageView)findViewById(R.id.imgUpdateInfoBack);
        tvUpdateInfoComplete = (TextView)findViewById(R.id.tvUpdateInfoComplete);
        tvUpdateInfoIdentify = (TextView)findViewById(R.id.tvUpdateInfoIdentify);
        tvUpdateInfoFace = (TextView)findViewById(R.id.tvUpdateInfoFace);
        tvUpdateInfoClass = (TextView)findViewById(R.id.tvUpdateInfoClass);
        edUpdateInfoName = (EditText) findViewById(R.id.edUpdateName);
        edUpdateInfoStudId = (EditText)findViewById(R.id.edUpdateInfoStudid);
    }


    private void showBottomSheetDialog(){

        tvUpdateInfoClass.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                openDlg();
            }
        });
    }


    private void openDlg(){

        View view = getLayoutInflater().inflate(R.layout.bottomsheet_updateinfo, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        btnUpdateInfoUp = (ImageButton)view.findViewById(R.id.btnUpdateInfoUp);
        btnUpdateInfoDown = (ImageButton)view.findViewById(R.id.btnUpdateInfoDown);
        btnDone = (Button)view.findViewById(R.id.btnUpdateDone);
        wheelCenter = (WheelPicker)view.findViewById(R.id.wheelUpdateInfo);

        setClassName();

        bottomSheetDialog.show();
    }


    private void closeDlg(){

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()){
            bottomSheetDialog.dismiss();
            bottomSheetDialog = null;
        }
    }


    private void setClassName(){

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                data = wheelCenter.getData().get(wheelCenter.getCurrentItemPosition()).toString();
                tvUpdateInfoClass.setText(data.toString());

                closeDlg();
            }
        });
    }


    private void upAnddownPicker(){

        btnUpdateInfoUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
//                btnUpdateInfoUp.setImageDrawable(getResources().getDrawable(R.drawable.im));
            }
        });
    }

}
