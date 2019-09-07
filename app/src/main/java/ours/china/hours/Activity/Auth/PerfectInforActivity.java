package ours.china.hours.Activity.Auth;

import androidx.appcompat.app.AppCompatActivity;

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

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Model.Confirm;
import ours.china.hours.Model.Register;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfectInforActivity extends AppCompatActivity implements WheelPicker.OnItemSelectedListener, View.OnClickListener{

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

    public APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_infor);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        wheelCenter.setOnItemSelectedListener(this);

        if (Global.identify.equals(getResources().getString(R.string.identify_success))){
            tvPerfectIdentify.setText(Global.identify.toString());
        }

        gotoBack();
        gotoIdentify();
        gotoRegister();
        showClassName();
        upAnddownPicker();
        setClassName();

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


    public void register(){
        String name = edPerfectName.getText().toString();
        String identyStatus = "";
        if (Global.identify.equals(getResources().getString(R.string.identify_success))){
             identyStatus = "1";
        }
        String faceState = "1";
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

        if (school.equals("")){
            Global.alert(PerfectInforActivity.this, getResources().getString(R.string.perfect_info), getResources().getString(R.string.studId), getResources().getString(R.string.confirm));
            edPerfectStudId.requestFocus();
            return;
        }

        try {
            Global.showLoading(PerfectInforActivity.this,"generate_report");
            Call<Register> call = apiInterface.register(name, mobile, identyStatus, faceState,
                                    school, classs,studId);
            call.enqueue(new Callback<Register>() {
                @Override public void onResponse(Call<Register> call, Response<Register> response) {
                    Global.hideLoading();

                    if (response.code() == 404){
                        Toast.makeText(PerfectInforActivity.this, "404", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 422){
                        Toast.makeText(PerfectInforActivity.this, "422", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 500){
                        Toast.makeText(PerfectInforActivity.this, "500", Toast.LENGTH_SHORT).show();
                    }else if (response.code() == 200){
                        String res = response.body().res;
                        Toast.makeText(PerfectInforActivity.this, res, Toast.LENGTH_SHORT).show();
                        if (res.equals("success")){
                              Intent intent = new Intent(PerfectInforActivity.this, MainActivity.class);
                              startActivity(intent);
                        }else if (res.equals("fail")){
                            Toast.makeText(PerfectInforActivity.this, "fail1", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override public void onFailure(Call<Register> call, Throwable t) {
                    Log.i("PerfectActivity", t.getMessage());
                }
            });

        }
        catch (Exception e) {
            Log.i("PerfectActivity", e.getMessage());
        }
    }


    private void showClassName(){

        tvPerfectClass.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                         bottom_sheet.setVisibility(View.VISIBLE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        btnPerfectSubmit.setVisibility(View.GONE);

                    } else {
                        bottom_sheet.setVisibility(View.INVISIBLE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        btnPerfectSubmit.setVisibility(View.VISIBLE);
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


    private void setClassName(){

      btnPerfectDone.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
              data = wheelCenter.getData().get(wheelCenter.getCurrentItemPosition()).toString();
              tvPerfectClass.setText(data.toString());
              tvPerfectClass.setTextColor(getResources().getColor(android.R.color.black));
              Log.i("PerfectInfoActivity", data);
              if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                  sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                  btnPerfectSubmit.setVisibility(View.VISIBLE);
                  bottom_sheet.setVisibility(View.INVISIBLE);

              }
          }
      });
    }


    @Override public void onClick(View view) {


    }


    @Override public void onItemSelected(WheelPicker picker, Object data, int position) {
        String text = "";
        switch (picker.getId()) {
            case R.id.main_wheel_center:
                text = "Center:";
                break;
        }
    }
}
