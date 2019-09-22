package ours.china.hours.Activity.Personality;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Auth.RegisterActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    private ImageView imgFeedBack;
    private EditText edFeedbakck;
    private Button btnFeedbackSubmit;
    public TextView tvOutDlgConfirm;

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        gotoBack();
        showDialog();
    }


    private void initUI(){
        imgFeedBack = (ImageView)findViewById(R.id.imgFeedBack);
        edFeedbakck = (EditText)findViewById(R.id.edFeedback);
        btnFeedbackSubmit = (Button)findViewById(R.id.btnFeedbackSubmit);
    }


    private void gotoBack(){
        imgFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    FeedbackActivity.this.finish();
                } else {
                    FeedbackActivity.super.onBackPressed();
                }
            }
        });
    }


    private void showDialog(){
        btnFeedbackSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                FeedbackDialog();
            }
        });
    }


    private void FeedbackDialog(){

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_feedback, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = alertDialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        tvOutDlgConfirm = (TextView)dialogView.findViewById(R.id.tvOutFeedConfirm);
        tvOutDlgConfirm.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                alertDialog.dismiss();
                feedbackProcess();
            }
        });
        alertDialog.show();
    }


    private void feedbackProcess(){

        final String feedBack = edFeedbakck.getText().toString();
        final String access_token = Global.access_token;
        final String mobile = Global.mobile;

        if (feedBack.equals("")){
            Global.alert(FeedbackActivity.this, getResources().getString(R.string.feedback), getResources().getString(R.string.feedback_write), getResources().getString(R.string.confirm));
            edFeedbakck.requestFocus();
            return;
        }
        Global.showLoading(FeedbackActivity.this,"generate_report");
        Ion.with(FeedbackActivity.this)
                .load(Url.send_feedback)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("content", feedBack)
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
                                    edFeedbakck.setText("");
                                    Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.login_verify_code), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(FeedbackActivity.this, resObj.getString("err_msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(FeedbackActivity.this, "验证码成功失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
