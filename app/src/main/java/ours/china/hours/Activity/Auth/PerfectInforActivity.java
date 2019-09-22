package ours.china.hours.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.R;

public class PerfectInforActivity extends AppCompatActivity{

    private EditText edPerfectName, edPerfectSchool, edPerfectStudId;
    private TextView tvPerfectIdentify, tvPerfectFace, tvPerfectClass;
    private Button btnPerfectSubmit, btnPerfectDone;
    private ImageButton btnDown, btnUp;
    private ImageView imgPerfectBack;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private WheelPicker wheelCenter;
    private Integer gotoBtnItemIndex;
    private String data;

    private BottomSheetDialog bottomSheetDialog;
    public ImageButton btnUpdateInfoUp, btnUpdateInfoDown;

    public Button btnDone;

    public APIInterface apiInterface;

    // for face engine
    private static final String TAG = "LoginOptionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_infor);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
        gotoIdentify();
        gotoRegister();
        goFaceRegister();
        showBottomSheetDialog();


    }


    private void initUI(){

        edPerfectName = (EditText)findViewById(R.id.edPerfectName);
        tvPerfectIdentify = (TextView)findViewById(R.id.tvPerfectidentify);
        tvPerfectFace = (TextView)findViewById(R.id.tvPerfectface);
        edPerfectSchool = (EditText)findViewById(R.id.edPerfectSchool);
        tvPerfectClass = (TextView)findViewById(R.id.tvPerfectClass);
        edPerfectStudId = (EditText) findViewById(R.id.edPerfectStudId);
        btnPerfectSubmit = (Button)findViewById(R.id.btnPerfectSubmit);
        bottom_sheet = (LinearLayout)findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        wheelCenter = (WheelPicker)findViewById(R.id.main_wheel_center);
        btnPerfectDone = (Button)findViewById(R.id.btnPerfectDone);
        btnDown = (ImageButton)findViewById(R.id.btnPerfectDown);
        btnUp = (ImageButton)findViewById(R.id.btnPerfectUp);
        imgPerfectBack = (ImageView)findViewById(R.id.imgPerfectBack);
    }


    private void gotoBack(){
        imgPerfectBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    PerfectInforActivity.this.finish();
                } else {
                    PerfectInforActivity.super.onBackPressed();
                }
            }
        });
    }


    private void gotoRegister(){
        btnPerfectSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                register();
            }
        });
    }


    private void gotoIdentify(){

        tvPerfectIdentify.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(PerfectInforActivity.this, IdentifyActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goFaceRegister() {
        tvPerfectFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfectInforActivity.this, FaceRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void register(){
        String name = edPerfectName.getText().toString();
        String identyStatus = "";

        if (!Global.identify.equals(getResources().getString(R.string.identify_success))){
            Toast.makeText(this, "请正确输入您的ID。", Toast.LENGTH_SHORT).show();
        }

        if (!Global.faceState.equals(getResources().getString(R.string.identify_success))) {
            Toast.makeText(this, "正确注册你的脸", Toast.LENGTH_SHORT).show();
        }

        String school = edPerfectSchool.getText().toString();
        String classs = tvPerfectClass.getText().toString();
        String studId = edPerfectStudId.getText().toString();
        String mobile = Global.mobile;

        if (name.equals("")){
            Global.alert(PerfectInforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.information_name), getResources().getString(R.string.confirm));
            edPerfectName.requestFocus();
            return;
        }

        if (school.equals("")){
            Global.alert(PerfectInforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.school), getResources().getString(R.string.confirm));
            edPerfectSchool.requestFocus();
            return;
        }

        if (classs.equals("")){
            Global.alert(PerfectInforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.studId), getResources().getString(R.string.confirm));
            edPerfectStudId.requestFocus();
            return;
        }

        Log.i(TAG, "name => " + name);
        Log.i(TAG, "mobile => " + mobile);
        Log.i(TAG, "school => " + school);
        Log.i(TAG, "class => " + classs);
        Log.i(TAG, "studId => " + studId);

        Global.showLoading(PerfectInforActivity.this,"generate_report");
        Ion.with(PerfectInforActivity.this)
                .load(Url.update_profile)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .setBodyParameter("name", name)
                .setBodyParameter("school", school)
                .setBodyParameter("class", classs)
                .setBodyParameter("studId", studId)
                .setBodyParameter("nickName", "")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Global.hideLoading();
                        Log.i(TAG, "result => " + result);

                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {

                                    Intent intent = new Intent(PerfectInforActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(PerfectInforActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(PerfectInforActivity.this, "正确注册你的脸", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void upAnddownPicker(){

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                btnUp.setImageDrawable(getResources().getDrawable(R.drawable.image_up));
                btnDown.setImageDrawable(getResources().getDrawable(R.drawable.image_down_shadow));

                gotoBtnItemIndex = wheelCenter.getCurrentItemPosition();
                wheelCenter.setSelectedItemPosition(gotoBtnItemIndex - 1);
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                btnDown.setImageDrawable(getResources().getDrawable(R.drawable.image_down));
                btnUp.setImageDrawable(getResources().getDrawable(R.drawable.image_up_shadow));
                gotoBtnItemIndex = wheelCenter.getCurrentItemPosition();
                wheelCenter.setSelectedItemPosition(gotoBtnItemIndex + 1);
            }
        });
    }




    private void openDlg() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_updateinfo, null);
        bottomSheetDialog  = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        btnUpdateInfoUp = (ImageButton) view.findViewById(R.id.btnUpdateInfoUp);
        btnUpdateInfoDown = (ImageButton) view.findViewById(R.id.btnUpdateInfoDown);

        btnDone = (Button) view.findViewById(R.id.btnUpdateDone);
        this.wheelCenter = (WheelPicker) view.findViewById(R.id.wheelUpdateInfo);
        setClassName();

        btnUpdateInfoUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoBtnItemIndex = wheelCenter.getCurrentItemPosition();
                wheelCenter.setSelectedItemPosition(gotoBtnItemIndex - 1);
                btnUpdateInfoUp.setImageDrawable(getResources().getDrawable(R.drawable.image_up));
                btnUpdateInfoDown.setImageDrawable(getResources().getDrawable(R.drawable.image_down_shadow));
            }
        });

        btnUpdateInfoDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoBtnItemIndex = wheelCenter.getCurrentItemPosition();
                wheelCenter.setSelectedItemPosition(gotoBtnItemIndex + 1);
                btnUpdateInfoDown.setImageDrawable(getResources().getDrawable(R.drawable.image_down));
                btnUpdateInfoUp.setImageDrawable(getResources().getDrawable(R.drawable.image_up_shadow));
            }
        });

        bottomSheetDialog.show();
    }

    private void closeDlg() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
            bottomSheetDialog = null;
        }
    }

    private void setClassName() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data = wheelCenter.getData().get(wheelCenter.getCurrentItemPosition()).toString();
                tvPerfectClass.setText(data);
                closeDlg();
            }
        });
    }


    private void showBottomSheetDialog() {
        tvPerfectClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openDlg();
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        if (Global.identify.equals(getResources().getString(R.string.identify_success))) {
            tvPerfectIdentify.setText(Global.identify);
        }
        if (Global.faceState.equals(getResources().getString(R.string.identify_success))) {
            tvPerfectFace.setText(Global.faceState);
        }
    }

}
