package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import ours.china.hours.Activity.Global;
import ours.china.hours.Dialog.OutDlg;
import ours.china.hours.R;

public class UpdateinforActivity extends AppCompatActivity {

    private ImageView imgUpdateInfoBack;
    private TextView tvUpdateInfoComplete, tvUpdateInfoIdentify, tvUpdateInfoFace, tvUpdateInfoClass;
    private EditText edUpdateInfoName, edUpdateInfoStudId, edUpdateInfoSchool;
    private BottomSheetDialog bottomSheetDialog;
    public ImageButton btnUpdateInfoUp, btnUpdateInfoDown;
    public Button btnDone;
    private WheelPicker wheelCenter;
    private Integer gotoBtnItemIndex;
    private String data, strName, strSchool, strClass, strStudId;
    private boolean isFill;
    public OutDlg outDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfor);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        setIdentifyandFaceState();
        showBottomSheetDialog();
        collectData();
        isFill = isFillData();
        if (isFill){
            showConfirmDlg();
        }
    }


    private void initUI(){

        imgUpdateInfoBack = (ImageView)findViewById(R.id.imgUpdateInfoBack);
        tvUpdateInfoIdentify = (TextView)findViewById(R.id.tvUpdateInfoIdentify);
        tvUpdateInfoFace = (TextView)findViewById(R.id.tvUpdateInfoFace);
        tvUpdateInfoClass = (TextView)findViewById(R.id.tvUpdateInfoClass);
        edUpdateInfoSchool = (EditText)findViewById(R.id.edUpdateInfoSchool);
        edUpdateInfoName = (EditText) findViewById(R.id.edUpdateName);
        edUpdateInfoStudId = (EditText)findViewById(R.id.edUpdateInfoStudid);
        tvUpdateInfoComplete = (TextView)findViewById(R.id.tvUpdateInfoComplete);
        tvUpdateInfoComplete.setClickable(false);
    }


    private void showBottomSheetDialog(){

        tvUpdateInfoClass.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                openDlg();
            }
        });
    }


    private void showConfirmDlg(){

        tvUpdateInfoComplete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                outDlg = new OutDlg(UpdateinforActivity.this);
                TextView tvTitle = (TextView)outDlg.findViewById(R.id.tvOutTitle);
                TextView tvCancel = (TextView)outDlg.findViewById(R.id.tvOutCancel);
                TextView tvConfirm = (TextView)outDlg.findViewById(R.id.tvOutConfirm);

                tvTitle.setText(getResources().getString(R.string.whether_upate_info));
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        outDlg.dismiss();
                    }
                });

                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                    }
                });
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


    private void setIdentifyandFaceState(){

        if (Global.identify.equals("1")){
            tvUpdateInfoIdentify.setText(getResources().getString(R.string.already_identify));
            tvUpdateInfoIdentify.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if (Global.faceState.equals("1")){
            tvUpdateInfoFace.setText(getResources().getString(R.string.already_identify));
            tvUpdateInfoFace.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }



    private void upAnddownPicker(){

        btnUpdateInfoUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
//                btnUpdateInfoUp.setImageDrawable(getResources().getDrawable(R.drawable.im));
            }
        });
    }


    private void collectData(){

        strName = edUpdateInfoName.getText().toString();
        strSchool = edUpdateInfoSchool.getText().toString();
        strClass = tvUpdateInfoClass.getText().toString();
        strStudId = edUpdateInfoStudId.getText().toString();
    }


    private boolean isFillData(){

        if (tvUpdateInfoIdentify.getText().equals(getResources().getString(R.string.already_identify)) &&
                tvUpdateInfoFace.getText().equals(getResources().getString(R.string.already_identify))) {

            if (!strName.equals("") && strSchool.equals("") && strClass.equals("") && strStudId.equals("")) {
                tvUpdateInfoComplete.setTextColor(getResources().getColor(android.R.color.black));
                tvUpdateInfoComplete.setClickable(true);
                return true;
            }
        }

        else {
            tvUpdateInfoComplete.setTextColor(getResources().getColor(R.color.default_shadow_color));
            tvUpdateInfoComplete.setClickable(false);
        }
        return false;
    }


    private void updatePersonalInfo(){

        collectData();

    }

}
