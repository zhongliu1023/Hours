package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Auth.FaceRegisterActivity;
import ours.china.hours.Activity.Auth.IdentifyActivity;
import ours.china.hours.Activity.Auth.LoginOptionActivity;
import ours.china.hours.Activity.Auth.PerfectInforActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Dialog.OutDlg;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.User;
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

    SharedPreferencesManager sessionManager;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfor);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        setIdentifyandFaceState();
        showBottomSheetDialog();
        collectData();
        showConfirmDlg();

        isFill = isFillData();

        initListener();
    }


    private void initUI(){

        sessionManager = new SharedPreferencesManager(this);
        currentUser = UsersManagement.getCurrentUser(sessionManager);

        imgUpdateInfoBack = (ImageView)findViewById(R.id.imgUpdateInfoBack);
        tvUpdateInfoIdentify = (TextView)findViewById(R.id.tvUpdateInfoIdentify);
        tvUpdateInfoFace = (TextView)findViewById(R.id.tvUpdateInfoFace);
        tvUpdateInfoClass = (TextView)findViewById(R.id.tvUpdateInfoClass);
        edUpdateInfoSchool = (EditText)findViewById(R.id.edUpdateInfoSchool);
        edUpdateInfoName = (EditText) findViewById(R.id.edUpdateName);
        edUpdateInfoStudId = (EditText)findViewById(R.id.edUpdateInfoStudid);
        tvUpdateInfoComplete = (TextView)findViewById(R.id.tvUpdateInfoComplete);
        tvUpdateInfoComplete.setClickable(false);

        edUpdateInfoName.setText(currentUser.name);
        edUpdateInfoStudId.setText(currentUser.studId);
        tvUpdateInfoClass.setText(currentUser.className);
        edUpdateInfoSchool.setText(currentUser.school);

        edUpdateInfoSchool.addTextChangedListener(textWatcher);
        edUpdateInfoName.addTextChangedListener(textWatcher);
        edUpdateInfoStudId.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isFill = isFillData();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    void initListener(){
        imgUpdateInfoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvUpdateInfoIdentify.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(UpdateinforActivity.this, IdentifyActivity.class);
                startActivity(intent);
            }
        });

        tvUpdateInfoFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateinforActivity.this, FaceRegisterActivity.class);
                startActivity(intent);
            }
        });
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

                new AlertView.Builder().setContext(UpdateinforActivity.this).setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.update_information))
                        .setDestructive(getString(R.string.cancel))
                        .setOthers(new String[]{getString(R.string.confirm)})
                        .setStyle(AlertView.Style.Alert).setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 1){
                            register();
                        }
                    }
                }).build().show();
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
                isFill = isFillData();
                closeDlg();
            }
        });
    }


    private void setIdentifyandFaceState(){

        if (currentUser.idCardFront != ""){
            tvUpdateInfoIdentify.setText(getResources().getString(R.string.already_identify));
            tvUpdateInfoIdentify.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        if (currentUser.faceInfoUrl != ""){
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

        collectData();
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
    public void register(){
        if (strName.equals("")){
            Global.alert(UpdateinforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.information_name), getResources().getString(R.string.confirm));
            edUpdateInfoName.requestFocus();
            return;
        }

        if (strSchool.equals("")){
            Global.alert(UpdateinforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.school), getResources().getString(R.string.confirm));
            edUpdateInfoSchool.requestFocus();
            return;
        }

        if (strClass.equals("")){
            Global.alert(UpdateinforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.information_class), getResources().getString(R.string.confirm));
            tvUpdateInfoClass.requestFocus();
            return;
        }

        if (strStudId.equals("")){
            Global.alert(UpdateinforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.studId), getResources().getString(R.string.confirm));
            edUpdateInfoStudId.requestFocus();
            return;
        }
        Global.showLoading(UpdateinforActivity.this,"generate_report");
        Ion.with(UpdateinforActivity.this)
                .load(Url.update_profile)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .setBodyParameter("name", strName)
                .setBodyParameter("school", strSchool)
                .setBodyParameter("class", strClass)
                .setBodyParameter("studId", strStudId)
                .setBodyParameter("nickName", "")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Global.hideLoading();
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {
                                    currentUser.school = strSchool;
                                    currentUser.className = strClass;
                                    currentUser.name = strName;
                                    currentUser.studId = strStudId;
                                    UsersManagement.saveCurrentUser(currentUser, sessionManager);
                                    finish();
                                } else {
                                    Toast.makeText(UpdateinforActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(UpdateinforActivity.this, "正确注册你的脸", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
